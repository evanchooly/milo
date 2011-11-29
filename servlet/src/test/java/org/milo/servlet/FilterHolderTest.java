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
