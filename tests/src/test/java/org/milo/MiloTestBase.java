package org.milo;

import java.io.IOException;

import org.milo.grizzly.GrizzlyServletContainer;
import org.testng.annotations.DataProvider;

public class MiloTestBase {
    @DataProvider(name = "containers")
    public Object[][] containers() throws IOException {
        return new Object[][] {
            new Object[]{new GrizzlyServletContainer(8080)},
//            new Object[]{new MinaServletContainer(8080)},
//            new Object[]{new NettyServletContainer(8080)},
        };
    }
}
