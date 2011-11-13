package com.antwerkz.milo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ServletContainer {
    private final Map<String, MiloServletContext> contexts = new ConcurrentHashMap<>();
    protected String host;
    protected int port;

    public ServletContainer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public abstract void start() throws IOException;

    public abstract void stop();

    public MiloServletContext createContext(String name, String path, String root) throws ServletException {
        final MiloServletContext context = new MiloServletContext(this, name, path, root);
        final MiloServletContext put = contexts.put(path, context);
        return put == null ? context : put;
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        final String uri = request.getRequestURI();
        final MiloServletContext context = contexts.get(uri);
        context.loadServlet(uri).service(request, response);
    }

    public ServletContext getContext(String uriPath) {
        return contexts.get(uriPath);
    }

    public abstract String getMimeType(String file);

    @Override
    public String toString() {
        return String.format("%s[%s:%s]", getClass().getSimpleName(), host, port);
    }
}
