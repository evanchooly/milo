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
package org.milo.servlet;

import org.milo.deployment.FilterHolder;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class FilterHolderTest {
    public void mapping() {
        FilterHolder holder = new FilterHolder();
        holder.addUrlPattern("/*");
        holder.addServletName("Dr. Dorian");
        final String name = "TestServlet";
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/loblaw"), name));

        holder = new FilterHolder();
        holder.addUrlPattern("/bob/*");
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/loblaw"), name));
        Assert.assertFalse(holder.matches(new DummyServletRequest("/foo"), "Doug"));
        Assert.assertFalse(holder.matches(new DummyServletRequest("/foo"), name));
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/bar/boo"), name));

        holder = new FilterHolder();
        holder.addUrlPattern("*.jsp");
        Assert.assertTrue(holder.matches(new DummyServletRequest("/index.jsp"), name));
        Assert.assertFalse(holder.matches(new DummyServletRequest("/api/login"), name));

    }

}
