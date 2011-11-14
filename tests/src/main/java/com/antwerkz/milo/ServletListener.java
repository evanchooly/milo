package com.antwerkz.milo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setInitParameter("listener", getClass().getName());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
