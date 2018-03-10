package WebPart;

import model.ChatInterface;
import model.Client;
import model.FindAgentSystem;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/websocket",
        configurator = GetHttpSessionConfigurator.class)
public class WebSocket implements ChatInterface {
    private Session wsSession;
    private FindAgentSystem findAgentSystem;
    private HttpSession httpSession;
    private Client client;

    @OnOpen
    public void open(Session session, EndpointConfig config) {
        this.wsSession = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        ServletContext servletContext =httpSession.getServletContext();
        findAgentSystem = (FindAgentSystem) servletContext.getAttribute("findAgentSystem");
        final String login = (String) httpSession.getAttribute("login");
        final boolean isAgent = "Agent".equals((String) httpSession.getAttribute("typeUser"));
        client = findAgentSystem.getUser(login,isAgent);
        if (client!=null){
            if (client.isAgent()){
                if(findAgentSystem.findSystem(client))
                    send("mk");
            }
        } else close();
        System.out.println("efef");
    }

    @OnMessage
    public void input(String msg) {
        System.out.println(msg);
        try {
            wsSession.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void send(String msg) {
        try {
            wsSession.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @OnClose
    public void close() {

    }


}
