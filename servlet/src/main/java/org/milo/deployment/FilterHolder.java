package org.milo.deployment;

import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.milo.FilterChainImpl;
import org.milo.MiloServletContext;

@SuppressWarnings({"unchecked"})
public class FilterHolder {
    private List<Pattern> urlPatterns = new ArrayList<>();
    private List<String> servletNames = new ArrayList<>();
    private Filter filter;
    private String filterClass;
    private boolean asyncSuppported;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChainImpl chain)
        throws ServletException, IOException {
        getFilter().doFilter(request, response, chain);
    }

    public boolean matches(ServletRequest request, String name) {
        if (!(request instanceof HttpServletRequest)) {
            return true;
        }
        boolean matched = false;
        final String uri = ((HttpServletRequest) request).getRequestURI();
        final Iterator<Pattern> iterator = urlPatterns.iterator();
        while (iterator.hasNext() && !matched) {
            matched = iterator.next().matcher(uri).matches();
        }
        if (!matched) {
            matched = servletNames.contains(name);
        }
        return matched;
    }

    public void addServletName(String name) {
        servletNames.add(name);
    }

    public void addUrlPattern(String value) {
        urlPatterns.add(Pattern.compile(value.replace("*", ".*")));
    }

    public Filter getFilter() throws ServletException {
        if (filter == null) {
            try {
                filter = ((Class<? extends Filter>) getClass().getClassLoader()
                                    .loadClass(filterClass)).newInstance();
            } catch (ReflectiveOperationException e) {
                throw new ServletException(e.getMessage(), e);
            }
        }
        return filter;
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSuppported = asyncSupported;
    }

    public void init(final String filterName, final MiloServletContext context) throws ServletException {
        getFilter().init(new FilterConfig() {
            @Override
            public String getFilterName() {
                return filterName;
            }

            @Override
            public ServletContext getServletContext() {
                return context;
            }

            @Override
            public String getInitParameter(String name) {
                return context.getInitParameter(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return context.getInitParameterNames();
            }
        });
    }
}
