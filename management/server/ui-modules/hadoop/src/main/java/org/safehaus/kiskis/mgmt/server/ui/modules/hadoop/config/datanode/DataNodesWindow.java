package org.safehaus.kiskis.mgmt.server.ui.modules.hadoop.config.datanode;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import org.safehaus.kiskis.mgmt.api.taskrunner.TaskCallback;
import org.safehaus.kiskis.mgmt.server.ui.modules.hadoop.HadoopClusterInfo;
import org.safehaus.kiskis.mgmt.server.ui.modules.hadoop.HadoopDAO;
import org.safehaus.kiskis.mgmt.server.ui.modules.hadoop.HadoopModule;
import org.safehaus.kiskis.mgmt.server.ui.modules.hadoop.common.Tasks;
import org.safehaus.kiskis.mgmt.server.ui.modules.hadoop.operation.DataNodeConfiguration;
import org.safehaus.kiskis.mgmt.shared.protocol.Agent;
import org.safehaus.kiskis.mgmt.shared.protocol.Response;
import org.safehaus.kiskis.mgmt.api.taskrunner.Task;

import java.util.regex.Pattern;

import static org.safehaus.kiskis.mgmt.api.taskrunner.TaskStatus.SUCCESS;

public final class DataNodesWindow extends Window {

    private Button startButton, stopButton, restartButton, addButton;
    private Label indicator, statusLabel;
    private DataNodesTable dataNodesTable;
    private AgentsComboBox agentsComboBox;
    private DataNodesWindow current;

    private final HadoopClusterInfo cluster;

    public DataNodesWindow(String clusterName) {
        this.current = this;
        setModal(true);
        setCaption("Hadoop Data Node Configuration");

        this.cluster = HadoopDAO.getHadoopClusterInfo(clusterName);
        getStatusLabel();
        getIndicator();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth(900, Sizeable.UNITS_PIXELS);
        verticalLayout.setHeight(450, Sizeable.UNITS_PIXELS);
        verticalLayout.setSpacing(true);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        buttonLayout.addComponent(getStartButton());
        buttonLayout.addComponent(getStopButton());
        buttonLayout.addComponent(getRestartButton());
        buttonLayout.addComponent(getStatusLabel());
        buttonLayout.addComponent(getIndicator());
        buttonLayout.addComponent(getAgentsComboBox());
        buttonLayout.addComponent(getAddButton());

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.addComponent(buttonLayout);
        panel.addComponent(getTable());

        verticalLayout.addComponent(panel);
        setContent(verticalLayout);

        getStatus();
    }

    private Button getStartButton() {
        startButton = new Button("Start");
        startButton.setEnabled(false);

        startButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                disableButtons(0);
                statusLabel.setValue("");
                indicator.setVisible(true);

                HadoopModule.getTaskRunner().executeTask(Tasks.getNameNodeCommand(cluster, "start"), new TaskCallback() {
                    @Override
                    public Task onResponse(Task task, Response response, String stdOut, String stdErr) {
                        if (task.isCompleted()) {
                            getStatus();
                        }

                        return null;
                    }
                });
            }
        });

        return startButton;
    }

    private Button getStopButton() {
        stopButton = new Button("Stop");
        stopButton.setEnabled(false);

        stopButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                disableButtons(0);
                statusLabel.setValue("");
                indicator.setVisible(true);

                HadoopModule.getTaskRunner().executeTask(Tasks.getNameNodeCommand(cluster, "stop"), new TaskCallback() {
                    @Override
                    public Task onResponse(Task task, Response response, String stdOut, String stdErr) {
                        if (task.isCompleted()) {
                            getStatus();
                        }

                        return null;
                    }
                });
            }
        });

        return stopButton;
    }

    private Button getRestartButton() {
        restartButton = new Button("Restart");
        restartButton.setEnabled(false);

        restartButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                restartCluster();
            }
        });

        return restartButton;
    }

    private Button getAddButton() {
        addButton = new Button("Add");
        addButton.setEnabled(false);
        addButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Agent agent = (Agent) agentsComboBox.getValue();
                DataNodeConfiguration.addNode(current, cluster.getClusterName(), agent);
            }
        });

        return addButton;
    }

    private AgentsComboBox getAgentsComboBox() {
        if (agentsComboBox == null) {
            agentsComboBox = new AgentsComboBox(cluster.getClusterName());
        }

        return agentsComboBox;
    }

    private void disableButtons(int status) {
        addButton.setEnabled(false);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        restartButton.setEnabled(false);

        if (status == 1) {
            stopButton.setEnabled(true);
            restartButton.setEnabled(true);
        }

        if (status == 2) {
            startButton.setEnabled(true);
            addButton.setEnabled(true);
        }
    }

    public void getStatus() {
        statusLabel.setValue("");
        indicator.setVisible(true);

        HadoopModule.getTaskRunner().executeTask(Tasks.getNameNodeCommand(cluster, "status"), new TaskCallback() {
            @Override
            public Task onResponse(Task task, Response response, String stdOut, String stdErr) {
                if (task.isCompleted()) {
                    if (task.getTaskStatus() == SUCCESS) {
                        String status = parseStatus(stdOut);
                        statusLabel.setValue(status);
                        if (status.trim().equalsIgnoreCase("Running")) {
                            disableButtons(1);
                        } else {
                            disableButtons(2);
                        }
                        indicator.setVisible(false);
                        dataNodesTable.refreshDataSource();
                    }
                }

                return null;
            }
        });
    }

    public void restartCluster() {
        disableButtons(0);
        statusLabel.setValue("");
        indicator.setVisible(true);

        HadoopModule.getTaskRunner().executeTask(Tasks.getNameNodeCommand(cluster, "restart"), new TaskCallback() {
            @Override
            public Task onResponse(Task task, Response response, String stdOut, String stdErr) {
                if (task.isCompleted()) {
                    getStatus();
                }

                return null;
            }
        });
        disableButtons(0);
    }

    private Label getStatusLabel() {
        if (statusLabel == null) {
            statusLabel = new Label();
            statusLabel.setValue("");
        }

        return statusLabel;
    }

    private Label getIndicator() {
        if (indicator == null) {
            indicator = new Label();
            indicator.setIcon(new ThemeResource("icons/indicator.gif"));
            indicator.setContentMode(Label.CONTENT_XHTML);
            indicator.setHeight(11, Sizeable.UNITS_PIXELS);
            indicator.setWidth(50, Sizeable.UNITS_PIXELS);
            indicator.setVisible(false);
        }

        return indicator;
    }

    private DataNodesTable getTable() {
        dataNodesTable = new DataNodesTable(this, cluster.getClusterName());

        return dataNodesTable;
    }

    private String parseStatus(String response) {
        String[] array = response.split("\n");

        for (String status : array) {
            if (status.contains("NameNode")) {
                return status.
                        replaceAll(Pattern.quote("!(SecondaryNameNode is not running on this machine)"), "").
                        replaceAll("NameNode is ", "");
            }
        }

        return "";
    }

    public void setLoading(boolean loading) {
        indicator.setVisible(loading);
    }
}
