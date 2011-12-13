package org.milo.tests;

import java.io.IOException;
import javax.servlet.ServletException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.milo.ServletContainer;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(dataProvider = "containers")
public class StaticResources extends MiloTestBase {
    public void rootFiles(ServletContainer container) throws IOException, ServletException {
        try {
            container.start();
            container.createContext("ROOT", "/", "tests/basic/target/basic");
            HttpClient httpclient = new DefaultHttpClient();
            assertBody(httpclient, "http://localhost:" + 8080 + "/foo.txt", "foo");
            assertBody(httpclient, "http://localhost:" + 8080 + "/sub/file.txt", "what up?");
        } finally {
            container.stop();
        }

    }

    private void assertBody(HttpClient httpclient, final String uri, final String body) throws IOException {
        Assert.assertTrue(read(httpclient.execute(new HttpGet(uri)).getEntity().getContent())
                .equals(body));
    }
}
