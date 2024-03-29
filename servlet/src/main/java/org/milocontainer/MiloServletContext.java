/**
 * Copyright (C) 2011 Justin Lee <jlee@antwerkz.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.milocontainer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.milocontainer.deployment.DeploymentContext;
import org.milocontainer.deployment.ServletHolder;
import org.milocontainer.staticresources.StaticResourcesHolder;

public class MiloServletContext implements ServletContext {
    private static final String RESOURCES_PATH = "META-INF/resources";
    private final String path;
    private String root;
    private ServletContainer container;
    private String contextName;
    private Map<String, Object> attributes = new HashMap<>();
    private ConcurrentMap<String, String> initParams = new ConcurrentHashMap<>();
    private SessionCookieConfig sessionCookieConfig;
    private Set<SessionTrackingMode> sessionTrackingModes;
    private List<EventListener> listeners = new ArrayList<>();
    private ClassLoader webAppClassLoader;
    private Map<String, ServletHolder> mappings;
    private FilterSet filterSet;
    private StaticResourcesHolder staticResources;

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
        File webXml = new File(root, "WEB-INF/web.xml");
        if (!webXml.exists()) {
            throw new ServletException("No WEB-INF/web.xml found: " + webXml.getAbsolutePath());
        }
        webAppClassLoader = new WebAppClassLoader(this.root, getClass().getClassLoader());
        deploy(webXml);
        staticResources = new StaticResourcesHolder(this);
    }

    public File getDocRoot() {
        return new File(root);
    }

    private void deploy(File webXml) throws ServletException {
        final DeploymentContext context = new DeploymentContext(this);
        context.load(webXml.getAbsoluteFile());
        init(context);
    }

    private void init(DeploymentContext context) throws ServletException {
        loadInitParams(context);
        loadAndInitServlets(context);
        loadAndInitFilters(context);
    }

    private void loadAndInitFilters(DeploymentContext context) throws ServletException {
        filterSet = context.getFilterSet();
        filterSet.init(this);
    }

    private void loadAndInitServlets(DeploymentContext context) throws ServletException {
        mappings = context.getMappings();
        Set<ServletHolder> initialLoads = new TreeSet<>(new Comparator<ServletHolder>() {
            @Override
            public int compare(ServletHolder o1, ServletHolder o2) {
                return o1.getLoadOnStartup().compareTo(o2.getLoadOnStartup());
            }
        });
        for (ServletHolder servletHolder : mappings.values()) {
            if (servletHolder.getLoadOnStartup() != -1) {
                initialLoads.add(servletHolder);
            }
        }
        for (ServletHolder holder : initialLoads) {
            holder.loadServlet();
        }
    }

    private void loadInitParams(DeploymentContext context) throws ServletException {
        initParams.putAll(context.getInitParams());
        for (String klass : context.getListeners()) {
            try {
                final ServletContextListener listener = (ServletContextListener) getClassLoader().loadClass(klass)
                    .newInstance();
                listeners.add(listener);
                listener.contextInitialized(new ServletContextEvent(this));
            } catch (ReflectiveOperationException e) {
                throw new ServletException(e.getMessage(), e);
            }
        }
    }

    public void destroy() {
        for (EventListener listener : listeners) {
            if (listener instanceof ServletContextListener) {
                ((ServletContextListener) listener).contextDestroyed(new ServletContextEvent(this));
            }
        }
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
    public Set<String> getResourcePaths(String resourcePath) {
        if (resourcePath == null) {
            return null;
        }
        if (!resourcePath.startsWith("/")) {
            throw new IllegalArgumentException("paths must begin with /");
        }
        Set<String> set = new TreeSet<>();
        Path rootPath = Paths.get(root);
        for (File path : Paths.get(root, resourcePath).toFile().listFiles()) {
            String relativePath = rootPath.relativize(path.toPath()).toString();
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
            if (path.isDirectory()) {
                relativePath += "/";
            }
            set.add(relativePath);
        }
        scanJars(resourcePath, set);
        return set;
    }

    private void scanJars(String resourcePath, Set<String> set) {
        final String rootPath = RESOURCES_PATH + resourcePath;
        Path webInf = Paths.get(root, "/WEB-INF/lib");
        for (File path : webInf.toFile().listFiles(new JarZipFilenameFilter())) {
            try (ZipFile zip = new ZipFile(path)) {
                final Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    final ZipEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(rootPath)) {
                        final String substring = entry.getName().substring(rootPath.length());
                        final boolean onlyOnEnd = substring.indexOf("/") == substring.lastIndexOf("/")
                            && substring.indexOf("/") == substring.length() - 1;
                        if (!substring.isEmpty() && onlyOnEnd) {
                            set.add("/" + entry.getName().substring(RESOURCES_PATH.length() + 1));
                        }
                    }
                }
            } catch (IOException e) {
                log(e, e.getMessage());
            }
        }
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
        return "milocontainer/0.1-SNAPSHOT";
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
        if (sessionCookieConfig == null) {
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
            addListener((ServletContextListener) getClassLoader().loadClass(className).newInstance());
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

    public ServletHolder loadServlet(String uri) throws ServletException {
        ServletHolder servletHolder = null;
        String resource = uri;
        while (servletHolder == null && resource.contains("/")) {
            servletHolder = mappings.get(resource);
            if (servletHolder == null) {
                resource = resource.substring(0, resource.lastIndexOf("/"));
            }
        }
        if(servletHolder == null) {
            if(staticResources.matches(uri)) {
                servletHolder = staticResources;
            }
        }
        return servletHolder;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        ServletHolder holder = loadServlet(requestURI);
        if (holder == null) {
            response.setStatus(404);
        } else {
            filterSet.doFilter(request, response, holder);
        }
    }

    private static class JarZipFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            final String lower = name.toLowerCase();
            return lower.endsWith(".jar") || lower.endsWith(".zip");
        }
    }

}
