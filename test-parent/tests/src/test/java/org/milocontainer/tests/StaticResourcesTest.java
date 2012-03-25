package org.milocontainer.tests;

import java.io.IOException;
import javax.servlet.ServletException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.milocontainer.ServletContainer;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(dataProvider = "containers")
public class StaticResourcesTest extends MiloTestBase {
    public void rootFiles(ServletContainer container) throws IOException, ServletException {
        try {
            container.start();
            DefaultHttpClient httpclient = new DefaultHttpClient();
            container.createContext("ROOT", "/", "../basic/target/basic");
            assertBody(httpclient, "http://localhost:" + 8080 + "/foo.txt", "foo");
            assertBody(httpclient, "http://localhost:" + 8080 + "/sub/file.txt", "what up?");
        } finally {
            container.stop();
        }

    }

    private void assertBody(final HttpClient httpclient, final String uri, final String body) throws IOException {
        Assert.assertEquals(read(httpclient.execute(new HttpGet(uri)).getEntity().getContent()), body);
    }
}
