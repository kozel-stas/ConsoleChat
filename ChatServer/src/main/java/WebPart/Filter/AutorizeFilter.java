package WebPart.Filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AutorizeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession(true);
        System.out.println(request.getAttribute("login")+ " " +request.getAttribute("password"));
        if(session!=null && session.getAttribute("login")!=null && session.getAttribute("password")!=null){
            request.getRequestDispatcher("/WEB-INF/pages/LoginPage.jsp").forward(request,response);
        } else if (request.getParameter("login")!=null && request.getParameter("typeUser")!=null){
            request.getRequestDispatcher("/WEB-INF/pages/ClientPage.jsp").forward(request,response);
        } else  request.getRequestDispatcher("/WEB-INF/pages/StartPage.jsp").forward(request,response);
    }

    @Override
    public void destroy() {

    }
}
