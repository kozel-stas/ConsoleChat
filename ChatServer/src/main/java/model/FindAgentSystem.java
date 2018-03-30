package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FindAgentSystem {
    private static FindAgentSystem findAgentSystem = null;
    private static Logger log = LoggerFactory.getLogger(FindAgentSystem.class);
    private ConcurrentLinkedQueue<User> waitAgents = new ConcurrentLinkedQueue();
    private ConcurrentLinkedQueue<User> waitClient = new ConcurrentLinkedQueue();

    public static synchronized FindAgentSystem getInstance() {
        if (findAgentSystem == null) findAgentSystem = new FindAgentSystem();
        return findAgentSystem;
    }

    private FindAgentSystem() {
    }

    public boolean findSystem(User client) {
        if (client.getRole() == Role.AGENT) {
            if (waitClient.size() > 0) {
                User user = waitClient.poll();
                client.addChat(user.getChat());
                user.getSocket().send(new CommandContainer(client.getLogin(), client.getRole(), AnswerCode.NEW_AGENT));
                client.getSocket().send(new CommandContainer(user.getLogin(), user.getRole(), AnswerCode.NEW_CLIENT));
                user.getChat().setAgent(client);
                if (client.checkMaxSize() && !waitAgents.contains(client))
                    waitAgents.add(client);
                return true;
            } else {
                if (!waitAgents.contains(client))
                    waitAgents.add(client);
                return false;
            }
        } else {
            if (waitAgents.size() > 0) {
                User agent = waitAgents.poll();
                agent.addChat(client.getChat());
                client.getSocket().send(new CommandContainer(agent.getLogin(), agent.getRole(), AnswerCode.NEW_AGENT));
                agent.getSocket().send(new CommandContainer(client.getLogin(), client.getRole(), AnswerCode.NEW_CLIENT));
                client.getChat().setAgent(agent);
                if (agent.checkMaxSize() && !waitAgents.contains(agent))
                    waitAgents.add(agent);
                return true;
            } else {
                waitClient.add(client);
                return false;
            }
        }
    }

    public void remove(User user) {
        if (user.getRole() == Role.CLIENT) waitClient.remove(user);
        else waitAgents.remove(user);
    }

    public void clear() {
        waitAgents.clear();
        waitClient.clear();
    }

}
