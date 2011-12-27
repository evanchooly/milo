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
package org.miloframework.servlet;

import javax.servlet.Servlet;

import org.miloframework.deployment.FilterHolder;
import org.miloframework.deployment.ServletHolder;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class FilterHolderTest {
    public void mapping() {
        FilterHolder holder = new FilterHolder();
        holder.addUrlPattern("/*");
        holder.addServletName("Dr. Dorian");
        final String name = "TestServlet";
        final ServletHolder servletHolder = new ServletHolder(null, name, Servlet.class);
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/loblaw"), servletHolder));
        holder = new FilterHolder();
        holder.addUrlPattern("/bob/*");
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/loblaw"), servletHolder));
        Assert.assertFalse(holder.matches(new DummyServletRequest("/foo"), new ServletHolder(null, "Doug", Servlet.class)));
        Assert.assertFalse(holder.matches(new DummyServletRequest("/foo"), servletHolder));
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/bar/boo"), servletHolder));
        holder = new FilterHolder();
        holder.addUrlPattern("*.jsp");
        Assert.assertTrue(holder.matches(new DummyServletRequest("/index.jsp"), servletHolder));
        Assert.assertFalse(holder.matches(new DummyServletRequest("/api/login"), servletHolder));

    }

}
