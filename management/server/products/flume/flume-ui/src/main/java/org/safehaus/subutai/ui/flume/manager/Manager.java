package org.safehaus.subutai.ui.flume.manager;

import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import org.safehaus.subutai.api.flume.Config;
import org.safehaus.subutai.server.ui.component.ConfirmationDialog;
import org.safehaus.subutai.server.ui.component.ProgressWindow;
import org.safehaus.subutai.server.ui.component.TerminalWindow;
import org.safehaus.subutai.shared.operation.ProductOperationState;
import org.safehaus.subutai.shared.operation.ProductOperationView;
import org.safehaus.subutai.shared.protocol.Agent;
import org.safehaus.subutai.shared.protocol.Util;
import org.safehaus.subutai.ui.flume.FlumeUI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Manager {

    private final VerticalLayout contentRoot;
    private final ComboBox clusterCombo;
    private final Table nodesTable;
    private Config config;

    public Manager() {

        contentRoot = new VerticalLayout();
        contentRoot.setSpacing(true);
        contentRoot.setWidth(90, Sizeable.Unit.PERCENTAGE);
        contentRoot.setHeight(100, Sizeable.Unit.PERCENTAGE);

        VerticalLayout content = new VerticalLayout();
        content.setWidth(100, Sizeable.Unit.PERCENTAGE);
        content.setHeight(100, Sizeable.Unit.PERCENTAGE);

        contentRoot.addComponent(content);
        contentRoot.setComponentAlignment(content, Alignment.TOP_CENTER);
        contentRoot.setMargin(true);

        //tables go here
        nodesTable = createTableTemplate("Nodes", 200);
        //tables go here

        HorizontalLayout controlsContent = new HorizontalLayout();
        controlsContent.setSpacing(true);

        Label clusterNameLabel = new Label("Select the cluster");
        controlsContent.addComponent(clusterNameLabel);

        clusterCombo = new ComboBox();
        clusterCombo.setImmediate(true);
        clusterCombo.setTextInputAllowed(false);
        clusterCombo.setWidth(200, Sizeable.Unit.PIXELS);
        clusterCombo.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                config = (Config) event.getProperty().getValue();
                refreshUI();
            }
        });

        controlsContent.addComponent(clusterCombo);

        Button refreshClustersBtn = new Button("Refresh clusters");
        refreshClustersBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                refreshClustersInfo();
            }
        });

        controlsContent.addComponent(refreshClustersBtn);

        Button destroyClusterBtn = new Button("Destroy cluster");
        destroyClusterBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (config != null) {
                    ConfirmationDialog alert = new ConfirmationDialog(String.format("Do you want to add node to the %s cluster?", config.getClusterName()),
                            "Yes", "No");
                    alert.getOk().addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            UUID trackID = FlumeUI.getManager().uninstallCluster(config.getClusterName());
                            ProgressWindow window = new ProgressWindow(FlumeUI.getExecutor(), FlumeUI.getTracker(), trackID, Config.PRODUCT_KEY);
                            window.getWindow().addCloseListener(new Window.CloseListener() {
                                @Override
                                public void windowClose(Window.CloseEvent closeEvent) {
                                    refreshClustersInfo();
                                }
                            });
                            contentRoot.getUI().addWindow(window.getWindow());
                        }
                    });

                    contentRoot.getUI().addWindow(alert.getAlert());
                } else {
                    show("Please, select cluster");
                }
            }
        });

        controlsContent.addComponent(destroyClusterBtn);

        Button addNodeBtn = new Button("Add Node");
        addNodeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (config != null) {
                    org.safehaus.subutai.api.hadoop.Config info = FlumeUI.getHadoopManager().getCluster(config.getClusterName());
                    if (info != null) {
                        Set<Agent> nodes = new HashSet<Agent>(info.getAllNodes());
                        nodes.removeAll(config.getNodes());
                        if (!nodes.isEmpty()) {
                            AddNodeWindow addNodeWindow = new AddNodeWindow(config, nodes);
                            contentRoot.getUI().addWindow(addNodeWindow);
                            addNodeWindow.addCloseListener(new Window.CloseListener() {
                                @Override
                                public void windowClose(Window.CloseEvent closeEvent) {
                                    refreshClustersInfo();
                                }
                            });
                        } else {
                            show("All nodes in corresponding Hadoop cluster have Flume installed");
                        }
                    } else {
                        show("Hadoop cluster info not found");
                    }
                } else {
                    show("Please, select cluster");
                }
            }
        });

        controlsContent.addComponent(addNodeBtn);

        content.addComponent(controlsContent);
        content.addComponent(nodesTable);

    }

    public Component getContent() {
        return contentRoot;
    }

    private void show(String notification) {
        Notification.show(notification);
    }

    private void populateTable(final Table table, Set<Agent> agents) {

        table.removeAllItems();

        for (final Agent agent : agents) {
            final Button destroyBtn = new Button("Destroy");
            final Button startBtn = new Button("Start");
            final Button stopBtn = new Button("Stop");
            stopBtn.setEnabled(true);
            startBtn.setEnabled(true);

            table.addItem(new Object[]{agent.getHostname(),
                    startBtn, stopBtn, destroyBtn}, null);

            startBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    startBtn.setEnabled(false);
                    stopBtn.setEnabled(false);
                    destroyBtn.setEnabled(false);

                    final UUID trackID = FlumeUI.getManager().startNode(
                            config.getClusterName(), agent.getHostname());
                    ProgressWindow window = new ProgressWindow(FlumeUI.getExecutor(), FlumeUI.getTracker(), trackID, Config.PRODUCT_KEY);
                    window.getWindow().addCloseListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent closeEvent) {
                            ProductOperationView po = FlumeUI.getTracker()
                                    .getProductOperation(Config.PRODUCT_KEY, trackID);
                            if (po.getState() == ProductOperationState.SUCCEEDED) {
                                stopBtn.setEnabled(true);
                            } else {
                                startBtn.setEnabled(true);
                            }
                            destroyBtn.setEnabled(true);
                        }
                    });
                    contentRoot.getUI().addWindow(window.getWindow());
                }
            });

            stopBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    startBtn.setEnabled(false);
                    stopBtn.setEnabled(false);
                    destroyBtn.setEnabled(false);

                    final UUID trackID = FlumeUI.getManager().stopNode(
                            config.getClusterName(), agent.getHostname());

                    ProgressWindow window = new ProgressWindow(FlumeUI.getExecutor(), FlumeUI.getTracker(), trackID, Config.PRODUCT_KEY);
                    window.getWindow().addCloseListener(new Window.CloseListener() {
                        @Override
                        public void windowClose(Window.CloseEvent closeEvent) {
                            ProductOperationView po = FlumeUI.getTracker()
                                    .getProductOperation(Config.PRODUCT_KEY, trackID);
                            if (po.getState() == ProductOperationState.SUCCEEDED) {
                                startBtn.setEnabled(true);
                            } else {
                                stopBtn.setEnabled(true);
                            }
                            destroyBtn.setEnabled(true);
                        }
                    });
                    contentRoot.getUI().addWindow(window.getWindow());
                }
            });

            destroyBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    ConfirmationDialog alert = new ConfirmationDialog(String.format("Do you want to add node to the %s node?", agent.getHostname()),
                            "Yes", "No");
                    alert.getOk().addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            UUID trackID = FlumeUI.getManager().destroyNode(config.getClusterName(), agent.getHostname());
                            ProgressWindow window = new ProgressWindow(FlumeUI.getExecutor(), FlumeUI.getTracker(), trackID, Config.PRODUCT_KEY);
                            window.getWindow().addCloseListener(new Window.CloseListener() {
                                @Override
                                public void windowClose(Window.CloseEvent closeEvent) {
                                    refreshClustersInfo();
                                }
                            });
                            contentRoot.getUI().addWindow(window.getWindow());
                        }
                    });

                    contentRoot.getUI().addWindow(alert.getAlert());
                }
            });
        }
    }

    private void refreshUI() {
        if (config != null) {
            populateTable(nodesTable, config.getNodes());
        } else {
            nodesTable.removeAllItems();
        }
    }

    public void refreshClustersInfo() {
        List<Config> clustersInfo = FlumeUI.getManager().getClusters();
        Config clusterInfo = (Config) clusterCombo.getValue();
        clusterCombo.removeAllItems();
        if (clustersInfo != null && clustersInfo.size() > 0) {
            for (Config ci : clustersInfo) {
                clusterCombo.addItem(ci);
                clusterCombo.setItemCaption(ci, ci.getClusterName());
            }
            if (clusterInfo != null) {
                for (Config ci : clustersInfo) {
                    if (ci.getClusterName().equals(clusterInfo.getClusterName())) {
                        clusterCombo.setValue(ci);
                        return;
                    }
                }
            } else {
                clusterCombo.setValue(clustersInfo.iterator().next());
            }
        }
    }

    private Table createTableTemplate(String caption, int size) {
        final Table table = new Table(caption);
        table.addContainerProperty("Host", String.class, null);
        table.addContainerProperty("Start", Button.class, null);
        table.addContainerProperty("Stop", Button.class, null);
        table.addContainerProperty("Destroy", Button.class, null);
        table.setWidth(100, Sizeable.Unit.PERCENTAGE);
        table.setHeight(size, Sizeable.Unit.PIXELS);
        table.setPageLength(10);
        table.setSelectable(false);
        table.setImmediate(true);

        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    String lxcHostname = (String) table.getItem(event.getItemId()).getItemProperty("Host").getValue();
                    Agent lxcAgent = FlumeUI.getAgentManager().getAgentByHostname(lxcHostname);
                    if (lxcAgent != null) {
                        TerminalWindow terminal = new TerminalWindow(Util.wrapAgentToSet(lxcAgent), FlumeUI.getExecutor(), FlumeUI.getCommandRunner(), FlumeUI.getAgentManager());
                        contentRoot.getUI().addWindow(terminal.getWindow());
                    } else {
                        show("Agent is not connected");
                    }
                }
            }
        });
        return table;
    }

}
