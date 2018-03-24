package WebPart.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ChatServlet extends HttpServlet {
    private final String pathChatPageClient = "/pages/ChatPageClient.html";
    private final String pathChatPageAgent = "/pages/ChatPageAgent.html";
    private final String loginAttribute = "login";
    private final String typeUserAttribute = "typeUser";
    private final String isChatAttribute = "isWork";
    private final String addressStart = "/start";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession httpSession = request.getSession();

        if (httpSession != null && httpSession.getAttribute(loginAttribute) != null && httpSession.getAttribute(typeUserAttribute) != null && httpSession.getAttribute(isChatAttribute) == null)
            if("Agent".equals(httpSession.getAttribute(typeUserAttribute))) request.getRequestDispatcher(pathChatPageAgent).forward(request, response);
            else request.getRequestDispatcher(pathChatPageClient).forward(request, response);
        else response.sendRedirect(addressStart);
    }

}       
