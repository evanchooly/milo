package org.milo.deployment;

import java.lang.String;
import java.util.Enumeration;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;

import org.milo.MiloServletContext;

public class ServletHolder {
    private String name;
    private Class<? extends Servlet> klass;
    private Servlet cachedServlet;
    private MiloServletContext servletContext;
    private Integer loadOnStartup = -1;

    public ServletHolder(String name, Class<? extends Servlet> clazz) {
        this.name = name;
        klass = clazz;
    }

    public String getName() {
        return name;
    }

    public void setServletContext(MiloServletContext miloServletContext) {
        servletContext = miloServletContext;
    }

    public Integer getLoadOnStartup() {
        return loadOnStartup;
    }

    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    public Servlet loadServlet() throws ServletException {
        Servlet servlet = cachedServlet;
        if(servlet == null) {
            try {
                servlet = klass.newInstance();
                servlet.init(new MiloServletConfig());
                if(!(servlet instanceof SingleThreadModel)) {
                    cachedServlet = servlet;
                }
            } catch (ReflectiveOperationException e) {
                throw new ServletException(e.getMessage(), e);
            }
        }
        return servlet;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ServletHolder");
        sb.append("{name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private class MiloServletConfig implements ServletConfig {
        @Override
        public String getServletName() {
            return name;
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }

        @Override
        public String getInitParameter(String name) {
            return servletContext.getInitParameter(name);
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return servletContext.getInitParameterNames();
        }
    }
}
