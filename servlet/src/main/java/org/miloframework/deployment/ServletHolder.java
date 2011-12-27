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

import java.lang.String;
import java.util.Enumeration;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;

import org.miloframework.MiloServletContext;

public class ServletHolder {
    private String name;
    private Class<? extends Servlet> klass;
    private Servlet cachedServlet;
    private final MiloServletContext servletContext;
    private Integer loadOnStartup = -1;

    public ServletHolder(MiloServletContext servletContext, String name, Class<? extends Servlet> clazz) {
        this.servletContext = servletContext;
        this.name = name;
        klass = clazz;
    }

    public String getName() {
        return name;
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
