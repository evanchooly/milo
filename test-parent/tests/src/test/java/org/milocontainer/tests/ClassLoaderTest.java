package org.milocontainer.tests;

import org.milocontainer.WebAppClassLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ClassLoaderTest {
    @Test
    public void basicServlet() throws ClassNotFoundException {
        final ClassLoader classLoader = getClass().getClassLoader();
        WebAppClassLoader cl = new WebAppClassLoader("../basic/target/basic/", classLoader);
        final Class<?> aClass = cl.loadClass("org.milocontainer.BasicServlet");
        Assert.assertNotEquals(aClass.getClassLoader(), classLoader);
    }
}
