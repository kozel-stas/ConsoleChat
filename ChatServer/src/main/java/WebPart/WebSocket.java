package WebPart;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/websocket",
        configurator = GetHttpSessionConfigurator.class)
public class WebSocket {
    private Session wsSession;
    private HttpSession httpSession;

    @OnOpen
    public void open(Session session, EndpointConfig config) {
        this.wsSession = session;
        this.httpSession = (HttpSession) config.getUserProperties()
                .get(HttpSession.class.getName());
        System.out.println("hugytfrde");
    }

    @OnMessage
    public void echo(String msg) throws IOException {
        System.out.println(msg);
        wsSession.getBasicRemote().sendText(msg);

    }
}
