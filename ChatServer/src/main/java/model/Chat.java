package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private static Logger log = LoggerFactory.getLogger(Chat.class);
    private User client;
    private User agent;
    private static FindAgentSystem findAgentSystem;
    private boolean waitAgent = false;
    private List<CommandContainer> bufferedMessage = new ArrayList<>(5);

    static {
        findAgentSystem = FindAgentSystem.getInstance();
    }

    private Chat() {
    }

    public Chat(User client, User agent) {
        this.client = client;
        this.agent = agent;
    }

    public Chat(User client) {
        this.client = client;
    }

    public User getClient() {
        return client;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent){
        if(this.agent!=null || agent==null) return;
        this.agent=agent;
        cancelWaitAgent();
    }

    public void agentLeave (){
        if(agent==null) return;
        client.getSocket().send(new CommandContainer("Server", null, AnswerCode.AGENT_LEAVE));
        if (findAgentSystem.findSystem(client)) {
            log.info("start conversation" + client.toString() + " " + agent.toString());
        } else {
            client.getSocket().send(new CommandContainer("Server",null,AnswerCode.AGENT_LEAVE_WAIT_NEW));
        }
        agent=null;
    }

    public boolean haveAgent() {
        if(agent==null) return false;
        return true;
    }

    public void destroyChat (){
        if(agent!=null) {
            agent.getSocket().send(new CommandContainer(client.getLogin(), null, AnswerCode.CLIENT_LEAVE));
            agent.delChat(this);
            if (findAgentSystem.findSystem(agent)) {
                log.info("start conversation" + client.toString() + " " + agent.toString());
            }
            agent = null;
        }
    }

    public boolean sendMessage(CommandContainer commandContainer) {
        if (commandContainer.getLogin().equals(client.getLogin()) && commandContainer.getRole()==client.getRole()) {
            if (agent == null) {
                if (waitAgent) {
                    bufferedMessage.add(commandContainer);
                    client.getSocket().send(new CommandContainer("Server",null,AnswerCode.FIRST_AGENT_ANSWER_YOU));
                }
                else {
                    if (findAgentSystem.findSystem(client)) {
                        agent.getSocket().send(commandContainer);
                    } else {
                        waitAgent=true;
                        bufferedMessage.add(commandContainer);
                        client.getSocket().send(new CommandContainer("Server",null,AnswerCode.NO_AGENT_WAIT));
                    }
                }
            } else agent.getSocket().send(commandContainer);
            return true;
        } else if (commandContainer.getLogin().equals(agent.getLogin()) && commandContainer.getRole()==agent.getRole()) {
            client.getSocket().send(commandContainer);
            return true;
        } else return false;
    }

    private void cancelWaitAgent() {
        if (agent == null) return;
        waitAgent = false;
        for (CommandContainer commandContainer : bufferedMessage)
            agent.getSocket().send(commandContainer);
        bufferedMessage.clear();
    }

}
