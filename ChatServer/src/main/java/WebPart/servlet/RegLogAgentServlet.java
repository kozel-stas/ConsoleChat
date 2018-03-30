//package WebPart.servlet;
//
//import model.AnswerCode;
//import model.User;
//import model.FindAgentSystem;
//import model.TypeApp;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//public class RegLogAgentServlet extends HttpServlet {
//    private FindAgentSystem findAgentSystem;
//    private final String pathRegisterPage = "/pages/RegLogAgent.jsp";
//    private final String addressChatPage = "/chat";
//    private final String addressRegLogAgent = "/regLogAgent";
//    private final String loginAttribute = "login";
//    private final String typeUserAttribute = "typeUser";
//    private final String typeUser = "Agent";
//
//    @Override
//    public void init() throws ServletException {
//        super.init();
//        findAgentSystem = FindAgentSystem.getInstance();
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        response.setStatus(200);
//        HttpSession httpSession = request.getSession();
//        if (httpSession != null && httpSession.getAttribute(loginAttribute) != null && httpSession.getAttribute(typeUserAttribute) != null)
//            response.sendRedirect("/");
//        else request.getRequestDispatcher(pathRegisterPage).forward(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        final HttpSession session = request.getSession();
//        final String login = request.getParameter(loginAttribute);
//        final String typeOperation = request.getParameter("typeOperation");
//        if (login != null && typeOperation != null) {
//            final String nameAnswerCode = "answerCode";
//            if ("register".equals(typeOperation)) {
//                if (findAgentSystem.findAgent(login)) {
//                    request.setAttribute(nameAnswerCode, AnswerCode.NAME_ALREADY_USED);
//                    request.getRequestDispatcher(pathRegisterPage).forward(request, response);
//                } else {
//                    findAgentSystem.addAgent(new User(login, null, true,TypeApp.WEB));
//                    request.getSession().setAttribute(loginAttribute, login);
//                    request.getSession().setAttribute(typeUserAttribute, typeUser);
//                    response.sendRedirect(addressChatPage);
//                }
//            } else if ("login".equals(typeOperation)) {
//                if (!findAgentSystem.authorize(login, typeUser)) {
//                    if (findAgentSystem.findAgent(login)) {
//                        request.setAttribute(nameAnswerCode, AnswerCode.AGENT_ONLINE_YET);
//                        request.getRequestDispatcher(pathRegisterPage).forward(request, response);
//                        return;
//                    }
//                    request.setAttribute(nameAnswerCode, AnswerCode.DONT_HAVE_REGISTER_AGENT);
//                    request.getRequestDispatcher(pathRegisterPage).forward(request, response);
//                } else {
//                    findAgentSystem.addAgent(new User(login, null, true, TypeApp.WEB));
//                    request.getSession().setAttribute(loginAttribute, login);
//                    request.getSession().setAttribute(typeUserAttribute, typeUser);
//                    response.sendRedirect(addressChatPage);
//                }
//            } else response.sendRedirect(addressRegLogAgent);
//        } else response.sendRedirect(addressRegLogAgent);
//    }
//}
//
