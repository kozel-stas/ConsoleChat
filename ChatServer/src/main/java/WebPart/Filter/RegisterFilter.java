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

public class RegisterFilter implements Filter {
    private FindAgentSystem findAgentSystem;
    private String pathRegisterPage;
    private String pathChatPage;
    private String nameAnswerCode = "answerCode";
    private String nameMap = "serverAnswer";
    private Map<AnswerCode, String> serverAnswer;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        findAgentSystem = (FindAgentSystem) filterConfig.getServletContext().getAttribute("findAgentSystem");
        pathRegisterPage = "/WEB-INF/pages/RegisterPage.jsp";
        pathChatPage = "/WEB-INF/pages/ChatPage.jsp";
        serverAnswer = new EnumMap<AnswerCode, String>(AnswerCode.class);
        serverAnswer.put(AnswerCode.NAME_ALREADY_USED, "Выбранное имя уже занято");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession();
        final String login = request.getParameter("login");
        final String typeUser = request.getParameter("typeUser");
        if (session != null && session.getAttribute("login") != null && session.getAttribute("typeUser") != null) {
            request.getRequestDispatcher(pathChatPage).forward(request, response);
        } else if (login != null && typeUser != null) {
            if ("Agent".equals(typeUser)) {
                if (findAgentSystem.findAgent(login)) {
                    request.setAttribute(nameAnswerCode, AnswerCode.NAME_ALREADY_USED);
                    request.setAttribute(nameMap, serverAnswer);
                    request.getRequestDispatcher(pathRegisterPage).forward(request, response);
                } else {
                    findAgentSystem.addAgent(new Client(login, null, true));
                    request.getSession().setAttribute("login", login);
                    request.getSession().setAttribute("typeUser", typeUser);
                    request.getRequestDispatcher(pathChatPage).forward(request, response);
                }
            } else if ("Client".equals(typeUser)) {
                if (findAgentSystem.findUser(login)) {

                } else {
                    findAgentSystem.addUser(new Client(login, null, false));
                    request.getSession().setAttribute("login", login);
                    request.getSession().setAttribute("typeUser", typeUser);
                    request.getRequestDispatcher(pathChatPage).forward(request, response);
                }
            } else request.getRequestDispatcher(pathRegisterPage).forward(request, response);
        } else request.getRequestDispatcher(pathRegisterPage).forward(request, response);
    }

    @Override
    public void destroy() {

    }
}
