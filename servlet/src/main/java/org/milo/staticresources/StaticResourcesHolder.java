package org.milo.staticresources;

import java.io.File;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.milo.MiloServletContext;
import org.milo.deployment.ServletHolder;

public class StaticResourcesHolder extends ServletHolder {
    private MiloServletContext servletContext;
    private Servlet servlet;

    public StaticResourcesHolder(MiloServletContext miloServletContext) {
        super(miloServletContext, "staticResources", null);
        this.servletContext = miloServletContext;
        servlet = new StaticResourcesServlet(miloServletContext.getDocRoot());
    }

    @Override
    public Servlet loadServlet() throws ServletException {
        return servlet;
    }

    public boolean matches(String uri) {
        final File docRoot = servletContext.getDocRoot();
        return new File(docRoot, uri).exists();
    }
}
