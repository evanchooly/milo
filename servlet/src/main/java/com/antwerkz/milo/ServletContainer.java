package com.antwerkz.milo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public abstract class ServletContainer {
    private final Map<String, MiloServletContext> contexts = new ConcurrentHashMap<String, MiloServletContext>();

    public abstract boolean add(HttpServlet servlet, String... mappings);

    public abstract void start() throws IOException;

    public abstract void stop();

    public MiloServletContext createContext(String name, String path, String root) throws ServletException {
        final MiloServletContext context = new MiloServletContext(this, name, path, root);
        final MiloServletContext put = contexts.put(path, context);
        return put == null ? context : put;
    }

    public ServletContext getContext(String uriPath) {
        return contexts.get(uriPath);
    }

    public abstract String getMimeType(String file);
}
