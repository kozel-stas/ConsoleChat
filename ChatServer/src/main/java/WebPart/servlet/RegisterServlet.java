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

public class RegisterServlet extends HttpServlet {
    private FindAgentSystem findAgentSystem;
    private String pathRegisterPage;
    private String addressChatPage;
    private String addressRegisterPage;
    private String loginAttribute;
    private String typeUserAttribute;
    private String isChatAttribute;

    @Override
    public void init() throws ServletException {
        super.init();
        findAgentSystem = FindAgentSystem.getInstance();
        pathRegisterPage = "/pages/RegisterPage.jsp";
        addressChatPage = "/chat";
        addressRegisterPage = "/register";
        loginAttribute = "login";
        typeUserAttribute = "typeUser";
        isChatAttribute = "isWork";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setStatus(200);
        HttpSession httpSession=request.getSession();
        if(httpSession!=null && httpSession.getAttribute(loginAttribute)!=null && httpSession.getAttribute(typeUserAttribute)!=null)
            response.sendRedirect("/");
        else request.getRequestDispatcher(pathRegisterPage).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final String login = request.getParameter(loginAttribute);
        final String typeUser = request.getParameter(typeUserAttribute);
        if (login != null && typeUser != null) {
            String nameAnswerCode = "answerCode";
            if ("Agent".equals(typeUser)) {
                if (findAgentSystem.findAgent(login)) {
                    request.setAttribute(nameAnswerCode, AnswerCode.NAME_ALREADY_USED);
                    request.getRequestDispatcher(pathRegisterPage).forward(request, response);
                } else {
                    findAgentSystem.addAgent(new Client(login, null, true));
                    request.getSession().setAttribute(loginAttribute, login);
                    request.getSession().setAttribute(typeUserAttribute, typeUser);
                    response.sendRedirect(addressChatPage);
                }
            } else if ("Client".equals(typeUser)) {
                if (findAgentSystem.findClient(login)) {
                    request.setAttribute(nameAnswerCode, AnswerCode.NAME_ALREADY_USED);
                    request.getRequestDispatcher(pathRegisterPage).forward(request, response);
                } else {
                    findAgentSystem.addClient(new Client(login, null, false));
                    request.getSession().setAttribute(loginAttribute, login);
                    request.getSession().setAttribute(typeUserAttribute, typeUser);
                    response.sendRedirect(addressChatPage);
                }
            } else response.sendRedirect(addressRegisterPage);
        } else response.sendRedirect(addressRegisterPage);
    }
}
