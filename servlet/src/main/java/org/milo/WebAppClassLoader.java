package org.milo;

public class WebAppClassLoader extends ClassLoader {
    private String root;

    public WebAppClassLoader(String root) {
        this.root = root;
    }
}
