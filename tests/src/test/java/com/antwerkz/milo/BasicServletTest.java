package com.antwerkz.milo;

import java.io.IOException;
import javax.servlet.ServletException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(dataProvider = "containers")
public class BasicServletTest extends MiloTestBase {
    public void deployServlet(ServletContainer container) throws IOException, ServletException {
        try {
            container.start();
            container.createContext("ROOT", "/", "target/tests");
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse responseBody = httpclient.execute(new HttpGet("http://localhost:" + 8080 + "/root"));
            final StatusLine statusLine = responseBody.getStatusLine();
            Assert.assertNotEquals(statusLine.getStatusCode(), 404, "Should not have returned a 404");
            validate(responseBody, "listener", ServletListener.class.getName());
            validate(responseBody, "name", "value");
            validate(responseBody, "name2", "value2");
            validate(responseBody, "name3", "value3");
        } finally {
            container.stop();
        }
    }

    private void validate(HttpResponse responseBody, final String name, final String value) {
        final Header[] names = responseBody.getHeaders(name);
        Assert.assertTrue(names.length == 1 && names[0].getValue().equals(value));
    }

}
