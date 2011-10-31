package com.antwerkz.milo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.antwerkz.milo.deployment.ServletType;
import com.antwerkz.milo.deployment.WebAppType;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class MiloServletContext implements ServletContext {
    private final String path;
    private String root;
    private ServletContainer container;
    private String contextName;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private ConcurrentMap<String, String> initParams = new ConcurrentHashMap<String, String>();
    private SessionCookieConfig sessionCookieConfig;
    private Set<SessionTrackingMode> sessionTrackingModes;
    private List<EventListener> listeners = new ArrayList<EventListener>();
    private ClassLoader webAppClassLoader;
    private File webXml;

    public MiloServletContext(ServletContainer container, String name, String path, String root)
        throws ServletException {
        this.container = container;
        contextName = name;
        this.path = path;
        try {
            this.root = new File(root).getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        webXml = new File(root, "WEB-INF/web.xml");
        if(!webXml.exists()) {
            throw new ServletException("No WEB-INF/web.xml found.");
        }
        webAppClassLoader = new WebAppClassLoader(root);
        deploy();
    }

    private void deploy() throws ServletException {
        try {
            JAXBContext jc = JAXBContext.newInstance( "com.antwerkz.milo.deployment", webAppClassLoader );
            Unmarshaller u = jc.createUnmarshaller();
            final JAXBElement<WebAppType> unmarshal = (JAXBElement<WebAppType>) u.unmarshal(webXml.getAbsoluteFile());
            final WebAppType webapp = (WebAppType) unmarshal.getValue();
            XStream xstream = new XStream(new StaxDriver());
            xstream.fromXML(new FileInputStream(webXml));
            System.out.println("webapp = " + webapp);

        } catch (JAXBException | FileNotFoundException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    private void deploy(ServletType type) {
        System.out.println("MiloServletContext.deploy");
    }
    @Override
    public String getContextPath() {
        return path;
    }

    @Override
    public ServletContext getContext(String uriPath) {
        return container.getContext(uriPath);
    }

    @Override
    public int getMajorVersion() {
        return 3;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 3;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType(String file) {
        return container.getMimeType(file);
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("paths must begin with /");
        }
        Set<String> set = new TreeSet<String>();
        try {
            final File dir = new File(root, path).getCanonicalFile();
            final File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String canonicalPath = file.getCanonicalPath();
                    if (file.isDirectory()) {
                        canonicalPath += "/";
                    }
                    set.add(canonicalPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return set;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return path == null ? null : Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return Collections.enumeration(Collections.<Servlet>emptyList());
    }

    @Override
    public Enumeration<String> getServletNames() {
        return Collections.enumeration(Collections.<String>emptyList());
    }

    @Override
    public void log(String msg) {
    }

    @Override
    public void log(Exception exception, String msg) {
    }

    @Override
    public void log(String message, Throwable throwable) {
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public String getServerInfo() {
        return "milo/1.0-SNAPSHOT";
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParams.keySet());
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return initParams.putIfAbsent(name, value) == null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public String getServletContextName() {
        return contextName;
    }

    @Override
    public Dynamic addServlet(String servletName, String className) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public Dynamic addServlet(String servletName, Servlet servlet) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName,
        Class<? extends Filter> filterClass) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        if(sessionCookieConfig == null) {
            sessionCookieConfig = new MiloSessionCookieConfig();
        }
        return sessionCookieConfig;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        this.sessionTrackingModes = sessionTrackingModes;
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return EnumSet.of(SessionTrackingMode.COOKIE);
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return sessionTrackingModes != null ? sessionTrackingModes : getDefaultSessionTrackingModes();
    }

    @Override
    public void addListener(String className) {
        try {
            addListener((ServletContextListener)getClassLoader().loadClass(className).newInstance());
        } catch (ReflectiveOperationException e) {
            log(e, e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        listeners.add(t);
    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {
        try {
            listeners.add(listenerClass.newInstance());
        } catch (ReflectiveOperationException e) {
            log(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException("Still working on this one...");
    }

    @Override
    public ClassLoader getClassLoader() {
        return webAppClassLoader;
    }

    @Override
    public void declareRoles(String... roleNames) {
    }

}
