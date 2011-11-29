package org.milo.servlet;

import org.milo.deployment.FilterHolder;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class FilterHolderTest {
    public void mapping() {
        FilterHolder holder = new FilterHolder();
        holder.addUrlPattern("/*");
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/loblaw")));

        holder = new FilterHolder();
        holder.addUrlPattern("/bob/*");
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/loblaw")));
        Assert.assertFalse(holder.matches(new DummyServletRequest("/foo")));
        Assert.assertTrue(holder.matches(new DummyServletRequest("/bob/bar/boo")));

        holder = new FilterHolder();
        holder.addUrlPattern("*.jsp");
        Assert.assertTrue(holder.matches(new DummyServletRequest("/index.jsp")));
        Assert.assertFalse(holder.matches(new DummyServletRequest("/api/login")));

    }

}
