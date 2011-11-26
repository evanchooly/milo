package com.antwerkz.milo.deployment;

import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings({"unchecked"})
public class FilterHolder {
    private List<Pattern> urlPattern = new ArrayList<>();
    private List<String> servletName = new ArrayList<>();
    private Filter filter;
    private String filterClass;
    private boolean asyncSuppported;
    private FilterChain filterChain;

    public FilterHolder(FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        final String uri = request.getRequestURI();
        Matcher matcher = null;
        for (int i = 0; i < urlPattern.size() && matcher == null; i++) {
            Pattern pattern = urlPattern.get(i);
            final Matcher match = pattern.matcher(uri);
            if (match.matches()) {
                matcher = match;
            }
        }
        if(matcher == null) {
            // check servlets
        } else {
            getFilter().doFilter(request, response, filterChain);
        }
    }

    public void addServletName(String name) {
        servletName.add(name);
    }

    public void addUrlPattern(String value) {
        urlPattern.add(Pattern.compile(value));
    }

    public Filter getFilter() throws ServletException {
        if (filter == null) {
            final Class<? extends Filter> aClass;
            try {
                aClass = (Class<? extends Filter>) getClass().getClassLoader().loadClass(filterClass);
                filter = aClass.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new ServletException(e.getMessage(), e);
            }
        }
        return filter;
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    public void setAsyncSuppported(boolean asyncSuppported) {
        this.asyncSuppported = asyncSuppported;
    }
}
