package org.milo.staticresources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticResourcesServlet extends HttpServlet {
    private File docRoot;

    public StaticResourcesServlet(File docRoot) {
        this.docRoot = docRoot;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File resource = new File(docRoot, req.getRequestURI());
        if(resource.isFile()) {
            Files.copy(resource.toPath(), resp.getOutputStream());
        } else {
            throw new ServletException("Directory listing not supported yet");
        }
    }
}
