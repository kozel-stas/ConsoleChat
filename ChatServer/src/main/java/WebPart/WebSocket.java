package WebPart;

import com.google.gson.Gson;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;
import javax.websocket.EndpointConfig;
import java.io.IOException;

@ServerEndpoint(value = "/websocket",
    configurator = GetHttpSessionConfigurator.class)
public class WebSocket implements ChatInterface {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Session wsSession;
    private static DataManipulate dataManipulate = DataManipulate.getInstance();
    private HttpSession httpSession;
    private User user;
    private Gson json = new Gson();

    @OnOpen
    public void open(Session session, EndpointConfig config) {
        this.wsSession = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        final String login = (String) httpSession.getAttribute("login");
        final Role role = "Agent".equals((String) httpSession.getAttribute("typeUser")) ? Role.AGENT : Role.CLIENT;
        if (login != null) {
            httpSession.setAttribute("isWork", true);
            user = new User(login, this, role, TypeApp.WEB);
            if (dataManipulate.login(user).getServerInfo() != AnswerCode.GOOD_LOGIN) close();
        } else close();

    }

    @OnMessage
    public void input(String msg) {
        if ("LEAVE".equals(msg) && user.getRole() == Role.CLIENT) {
            if (user.getChat().haveAgent()) {
                send(new CommandContainer("Server", null, AnswerCode.LEAVE_CHAT));
                user.leave();
            } else send(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_CHAT));
        } else {
            MessageWeb messageWeb = json.fromJson(msg, MessageWeb.class);
            CommandContainer commandContainer = new CommandContainer(user.getLogin(), user.getRole(), messageWeb.getMsg());
            if (user.getRole() == Role.CLIENT) user.getChat().sendMessage(commandContainer);
            else if (user.getChat(messageWeb.getName()) != null) {
                user.getChat(messageWeb.getName()).sendMessage(commandContainer);
            } else {
                send(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_CLIENT));
            }
        }
    }

    @OnClose
    public void close() {
        if (user != null) user.leave();
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
        if (user != null) user.leave();
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

    public class MessageWeb {
        private String name;
        private String msg;

        public String getName() {
            return name;
        }

        public String getMsg() {
            return msg;
        }

        public MessageWeb(String name, String msg) {
            this.msg = msg;
            this.name = name;
        }
    }
}