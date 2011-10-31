package com.antwerkz.milo;

import java.io.IOException;
import javax.servlet.ServletException;

import org.testng.annotations.Test;

@Test(dataProvider = "containers")
public class BasicServletTest extends MiloTestBase {
    public void deployServlet(ServletContainer container) throws IOException, ServletException {
        try {
            container.start();
            container.createContext("ROOT", "/", "target/tests");
        } finally {
            container.stop();
        }
    }

}
