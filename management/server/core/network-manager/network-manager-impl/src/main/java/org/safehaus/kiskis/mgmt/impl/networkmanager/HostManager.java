package org.safehaus.kiskis.mgmt.impl.networkmanager;

import org.safehaus.kiskis.mgmt.api.commandrunner.Command;
import org.safehaus.kiskis.mgmt.shared.protocol.Agent;

import java.util.List;

/**
 * Created by daralbaev on 04.04.14.
 */
public class HostManager {
    private List<Agent> agentList;
    private String domainName;

    public HostManager(List<Agent> agentList, String domainName) {
        this.agentList = agentList;
        this.domainName = domainName;
    }

    public boolean execute() {
        if (agentList != null && !agentList.isEmpty()) {
            return write();
        }

        return false;
    }

    public boolean execute(Agent agent) {
        if (agentList != null && !agentList.isEmpty() && agent != null) {
            agentList.add(agent);
            return write();
        }

        return false;
    }

    private boolean write() {
        String hosts = prepareHost();
        Command command = Commands.getWriteHostsCommand(agentList, hosts);
        NetwokManagerImpl.getCommandRunner().runCommand(command);

        return command.hasSucceeded();
    }

    private String prepareHost() {
        StringBuilder value = new StringBuilder();

        for (Agent agent : agentList) {
            value.append(agent.getListIP().get(0));
            value.append("\t");
            value.append(agent.getHostname());
            value.append(".");
            value.append(domainName);
            value.append("\t");
            value.append(agent.getHostname());
            value.append("\n");
        }
        value.append("127.0.0.1\tlocalhost");

        return value.toString();
    }
}
