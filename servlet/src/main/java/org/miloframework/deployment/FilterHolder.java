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
package org.miloframework.deployment;

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

import org.miloframework.FilterChainImpl;
import org.miloframework.MiloServletContext;

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

    public boolean matches(ServletRequest request, ServletHolder holder) {
        if (!(request instanceof HttpServletRequest)) {
            return false;
        }
        boolean matched = false;
        final String uri = ((HttpServletRequest) request).getRequestURI();
        final Iterator<Pattern> iterator = urlPatterns.iterator();
        while (iterator.hasNext() && !matched) {
            matched = iterator.next().matcher(uri).matches();
        }
        if (!matched && holder != null) {
            matched = servletNames.contains(holder.getName());
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("FilterHolder{");
        sb.append("servletNames=").append(servletNames);
        sb.append(", filterClass='").append(filterClass).append('\'');
        sb.append(", urlPatterns=").append(urlPatterns);
        sb.append(", asyncSuppported=").append(asyncSuppported);
        sb.append('}');
        return sb.toString();
    }
}
