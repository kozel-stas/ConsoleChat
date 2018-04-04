package model;

import com.google.gson.annotations.Expose;
import model.SupportClasses.AnswerCode;
import model.SupportClasses.CommandContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Chat {
    private static Logger log = LoggerFactory.getLogger(Chat.class);
    private static FindAgentSystem findAgentSystem = FindAgentSystem.getInstance();
    private static ConcurrentMap<Long,Chat> chats = new ConcurrentHashMap<>() ;
    private static AtomicLong atomicLong = new AtomicLong();
    @Expose
    private long ID;
    @Expose
    private User client;
    @Expose
    private User agent;
    @Expose
    private boolean waitAgent = false;
    private List<CommandContainer> bufferedMessage = new ArrayList<>(5);

    private Chat() {
    }

    public Chat(User client, User agent) {
        ID = atomicLong.getAndIncrement();
        chats.put(ID,this);
        this.client = client;
        this.agent = agent;
    }

    public Chat(User client) {
        ID = atomicLong.getAndIncrement();
        chats.put(ID,this);
        this.client = client;
    }

    public long getID (){return ID; }

    public User getClient() {
        return client;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        if (this.agent != null || agent == null) return;
        this.agent = agent;
        cancelWaitAgent();
    }

    public void agentLeave() {
        if (agent == null) return;
        client.getSocket().send(new CommandContainer("Server", null, AnswerCode.AGENT_LEAVE));
        if (findAgentSystem.findSystem(client)) {
            log.info("start conversation" + client.toString() + " " + agent.toString());
        } else {
            client.getSocket().send(new CommandContainer("Server", null, AnswerCode.AGENT_LEAVE_WAIT_NEW));
        }
        agent = null;
    }

    public boolean haveAgent() {
        if (agent == null) return false;
        return true;
    }

    public void destroyChat() {
        chats.remove(ID);
        if (agent != null) {
            agent.getSocket().send(new CommandContainer(client.getLogin(), null, AnswerCode.CLIENT_LEAVE));
            agent.delChat(this);
            if (findAgentSystem.findSystem(agent)) {
                log.info("start conversation" + client.toString() + " " + agent.toString());
            }
            agent = null;
        }
    }

    public boolean sendMessage(CommandContainer commandContainer) {
        if (commandContainer.getLogin().equals(client.getLogin()) && commandContainer.getRole() == client.getRole()) {
            if (agent == null) {
                if (waitAgent) {
                    bufferedMessage.add(commandContainer);
                    client.getSocket().send(new CommandContainer("Server", null, AnswerCode.FIRST_AGENT_ANSWER_YOU));
                } else {
                    if (findAgentSystem.findSystem(client)) {
                        agent.getSocket().send(commandContainer);
                    } else {
                        waitAgent = true;
                        bufferedMessage.add(commandContainer);
                        client.getSocket().send(new CommandContainer("Server", null, AnswerCode.NO_AGENT_WAIT));
                    }
                }
            } else agent.getSocket().send(commandContainer);
            return true;
        } else if (commandContainer.getLogin().equals(agent.getLogin()) && commandContainer.getRole() == agent.getRole()) {
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

    public static Collection<Chat> getChats(){
        return chats.values();
    }

    public static Chat getInfoChat(long ID){
        return chats.get(ID);
    }
}
