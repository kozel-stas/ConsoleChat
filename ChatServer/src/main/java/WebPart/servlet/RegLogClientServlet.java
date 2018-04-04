package WebPart.servlet;

import model.*;
import model.SupportClasses.AnswerCode;
import model.SupportClasses.CommandContainer;
import model.SupportClasses.Role;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RegLogClientServlet extends HttpServlet {
    private DataManipulate dataManipulate;
    private final String pathRegLogClient = "/pages/RegLogClient.jsp";
    private final String addressRegLogClientPage = "/regLogClient";
    private final String loginAttribute = "login";
    private final String typeUserAttribute = "typeUser";
    private final String addressChatPage = "/chat";
    private final String typeUser = "User";

    @Override
    public void init() throws ServletException {
        super.init();
        dataManipulate = DataManipulate.getInstance();
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
        if (login != null && typeOperation != null) {
            User user = new User(login, null, Role.CLIENT, null);
            final String nameAnswerCode = "answerCode";
            if ("register".equals(typeOperation)) {
                CommandContainer commandContainer = dataManipulate.register(user);
                if (commandContainer.getServerInfo() == AnswerCode.GOOD_REGISTER) {
                    request.getSession().setAttribute(loginAttribute, login);
                    request.getSession().setAttribute(typeUserAttribute, typeUser);
                    response.sendRedirect(addressChatPage);
                    dataManipulate.remove(user);
                } else {
                    request.setAttribute(nameAnswerCode, commandContainer.getServerInfo());
                    request.getRequestDispatcher(pathRegLogClient).forward(request, response);
                }
            } else if ("login".equals(typeOperation)) {
                CommandContainer commandContainer = dataManipulate.login(user);
                if (commandContainer.getServerInfo() == AnswerCode.GOOD_LOGIN) {
                    request.getSession().setAttribute(loginAttribute, login);
                    request.getSession().setAttribute(typeUserAttribute, typeUser);
                    response.sendRedirect(addressChatPage);
                    dataManipulate.remove(user);
                } else {
                    request.setAttribute(nameAnswerCode, commandContainer.getServerInfo());
                    request.getRequestDispatcher(pathRegLogClient).forward(request, response);
                }
            } else response.sendRedirect(addressRegLogClientPage);
        } else response.sendRedirect(addressRegLogClientPage);
    }
}