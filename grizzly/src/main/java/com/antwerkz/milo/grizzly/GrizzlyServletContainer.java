package com.antwerkz.milo.grizzly;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.antwerkz.milo.ServletContainer;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class GrizzlyServletContainer extends ServletContainer {
    private final String host;
    private final int port;
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
        this.host = host;
        this.port = port;
        
        server = new HttpServer();
        server.addListener(new NetworkListener("milo", this.host, this.port));
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

    @Override
    public boolean add(final HttpServlet servlet, String... mappings) {
        server.getServerConfiguration().addHttpHandler(new HttpHandler() {
            @Override
            public void service(Request request, Response response) throws Exception {
                servlet.service(createRequest(request), createResponse(response));
            }
        }, mappings);
        return true;
    }

    private HttpServletRequest createRequest(Request request) {
        return new MiloHttpServletRequest(request);
    }

    private HttpServletResponse createResponse(Response response) {
        return new MiloHttpServletResponse(response);
    }

}