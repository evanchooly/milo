package org.milo;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class BeforeFilter implements Filter {
    public static final String BEFORE = "###BEFORE###";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        response.getWriter().write(BEFORE);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
