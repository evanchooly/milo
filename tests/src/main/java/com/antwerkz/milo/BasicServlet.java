package com.antwerkz.milo;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BasicServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Enumeration<String> initParameterNames = getInitParameterNames();
        while(initParameterNames.hasMoreElements()) {
            final String name = initParameterNames.nextElement();
            resp.setHeader(name, getInitParameter(name));
        }
        resp.getWriter().println("BasicServlet.service");
    }
}
