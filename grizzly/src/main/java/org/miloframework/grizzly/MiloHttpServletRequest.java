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
package org.miloframework.grizzly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.glassfish.grizzly.http.server.Request;

public class MiloHttpServletRequest implements HttpServletRequest {
    private Request request;
    Cookie[] cookies;

    @Override
    public String getAuthType() {
        return request.getAuthType();
    }

    @Override
    public Cookie[] getCookies() {
        if(cookies == null) {
            final org.glassfish.grizzly.http.Cookie[] c = request.getCookies();
            final int length = c == null ? 0 : c.length;
            cookies = new Cookie[length];
            for (int i = 0; i < length; i++) {
                cookies[i] = new GrizzlyCookie(c[i]);
            }
        }
        return cookies;
    }

    @Override
    public long getDateHeader(String name) {
        return request.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return toEnumeration(request.getHeaders(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return toEnumeration(request.getHeaderNames());

    }

    private <T> Enumeration<T> toEnumeration(final Iterable<T> iterable) {
        List<T> list = new ArrayList<T>();
        for (T item : iterable) {
            list.add(item);
        }
        return Collections.enumeration(list);
    }

    @Override
    public int getIntHeader(String name) {
        return request.getIntHeader(name);
    }

    @Override
    public String getMethod() {
        return request.getMethod().getMethodString();
    }

    @Override
    public String getPathInfo() {
        return request.getRequestURI();
    }

    @Override
    public String getPathTranslated() {
        return null; //request.getPathTranslated();
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    @Override
    public String getQueryString() {
        return request.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    @Override
    public boolean isUserInRole(String role) {
        return false; //request.isUserInRole(role);
    }

    @Override
    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    @Override
    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    @Override
    public String getRequestURI() {
        return request.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(request.getRequestURL());
    }

    @Override
    public String getServletPath() {
        return null; //request.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null; //request.getSession(create);
    }

    @Override
    public HttpSession getSession() {
        return null; //request.getSession();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return request.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return request.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false; //request.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false; //request.authenticate(response);
    }

    @Override
    public void login(String username, String password) throws ServletException {
//        request.login(username, password);
    }

    @Override
    public void logout() throws ServletException {
//        request.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null; //request.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null; //request.getPart(name);
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(request.getAttributeNames());
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        request.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength() {
        return request.getContentLength();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final InputStream inputStream = request.getInputStream();
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(request.getParameterNames());
    }

    @Override
    public String[] getParameterValues(String name) {
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return request.getProtocol().getProtocolString();
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(request.getReader());
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(request.getLocales());
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null; //request.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return null; //request.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return request.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return request.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return null; //request.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null; //request.startAsync();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
        throws IllegalStateException {
        return null; //request.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {
        return false; //request.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return false; //request.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null; //request.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null; //request.getDispatcherType();
    }

    public MiloHttpServletRequest(Request request) {
        this.request = request;
    }

}
