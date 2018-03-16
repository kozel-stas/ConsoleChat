package WebPart.Filter;


import model.AnswerCode;
import model.Client;
import model.FindAgentSystem;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class AutorizeFilter implements Filter {
    private FindAgentSystem findAgentSystem;
    private String pathLoginPage;
    private String pathChatPage;
    private String addressLoginPage;
    private String loginAttribute;
    private String typeUserAttribute;
    private String isChatAttribute;
    private String nameAnswerCode = "answerCode";
    private String nameMap = "serverAnswer";
    private Map<AnswerCode, String> serverAnswer;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        findAgentSystem = (FindAgentSystem) filterConfig.getServletContext().getAttribute("findAgentSystem");
        pathLoginPage = "/pages/LoginPage.jsp";
        pathChatPage = "/pages/ChatPage.jsp";
        addressLoginPage = "/login";
        loginAttribute = "login";
        typeUserAttribute = "typeUser";
        isChatAttribute = "isWork";
        serverAnswer = new EnumMap<AnswerCode, String>(AnswerCode.class);
        serverAnswer.put(AnswerCode.NAME_ALREADY_USED, "Выбранное имя уже занято");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession();
        final String login = request.getParameter(loginAttribute);
        final String typeUser = request.getParameter(typeUserAttribute);
        if (login != null && typeUser != null) {
            if ("Agent".equals(typeUser)) {
                if (!findAgentSystem.authorize(login, "Agent")) {
                    if (findAgentSystem.findAgent(login)) {
                        request.setAttribute(nameAnswerCode, AnswerCode.AGENT_ONLINE_YET);
                        request.setAttribute(nameMap, serverAnswer);
                        response.sendRedirect(addressLoginPage);
                        return;
                    }
                    request.setAttribute(nameAnswerCode, AnswerCode.DONT_HAVE_REGISTER_AGENT);
                    request.setAttribute(nameMap, serverAnswer);
                    response.sendRedirect(addressLoginPage);
                } else {
                    findAgentSystem.addAgent(new Client(login, null, true));
                    request.getSession().setAttribute(loginAttribute, login);
                    request.getSession().setAttribute(typeUserAttribute, typeUser);
                    request.getRequestDispatcher(pathChatPage).forward(request, response);
                }
            } else if ("Client".equals(typeUser)) {
                if (!findAgentSystem.authorize(login, "Client")) {
                    if (findAgentSystem.findAgent(login)) {
                        request.setAttribute(nameAnswerCode, AnswerCode.CLIENT_ONLINE_YET);
                        request.setAttribute(nameMap, serverAnswer);
                        response.sendRedirect(addressLoginPage);
                        return;
                    }
                    request.setAttribute(nameAnswerCode, AnswerCode.DONT_HAVE_REGISTER_CLIENT);
                    request.setAttribute(nameMap, serverAnswer);
                    response.sendRedirect(addressLoginPage);
                } else {
                    findAgentSystem.addClient(new Client(login, null, false));
                    request.getSession().setAttribute(loginAttribute, login);
                    request.getSession().setAttribute(typeUserAttribute, typeUser);
                    request.getRequestDispatcher(pathChatPage).forward(request, response);
                }
            } else response.sendRedirect(addressLoginPage);
        } else response.sendRedirect(addressLoginPage);
    }

    @Override
    public void destroy() {

    }
}
