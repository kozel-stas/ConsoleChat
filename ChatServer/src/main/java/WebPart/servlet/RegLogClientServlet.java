package WebPart.servlet;

import model.AnswerCode;
import model.Client;
import model.FindAgentSystem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RegLogClientServlet extends HttpServlet {
    private FindAgentSystem findAgentSystem;
    private final String pathRegLogClient = "/pages/RegLogClient.jsp";
    private final String addressRegLogClientPage = "/regLogClient";
    private final String loginAttribute = "login";
    private final String typeUserAttribute = "typeUser";
    private final String addressChatPage = "/chat";
    private final String typeUser = "Client";

    @Override
    public void init() throws ServletException {
        super.init();
        findAgentSystem = FindAgentSystem.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession httpSession = request.getSession();
        if (httpSession != null && httpSession.getAttribute(loginAttribute) != null && httpSession.getAttribute(typeUserAttribute) != null)
            response.sendRedirect("/");
        else request.getRequestDispatcher(pathRegLogClient).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final String login = request.getParameter(loginAttribute);
        final String typeOperation = request.getParameter("typeOperation");
        final String nameAnswerCode = "answerCode";
        if (login != null && typeOperation != null) {
            if ("register".equals(typeOperation)) {
                if (findAgentSystem.findClient(login)) {
                    request.setAttribute(nameAnswerCode, AnswerCode.NAME_ALREADY_USED);
                    request.getRequestDispatcher(pathRegLogClient).forward(request, response);
                } else {
                    findAgentSystem.addClient(new Client(login, null, false));
                    request.getSession().setAttribute(loginAttribute, login);
                    request.getSession().setAttribute(typeUserAttribute, typeUser);
                    response.sendRedirect(addressChatPage);
                }
            } else if ("login".equals(typeOperation)) {
                if (!findAgentSystem.authorize(login, typeUser)) {
                    if (findAgentSystem.findAgent(login)) {
                        request.setAttribute(nameAnswerCode, AnswerCode.CLIENT_ONLINE_YET);
                        request.getRequestDispatcher(pathRegLogClient).forward(request, response);
                        return;
                    }
                    request.setAttribute(nameAnswerCode, AnswerCode.DONT_HAVE_REGISTER_CLIENT);
                    request.getRequestDispatcher(pathRegLogClient).forward(request, response);
                } else {
                    findAgentSystem.addClient(new Client(login, null, false));
                    request.getSession().setAttribute(loginAttribute, login);
                    request.getSession().setAttribute(typeUserAttribute, typeUser);
                    response.sendRedirect(addressChatPage);
                }
            } else response.sendRedirect(addressRegLogClientPage);
        } else response.sendRedirect(addressRegLogClientPage);
    }
}