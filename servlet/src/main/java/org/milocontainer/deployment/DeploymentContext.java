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
package org.milocontainer.deployment;

import java.io.File;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.milocontainer.FilterSet;
import org.milocontainer.MiloServletContext;

@SuppressWarnings({"unchecked"})
public class DeploymentContext {
    private final Map<String, ServletHolder> mappings = new HashMap<>();
    private Map<String, String> initParams = new HashMap<>();
    private Map<String, ServletHolder> servletHolders = new HashMap<>();
    private List<String> listeners = new ArrayList<>();
    private MiloServletContext servletContext;
    private FilterSet filterSet;
    private ClassLoader classLoader;

    public DeploymentContext(MiloServletContext servletContext) {
        this.servletContext = servletContext;
        classLoader = servletContext.getClassLoader();
        filterSet = new FilterSet(this.servletContext);
    }

    public void setServletContext(MiloServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @SuppressWarnings({"unchecked"})
    public void load(File file) throws ServletException {
        try {
            JAXBContext jc = JAXBContext.newInstance("org.milocontainer.deployment");
            final WebAppType webApp = ((JAXBElement<WebAppType>) jc.createUnmarshaller().unmarshal(file))
                .getValue();
            for (JAXBElement<?> jaxbElement : webApp.getModuleNameOrDescriptionAndDisplayName()) {
                parse(jaxbElement.getValue());
            }
            if (filterSet != null) {
                filterSet.init(servletContext);
            }
        } catch (JAXBException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    private void parse(final Object value) throws ServletException {
        if (value instanceof ServletType) {
            parse((ServletType) value);
        } else if (value instanceof ServletMappingType) {
            parse((ServletMappingType) value);
        } else if (value instanceof FilterType) {
            parse((FilterType) value);
        } else if (value instanceof FilterMappingType) {
            parse((FilterMappingType) value);
        } else if (value instanceof ParamValueType) {
            parse((ParamValueType) value);
        } else if (value instanceof ListenerType) {
            parse((ListenerType) value);
        } else if (value instanceof ErrorPageType) {
            parse((ErrorPageType) value);
        } else {
            throw new ServletException("Unknown configuration element: " + value.getClass());
        }
    }

    private void parse(ErrorPageType value) {
        System.out.println("value = " + value);
    }

    private void parse(ParamValueType value) {
        initParams.put(value.getParamName().getValue(), value.getParamValue().getValue());
    }

    private void parse(ListenerType listenerType) {
        listeners.add(listenerType.getListenerClass().getValue());
    }

    private void parse(FilterType value) {
        filterSet.add(value);
    }

    private void parse(ServletType value) throws ServletException {
        final String name = value.getServletName().getValue();
        try {
            final Class<Servlet> clazz = (Class<Servlet>) getClassLoader().loadClass(
                value.getServletClass().getValue());
            final ServletHolder holder = new ServletHolder(servletContext, name, clazz);
            final String loadOnStartup = value.getLoadOnStartup();
            if (loadOnStartup != null) {
                holder.setLoadOnStartup(Integer.valueOf(loadOnStartup));
            }
            servletHolders.put(name, holder);
        } catch (ClassNotFoundException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    private void parse(FilterMappingType value) throws ServletException {
        final String name = value.getFilterName().getValue();
        FilterHolder holder = filterSet.getFilter(name);
        final List<Object> mappingInfo = value.getUrlPatternOrServletName();
        for (Object info : mappingInfo) {
            if (info instanceof ServletNameType) {
                holder.addServletName(((ServletNameType) info).getValue());
            } else {
                holder.addUrlPattern(((UrlPatternType) info).getValue());
            }
        }
    }

    private void parse(ServletMappingType value) {
        final List<UrlPatternType> urlPatterns = value.getUrlPattern();
        final String name = value.getServletName().getValue();
        ServletHolder holder = servletHolders.get(name);
        for (UrlPatternType pattern : urlPatterns) {
            String uri = pattern.getValue();
            if (!uri.startsWith("/")) {
                uri = "/" + uri;
            }
            mappings.put(uri, holder);
        }
    }

    public Map<String, ServletHolder> getMappings() {
        return mappings;
    }

    public Map<String, String> getInitParams() {
        return initParams;
    }

    public List<String> getListeners() {
        return listeners;
    }

    public FilterSet getFilterSet() {
        return filterSet;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}