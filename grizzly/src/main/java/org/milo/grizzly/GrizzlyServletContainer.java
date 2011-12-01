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
package org.milo.grizzly;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.milo.ServletContainer;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class GrizzlyServletContainer extends ServletContainer {
    private HttpServer server;

    /**
     * Create container with the default port of 8080
     */
    public GrizzlyServletContainer(String host) throws IOException {
        this(host, 8080);
    }

    /**
     * Create a container with the default host of localhost
     * @param port
     * @throws IOException
     */
    public GrizzlyServletContainer(int port) throws IOException {
        this("localhost", port);
    }

    public GrizzlyServletContainer(String host, int port) throws IOException {
        super(host, port);
        server = new HttpServer();
        server.addListener(new NetworkListener("milo", this.host, this.port));
         server.getServerConfiguration().addHttpHandler(new HttpHandler() {
            @Override
            public void service(Request request, Response response) throws Exception {
                GrizzlyServletContainer.this.service(createRequest(request), createResponse(response));
            }
        });
    }

    @Override
    public void start() throws IOException {
        server.start();
    }

    @Override
    public void stop() {
        server.stop();
    }

    @Override
    public String getMimeType(String fileName) {
        return org.glassfish.grizzly.http.server.util.MimeType.getByFilename(fileName);
    }

    private HttpServletRequest createRequest(Request request) {
        return new MiloHttpServletRequest(request);
    }

    private HttpServletResponse createResponse(Response response) {
        return new MiloHttpServletResponse(response);
    }
}