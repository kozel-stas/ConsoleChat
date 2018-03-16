package WebPart.Filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SkipFilter implements Filter {
    private final String loginAttribute  = "login";
    private final String typeUserAttribute= "typeUser";
    private final String isChatAttribute = "isWork";
    private final String addressStartPage = "/start";
    private final String addressChatPage="/chat";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession();
        if (session != null && session.getAttribute(loginAttribute) != null && session.getAttribute(typeUserAttribute) != null) {
            if (session.getAttribute(isChatAttribute) != null) {
                response.sendRedirect(addressStartPage);
            } else response.sendRedirect(addressChatPage);
        } else response.sendRedirect(addressStartPage);
    }

    @Override
    public void destroy() {

    }
}
