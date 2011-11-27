package org.milo.grizzly;

import javax.servlet.http.Cookie;

public class GrizzlyCookie extends Cookie {
    private org.glassfish.grizzly.http.Cookie cookie;

    public GrizzlyCookie(org.glassfish.grizzly.http.Cookie cookie) {
        super(cookie.getName(), cookie.getValue());
        this.cookie = cookie;
    }

    @Override
    public void setComment(String purpose) {
        cookie.setComment(purpose);
    }

    @Override
    public String getComment() {
        return cookie.getComment();
    }

    @Override
    public void setDomain(String domain) {
        cookie.setDomain(domain);
    }

    @Override
    public String getDomain() {
        return cookie.getDomain();
    }

    @Override
    public void setMaxAge(int expiry) {
        cookie.setMaxAge(expiry);
    }

    @Override
    public int getMaxAge() {
        return cookie.getMaxAge();
    }

    @Override
    public void setPath(String uri) {
        cookie.setPath(uri);
    }

    @Override
    public String getPath() {
        return cookie.getPath();
    }

    @Override
    public void setSecure(boolean flag) {
        cookie.setSecure(flag);
    }

    @Override
    public boolean getSecure() {
        return cookie.isSecure();
    }

    @Override
    public String getName() {
        return cookie.getName();
    }

    @Override
    public void setValue(String newValue) {
        cookie.setValue(newValue);
    }

    @Override
    public String getValue() {
        return cookie.getValue();
    }

    @Override
    public int getVersion() {
        return cookie.getVersion();
    }

    @Override
    public void setVersion(int v) {
        cookie.setVersion(v);
    }

    @Override
    public void setHttpOnly(boolean isHttpOnly) {
        cookie.setHttpOnly(isHttpOnly);
    }

    @Override
    public boolean isHttpOnly() {
        return cookie.isHttpOnly();
    }
}