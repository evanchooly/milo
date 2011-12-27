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

public class WebAppClassLoader extends URLClassLoader {
    public WebAppClassLoader(String root) {
        super(new URL[]{}, null);
        loadWarInfo(root);
        loadParentData();
    }

    private void loadWarInfo(String root) {
        File rootDir = new File(root);
        try {
            addURL(new File(rootDir, "classes").toURI().toURL());
            final File[] files = new File(rootDir, "lib").listFiles(new FilenameFilter() {
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

    private void loadParentData() {
        final ClassLoader parent = getClass().getClassLoader();
        if (parent instanceof URLClassLoader) {
            for (URL url : ((URLClassLoader) parent).getURLs()) {
                addURL(url);
            }
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
