package com.antwerkz.milo;

import java.io.File;
import java.util.Set;
import javax.servlet.ServletException;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test(dataProvider = "containers")
public class ContextTest extends MiloTestBase {
    public void getResourcePaths(ServletContainer container) throws ServletException {
        final MiloServletContext context = container.createContext("ROOT", "/", "target");
        validate(context.getResourcePaths("/"));
        validate(context.getResourcePaths("/test-classes/com/.."));
        Assert.assertNull(context.getResourcePaths(null));
    }

    @Test(dataProvider = "containers", expectedExceptions = {IllegalArgumentException.class})
    public void badPath(ServletContainer container) throws ServletException {
        final MiloServletContext context = container.createContext("ROOT", "/", "target");
        validate(context.getResourcePaths("test-classes/com/.."));
    }

    private void validate(Set<String> paths) {
        Assert.assertFalse(paths.isEmpty());
        for (String path : paths) {
            final File file = new File(path);
            if(file.isDirectory()) {
                Assert.assertTrue(path.endsWith("/"));
            } else {
                Assert.assertFalse(path.endsWith("/"));
            }
        }
    }
}
