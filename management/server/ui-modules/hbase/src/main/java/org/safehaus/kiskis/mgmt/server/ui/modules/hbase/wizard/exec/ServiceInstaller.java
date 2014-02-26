/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.safehaus.kiskis.mgmt.server.ui.modules.hbase.wizard.exec;

import com.vaadin.ui.TextArea;
import org.safehaus.kiskis.mgmt.server.ui.modules.hbase.HBaseConfig;
import org.safehaus.kiskis.mgmt.shared.protocol.*;
import org.safehaus.kiskis.mgmt.shared.protocol.enums.TaskStatus;
import java.util.LinkedList;
import java.util.Queue;
import org.safehaus.kiskis.mgmt.api.taskrunner.TaskCallback;
import org.safehaus.kiskis.mgmt.api.taskrunner.TaskRunner;
import org.safehaus.kiskis.mgmt.server.ui.modules.hbase.HBaseDAO;
import org.safehaus.kiskis.mgmt.server.ui.modules.hbase.commands.HBaseCommands;
import org.safehaus.kiskis.mgmt.server.ui.modules.hbase.management.HBaseCommandEnum;
import org.safehaus.kiskis.mgmt.server.ui.modules.hbase.wizard.Wizard;

/**
 *
 * @author bahadyr
 */
public class ServiceInstaller {

    private final Queue<Task> tasks = new LinkedList<Task>();
    private final TextArea terminal;
    private Task currentTask;
    HBaseConfig config;
    private TaskRunner asyncTaskRunner;

    public ServiceInstaller(Wizard wizard, TextArea terminal) {
        this.terminal = terminal;
        this.config = wizard.getConfig();
        this.asyncTaskRunner = wizard.getRunner();

        Task updateApt = new Task("apt-get update");
        for (Agent agent : config.getAgents()) {
            Request command = HBaseCommands.getAptGetUpdate();
            command.setUuid(agent.getUuid());
            updateApt.addRequest(command);
        }
        tasks.add(updateApt);

        Task installTask = new Task("Install HBase");
        for (Agent agent : config.getAgents()) {
            Request command = new HBaseCommands().getCommand(HBaseCommandEnum.INSTALL);
            command.setUuid(agent.getUuid());
            installTask.addRequest(command);
        }
        tasks.add(installTask);

        StringBuilder masterSB = new StringBuilder();
        for (Agent agent : config.getMaster()) {
            masterSB.append(agent.getHostname()).append(" ").append(agent.getHostname());
            break;
        }
        Task setMasterTask = new Task("Set master");
        for (Agent agent : config.getAgents()) {
            Request command = HBaseCommands.getSetMasterCommand(masterSB.toString());
            command.setUuid(agent.getUuid());
            setMasterTask.addRequest(command);
        }
        tasks.add(setMasterTask);

        StringBuilder regionSB = new StringBuilder();
        for (Agent agent : config.getRegion()) {
            regionSB.append(agent.getHostname()).append(" ");
        }
        Task setRegionTask = new Task("Set region");
        for (Agent agent : config.getAgents()) {
            Request command = HBaseCommands.getSetRegionCommand(regionSB.toString());
            command.setUuid(agent.getUuid());
            setRegionTask.addRequest(command);
        }
        tasks.add(setRegionTask);

        StringBuilder quorumSB = new StringBuilder();
        for (Agent agent : config.getQuorum()) {
            quorumSB.append(agent.getHostname()).append(" ");
        }
        Task setQuorumTask = new Task("Set quorum");
        for (Agent agent : config.getAgents()) {
            Request command = HBaseCommands.getSetQuorumCommand(quorumSB.toString());
            command.setUuid(agent.getUuid());
            setQuorumTask.addRequest(command);
        }
        tasks.add(setQuorumTask);

        StringBuilder backupSB = new StringBuilder();
        for (Agent agent : config.getBackupMasters()) {
            backupSB.append(agent.getHostname()).append(" ");
        }
        Task setBackupMastersTask = new Task("Set backup masters");
        for (Agent agent : config.getAgents()) {
            Request command = HBaseCommands.getSetBackupMastersCommand(backupSB.toString());
            command.setUuid(agent.getUuid());
            setBackupMastersTask.addRequest(command);
        }
        tasks.add(setBackupMastersTask);

    }

    public void start() {
        terminal.setValue("Starting installation...\n");
        moveToNextTask();
        if (currentTask != null) {
//            for (Request command : currentTask.getCommands()) {
//                executeCommand(command);
//            }

            asyncTaskRunner.executeTask(currentTask, new TaskCallback() {

                @Override
                public Task onResponse(Task task, Response response, String stdOut, String stdErr) {
//                    List<ParseResult> list = RequestUtil.parseTask(response.getTaskUuid(), true);
//                    Task task = RequestUtil.getTask(response.getTaskUuid());
                    if (task.isCompleted()) {
                        if (task.getTaskStatus() == TaskStatus.SUCCESS) {
                            terminal.setValue(terminal.getValue().toString() + task.getDescription() + " successfully finished.\n");
                            moveToNextTask();
                            if (currentTask != null) {
                                terminal.setValue(terminal.getValue().toString() + "Running next step " + currentTask.getDescription() + "\n");
//                                for (Request command : currentTask.getCommands()) {
//                                    executeCommand(command);
//                                }
                                return currentTask;
                            } else {
                                terminal.setValue(terminal.getValue().toString() + "Tasks complete.\n");
//                        saveInfo();
                                saveHBaseInfo();
                            }
                        } else if (task.getTaskStatus() == TaskStatus.FAIL) {
                            terminal.setValue(terminal.getValue().toString() + task.getDescription() + " failed\n");
                        }
                        terminal.setCursorPosition(terminal.getValue().toString().length());
                    }
                    return null;
                }
            });

        }
    }

    private void moveToNextTask() {
        currentTask = tasks.poll();
    }

//    public void onResponse(Response response) {
//        if (currentTask != null && response.getTaskUuid() != null
//                && currentTask.getUuid().compareTo(response.getTaskUuid()) == 0) {
//            List<ParseResult> list = RequestUtil.parseTask(response.getTaskUuid(), true);
//            Task task = RequestUtil.getTask(response.getTaskUuid());
//            if (!list.isEmpty() && terminal != null) {
//                if (task.getTaskStatus() == TaskStatus.SUCCESS) {
//                    terminal.setValue(terminal.getValue().toString() + task.getDescription() + " successfully finished.\n");
//                    moveToNextTask();
//                    if (currentTask != null) {
//                        terminal.setValue(terminal.getValue().toString() + "Running next step " + currentTask.getDescription() + "\n");
//                        for (Request command : currentTask.getCommands()) {
//                            executeCommand(command);
//                        }
//                    } else {
//                        terminal.setValue(terminal.getValue().toString() + "Tasks complete.\n");
////                        saveInfo();
//                        saveHBaseInfo();
//                    }
//                } else if (task.getTaskStatus() == TaskStatus.FAIL) {
//                    terminal.setValue(terminal.getValue().toString() + task.getDescription() + " failed\n");
//                }
//            }
//            terminal.setCursorPosition(terminal.getValue().toString().length());
//
//        }
//    }
//    private static final Logger LOG = Logger.getLogger(ServiceInstaller.class.getName());
//    private void saveInfo() {
//        HBaseClusterInfo info = new HBaseClusterInfo();
//        info.setDomainName(config.getDomainInfo());
//        info.setMaster(config.getMasterUUIDset());
//        info.setRegion(config.getRegionSet());
//        info.setQuorum(config.getQuorumSet());
//        info.setBmasters(config.getBackupMastersSet());
//        info.setAllnodes(config.getAgentsSet());
//
//        if (HBaseDAO.saveHBaseClusterInfo(info)) {
//            terminal.setValue(terminal.getValue().toString() + info.getUuid() + " cluster saved into keyspace.\n");
//        }
//    }
    private void saveHBaseInfo() {
        if (HBaseDAO.saveClusterInfo(config)) {
            terminal.setValue(terminal.getValue().toString() + config.getUuid() + " cluster saved into keyspace.\n");
        }
    }

//    private void executeCommand(Request command) {
//        terminal.setValue(terminal.getValue().toString() + command.getProgram() + "\n");
//        ServiceLocator.getService(CommandManager.class).executeCommand(command);
//    }
}
