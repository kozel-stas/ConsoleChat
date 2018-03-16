package WebPart.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    private final String pathLoginPage="/pages/LoginPage.jsp";
    private final String loginAttribute = "login";
    private final String typeUserAttribute = "typeUser";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession httpSession=request.getSession();
        if(httpSession!=null && httpSession.getAttribute(loginAttribute)!=null && httpSession.getAttribute(typeUserAttribute)!=null)
            response.sendRedirect("/");
        else request.getRequestDispatcher(pathLoginPage).forward(request, response);
    }
}