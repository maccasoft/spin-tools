/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.SourceProvider;

public abstract class Compiler {

    public static class ObjectInfo {

        public ObjectCompiler compiler;

        public long offset;
        public Expression count;

        public ObjectInfo(ObjectCompiler compiler) {
            this.compiler = compiler;
        }

        public ObjectInfo(ObjectCompiler compiler, Expression count) {
            this.compiler = compiler;
            this.count = count;
        }

        public boolean hasErrors() {
            return compiler.hasErrors();
        }

    }

    public static class FileSourceProvider extends SourceProvider {

        final String suffix;
        final File[] searchPaths;

        final List<File> collectedSearchPaths;

        public FileSourceProvider(String suffix, File[] searchPaths) {
            this.suffix = suffix;
            this.searchPaths = searchPaths;
            this.collectedSearchPaths = new ArrayList<>();
        }

        @Override
        public File getFile(String name) {
            File localFile = new File(name);
            if (localFile.exists()) {
                File parent = localFile.getParentFile();
                if (!collectedSearchPaths.contains(parent)) {
                    collectedSearchPaths.add(parent);
                }
                return localFile;
            }

            for (File file : collectedSearchPaths) {
                localFile = new File(file, name);
                if (localFile.exists()) {
                    File parent = localFile.getParentFile();
                    if (!collectedSearchPaths.contains(parent)) {
                        collectedSearchPaths.add(parent);
                    }
                    return localFile;
                }
            }

            for (int i = 0; i < searchPaths.length; i++) {
                localFile = new File(searchPaths[i], name);
                if (localFile.exists()) {
                    return localFile;
                }
            }

            return null;
        }

        @Override
        public Node getParsedSource(String name) {
            return null;
        }

    }

    protected ObjectTree tree;

    final List<SourceProvider> sourceProviders = new ArrayList<SourceProvider>();

    boolean caseSensitive;

    public Compiler() {

    }

    public void addSourceProvider(SourceProvider provider) {
        sourceProviders.add(provider);
    }

    public boolean isRemoveUnusedMethods() {
        return false;
    }

    public void setRemoveUnusedMethods(boolean removeUnusedMethods) {

    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void setDebugEnabled(boolean enabled) {

    }

    public abstract void compile(File file, OutputStream binary, PrintStream listing) throws Exception;

    public abstract SpinObject compile(File rootFile, Node root);

    public ObjectInfo getObjectInfo(String fileName) {
        return null;
    }

    public boolean hasErrors() {
        return false;
    }

    public List<CompilerException> getMessages() {
        return Collections.emptyList();
    }

    public File getFile(String name) {
        for (SourceProvider p : sourceProviders) {
            File file = p.getFile(name);
            if (file != null) {
                return file;
            }
        }
        return null;
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
            File file = p.getFile(name);
            if (file != null) {
                try {
                    return FileUtils.loadFromFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    protected byte[] getResource(String name) {
        for (SourceProvider p : sourceProviders) {
            File file = p.getFile(name);
            if (file != null) {
                try {
                    return FileUtils.loadBinaryFromFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    public Map<String, Expression> getPublicSymbols() {
        return Collections.emptyMap();
    }

    public ObjectTree getObjectTree() {
        return tree;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

}
