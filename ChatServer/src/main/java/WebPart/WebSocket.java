package WebPart;

import com.google.gson.Gson;
import model.ChatInterface;
import model.FindAgentSystem;
import model.CommandContainer;
import model.Client;
import model.AnswerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(value = "/websocket",
        configurator = GetHttpSessionConfigurator.class)
public class WebSocket implements ChatInterface {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Session wsSession;
    private FindAgentSystem findAgentSystem;
    private HttpSession httpSession;
    private Client client;
    private boolean waitAgent = false;
    private List<CommandContainer> bufferedMessage;
    private Gson json = new Gson();

    @OnOpen
    public void open(Session session, EndpointConfig config) {
        this.wsSession = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        findAgentSystem = FindAgentSystem.getInstance();
        final String login = (String) httpSession.getAttribute("login");
        final boolean isAgent = "Agent".equals((String) httpSession.getAttribute("typeUser"));
        if (login != null) {
            httpSession.setAttribute("isWork", true);
            client = findAgentSystem.getUser(login, isAgent);
            if (client != null) {
                client.setSocket(this);
                if (client.isAgent()) {
                    if (findAgentSystem.findSystem(client)) {
                        client.getRecipient().getSocket().updateBufferedMessage();
                    }
                } else {
                    if (findAgentSystem.findSystem(client)) {
                    } else {
                        bufferedMessage = new ArrayList<>();
                        send(new CommandContainer(AnswerCode.NO_AGENT_WAIT, "Server"));
                    }
                }
            } else close();
        } else close();

    }

    @OnMessage
    public void input(String msg) {
        if ("LEAVE".equals(msg)) {
            if(client.getRecipient()!=null) {
                send(new CommandContainer(AnswerCode.LEAVE_CHAT, "Server"));
                leave();
            } else send(new CommandContainer(AnswerCode.DONT_HAVE_CHAT, "Server"));
        } else {
            CommandContainer commandContainer = new CommandContainer(client.getName(), client.isAgent(), msg);
            if (client.getRecipient() != null) {
                client.getRecipient().getSocket().send(commandContainer);
            } else {
                if (client.isAgent()) send(new CommandContainer(AnswerCode.DONT_HAVE_CLIENT, "Server"));
                else if (waitAgent) {
                    if (bufferedMessage == null) bufferedMessage = new ArrayList<>();
                    bufferedMessage.add(commandContainer);
                    send(new CommandContainer(AnswerCode.FIRST_AGENT_ANSWER_YOU, "Server"));
                } else {
                    if (findAgentSystem.findSystem(client)) {
                        client.getRecipient().getSocket().send(commandContainer);
                    } else {
                        bufferedMessage = new ArrayList<>();
                        bufferedMessage.add(commandContainer);
                        send(new CommandContainer(AnswerCode.NO_AGENT_WAIT, "Server"));
                    }
                }
            }
        }
    }

    @OnClose
    public void close() {
        leave();
        findAgentSystem.remove(client);
        changeWorkAttribute();
        if (wsSession.isOpen()) {
            try {
                wsSession.close();
            } catch (IOException e) {
                log.error("Exception in WebSocket closing", e);
            }
        }
    }

    @OnError
    public void error(Session session, Throwable t) {
        leave();
        findAgentSystem.remove(client);
        changeWorkAttribute();
        log.error("Error in webSocket", t);
    }

    private void changeWorkAttribute() {
        try {
            httpSession.removeAttribute("isWork");
        } catch (IllegalStateException ex) {
            log.info("invalid session", ex);
        }
    }

    @Override
    public void send(CommandContainer commandContainer) {
        try {
            wsSession.getBasicRemote().sendText(json.toJson(commandContainer));
        } catch (IOException e) {
            log.error("Exception in WebSocket sending", e);
        }
    }


    @Override
    public void notWaitAgent() {
        waitAgent = false;
    }

    @Override
    public void waitAgent() {
        waitAgent = true;
    }

    @Override
    public void updateBufferedMessage() {
        if (bufferedMessage != null) {
            for (CommandContainer commandContainer : bufferedMessage)
                client.getRecipient().getSocket().send(commandContainer);
            bufferedMessage = null;
        }
    }

    private void leave() {
        if (client != null) {
            if (client.isAgent()) {
                if (client.getRecipient() != null) {
                    if (findAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getSocket().send(new CommandContainer(AnswerCode.AGENT_LEAVE, "Server"));
                        client.getRecipient().getSocket().updateBufferedMessage();
                    } else {
                        client.getRecipient().getSocket().send(new CommandContainer(AnswerCode.AGENT_LEAVE_WAIT_NEW, "Server"));
                        client.getRecipient().setRecipient(null);
                    }
                }
            } else {
                if (client.getRecipient() != null) {
                    client.getRecipient().getSocket().send(new CommandContainer(AnswerCode.CLIENT_LEAVE, "Server"));
                    if (findAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getRecipient().getSocket().updateBufferedMessage();
                    } else {
                        client.getRecipient().setRecipient(null);
                    }
                }
            }
            client.setRecipient(null);
        }
    }

}
