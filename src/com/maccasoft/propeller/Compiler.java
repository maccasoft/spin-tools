/*
 * Copyright (c) 2022 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.model.Node;

public abstract class Compiler {

    public static abstract class SourceProvider {

        public Node getParsedSource(String name) {
            return null;
        }

        public abstract String getSource(String name);

        public abstract byte[] getResource(String name);
    }

    public static class FileSourceProvider extends SourceProvider {

        File[] searchPaths;

        public FileSourceProvider(File[] searchPaths) {
            this.searchPaths = searchPaths;
        }

        @Override
        public String getSource(String name) {
            File localFile = new File(name);
            if (localFile.exists()) {
                try {
                    return FileUtils.loadFromFile(localFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            for (int i = 0; i < searchPaths.length; i++) {
                localFile = new File(searchPaths[i], name);
                if (localFile.exists()) {
                    try {
                        return FileUtils.loadFromFile(localFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            return null;
        }

        @Override
        public byte[] getResource(String name) {
            File localFile = new File(name);
            if (localFile.exists()) {
                try {
                    return loadBinaryFromFile(localFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            for (int i = 0; i < searchPaths.length; i++) {
                localFile = new File(searchPaths[i], name);
                if (localFile.exists()) {
                    try {
                        return loadBinaryFromFile(localFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            return null;

        }

        byte[] loadBinaryFromFile(File file) throws Exception {
            try {
                InputStream is = new FileInputStream(file);
                try {
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return b;
                } finally {
                    try {
                        is.close();
                    } catch (Exception e) {

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    final List<SourceProvider> sourceProviders = new ArrayList<SourceProvider>();

    public Compiler() {

    }

    public void addSourceProvider(SourceProvider provider) {
        sourceProviders.add(provider);
    }

    public void setRemoveUnusedMethods(boolean removeUnusedMethods) {

    }

    public void setDebugEnabled(boolean enabled) {

    }

    public abstract void compile(File file, OutputStream binary, PrintStream listing) throws Exception;

    public abstract SpinObject compile(String rootFileName, Node root);

    public boolean hasErrors() {
        return false;
    }

    public List<CompilerException> getMessages() {
        return Collections.emptyList();
    }

    protected Node getParsedSource(String name) {
        for (SourceProvider p : sourceProviders) {
            Node node = p.getParsedSource(name);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    protected String getSource(String name) {
        for (SourceProvider p : sourceProviders) {
            String text = p.getSource(name);
            if (text != null) {
                return text;
            }
        }
        return null;
    }

    protected byte[] getResource(String name) {
        for (SourceProvider p : sourceProviders) {
            byte[] data = p.getResource(name);
            if (data != null) {
                return data;
            }
        }
        return null;
    }

    public abstract String getObjectTree();
}
