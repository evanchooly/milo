package com.antwerkz.milo;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.antwerkz.milo.deployment.FilterHolder;
import com.antwerkz.milo.deployment.FilterType;
import com.antwerkz.milo.deployment.TrueFalseType;

public class FilterChainImpl implements FilterChain {
    private final Map<String, FilterHolder> filters = new LinkedHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        for (FilterHolder holder : filters.values()) {
            
        }
    }

    public void add(FilterType filterType) {
        final FilterHolder holder = new FilterHolder(this);
        final String name = filterType.getFilterName().getValue();
        holder.setFilterClass(filterType.getFilterClass().getValue());
        final TrueFalseType asyncSupported = filterType.getAsyncSupported();
        holder.setAsyncSuppported(asyncSupported != null && asyncSupported.isValue());
        filters.put(name, holder);
    }

    public FilterHolder getFilter(String name) {
        return filters.get(name);
    }
}