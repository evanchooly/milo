package com.antwerkz.milo.deployment;

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

import com.antwerkz.milo.MiloServletContext;

@SuppressWarnings({"unchecked"})
public class DeploymentContext {
    private final Map<String, ServletHolder> mappings = new HashMap<>();
    private Map<String, String> initParams = new HashMap<>();
    private Map<String, ServletHolder> holders = new HashMap<>();
    private List<String> listeners = new ArrayList<>();
    private MiloServletContext servletContext;

    public void setServletContext(MiloServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @SuppressWarnings({"unchecked"})
    public void load(File file) throws ServletException {
        try {
            JAXBContext jc = JAXBContext.newInstance("com.antwerkz.milo.deployment");
            final WebAppType webApp = ((JAXBElement<WebAppType>) jc.createUnmarshaller().unmarshal(file))
                .getValue();
            for (JAXBElement<?> jaxbElement : webApp.getModuleNameOrDescriptionAndDisplayName()) {
                dispatch(jaxbElement.getValue());
            }
        } catch (JAXBException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    private void dispatch(final Object value) throws ServletException {
        if (value instanceof ServletType) {
            load((ServletType) value);
        } else if (value instanceof ServletMappingType) {
            register((ServletMappingType) value);
        } else if (value instanceof ParamValueType) {
            record((ParamValueType) value);
        } else if (value instanceof ListenerType) {
            record((ListenerType) value);
        } else {
            throw new ServletException("Unknown configuration element: " + value.getClass());
        }
    }

    private void record(ListenerType listenerType) {
        final FullyQualifiedClassType listenerClassType = listenerType.getListenerClass();
        System.out.println("listenerClassType = " + listenerClassType.getValue());
        listeners.add(listenerClassType.getValue());
    }

    private void load(ServletType value) throws ServletException {
        final String name = value.getServletName().getValue();
        try {
            final Class<Servlet> clazz = (Class<Servlet>) getClass().getClassLoader().loadClass(
                value.getServletClass().getValue());
            final ServletHolder holder = new ServletHolder(name, clazz);
            holder.setServletContext(servletContext);
            final String loadOnStartup = value.getLoadOnStartup();
            if(loadOnStartup != null) {
                holder.setLoadOnStartup(Integer.valueOf(loadOnStartup));
            }
            holders.put(name, holder);
        } catch (ClassNotFoundException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    private void record(ParamValueType value) {
        initParams.put(value.getParamName().getValue(), value.getParamValue().getValue());
    }

    private void register(ServletMappingType value) {
        final List<UrlPatternType> urlPatterns = value.getUrlPattern();
        final String name = value.getServletName().getValue();
        ServletHolder holder = holders.get(name);
        for (UrlPatternType pattern : urlPatterns) {
            String uri = pattern.getValue();
            if(!uri.startsWith("/")) {
                uri = "/" + uri;
            }
            mappings.put(uri, holder);
        }
    }

    public Map<String,ServletHolder> getMappings() {
        return mappings;
    }

    public Map<String,String> getInitParams() {
        return initParams;
    }

    public List<String> getListeners() {
        return listeners;
    }
}