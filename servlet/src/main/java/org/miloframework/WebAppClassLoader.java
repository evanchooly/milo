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
package org.miloframework;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class WebappClassLoader extends URLClassLoader {
    private ClassLoader parent;

    public WebappClassLoader(String root, ClassLoader parent) {
        super(new URL[]{}, null);
        this.parent = parent;
        loadWarInfo(root);
    }

    private void loadWarInfo(String root) {
        File rootDir = new File(root);
        try {
            addURL(new File(rootDir, "WEB-INF/classes").toURI().toURL());
            final File[] files = new File(rootDir, "WEB-INF/lib").listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".zip") || name.endsWith(".jar");
                }
            });
            if (files != null) {
                for (File file : files) {
                    addURL(file.toURI().toURL());
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if(isSystemClass(name)) {
            return parent.loadClass(name);
        }
        return super.loadClass(name, resolve);
    }

    private boolean isSystemClass(String name) {
        return name.startsWith("java.") || name.startsWith("javax.servlet.");
    }
}
