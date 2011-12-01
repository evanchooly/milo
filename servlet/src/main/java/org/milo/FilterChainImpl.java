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
package org.milo;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.milo.deployment.FilterHolder;
import org.milo.deployment.ServletHolder;

public class FilterChainImpl implements FilterChain {
    private Iterator<FilterHolder> holders;
    private ServletHolder servlet;

    public FilterChainImpl(ServletHolder servlet, List<FilterHolder> holders) {
        this.servlet = servlet;
        this.holders = holders.iterator();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (!holders.hasNext()) {
            servlet.loadServlet().service(request, response);
        } else {
            boolean matched = false;
            while (!matched && holders.hasNext()) {
                final FilterHolder holder = holders.next();
                if (holder.matches(request, servlet.getName())) {
                    matched = true;
                    holder.doFilter(request, response, this);
                }
            }
        }
    }
}
