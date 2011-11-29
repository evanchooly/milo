package org.milo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.milo.deployment.FilterHolder;
import org.milo.deployment.FilterType;
import org.milo.deployment.ServletHolder;
import org.milo.deployment.TrueFalseType;

public class FilterSet {
    private final Map<String, FilterHolder> filters = new LinkedHashMap<>();

    public void add(FilterType filterType) {
        final FilterHolder holder = new FilterHolder();
        final String name = filterType.getFilterName().getValue();
        holder.setFilterClass(filterType.getFilterClass().getValue());
        final TrueFalseType asyncSupported = filterType.getAsyncSupported();
        holder.setAsyncSupported(asyncSupported != null && asyncSupported.isValue());
        filters.put(name, holder);
    }

    public FilterHolder getFilter(String name) {
        return filters.get(name);
    }

    public void init(MiloServletContext context) throws ServletException {
        for (Entry<String, FilterHolder> entry : filters.entrySet()) {
            entry.getValue().init(entry.getKey(), context);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, ServletHolder servlet)
        throws IOException, ServletException {
        new FilterChainImpl(servlet, new ArrayList<>(filters.values())).doFilter(request, response);
    }
}