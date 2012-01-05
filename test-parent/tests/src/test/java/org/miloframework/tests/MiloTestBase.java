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
package org.miloframework.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.miloframework.grizzly.GrizzlyServletContainer;
import org.testng.Assert;
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

    protected String read(InputStream content) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int read;
        final byte[] bytes = new byte[4096];
        while((read = content.read(bytes)) != -1) {
            stream.write(bytes, 0, read);
        }

        return new String(stream.toByteArray());
    }

    protected void validateHeader(HttpResponse responseBody, final String name, final String value) {
        final Header[] names = responseBody.getHeaders(name);
        Assert.assertEquals(names[0].getValue(), value);
    }
}
