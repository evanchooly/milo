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
package org.miloframework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.miloframework.deployment.FilterHolder;
import org.miloframework.deployment.FilterType;
import org.miloframework.deployment.ServletHolder;
import org.miloframework.deployment.TrueFalseType;

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