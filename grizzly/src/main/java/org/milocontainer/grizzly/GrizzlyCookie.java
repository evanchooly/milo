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
package org.milocontainer.grizzly;

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