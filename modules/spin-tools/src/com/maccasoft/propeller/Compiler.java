/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.SourceProvider;

public abstract class Compiler {

    public static class ObjectInfo {

        public File file;
        public ObjectCompiler compiler;
        public Map<String, Expression> parameters;

        public long offset;
        public Expression count;

        public ObjectInfo(File file, ObjectCompiler compiler, Map<String, Expression> parameters) {
            this.file = file;
            this.compiler = compiler;
            this.parameters = parameters;
        }

        public ObjectInfo(File file, ObjectCompiler compiler, Expression count, Map<String, Expression> parameters) {
            this.file = file;
            this.compiler = compiler;
            this.count = count;
            this.parameters = parameters;
        }

        public ObjectInfo(ObjectInfo info, Expression count) {
            this.file = info.file;
            this.compiler = info.compiler;
            this.parameters = info.parameters;
            this.count = count;
        }

        public boolean hasErrors() {
            return compiler.hasErrors();
        }

        @Override
        public int hashCode() {
            return Objects.hash(file, parameters);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ObjectInfo other = (ObjectInfo) obj;
            return Objects.equals(file, other.file) && Objects.equals(parameters, other.parameters);
        }

    }

    public static class FileSourceProvider extends SourceProvider {

        final File[] searchPaths;
        final List<File> collectedSearchPaths;

        public FileSourceProvider(File[] searchPaths) {
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

    }

    protected ObjectTree tree;

    protected SourceProvider sourceProvider;

    boolean caseSensitive;

    public Compiler() {

    }

    public Compiler(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void setSourceProvider(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
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

    public abstract SpinObject compile(File file) throws Exception;

    public abstract SpinObject compile(File rootFile, Node root);

    public ObjectInfo getObjectInfo(ObjectCompiler parent, File file, Map<String, Expression> parameters) throws Exception {
        return null;
    }

    public boolean hasErrors() {
        return false;
    }

    public List<CompilerException> getMessages() {
        return Collections.emptyList();
    }

    public File getFile(String name, String... extensions) {
        if (sourceProvider != null) {
            for (String suffix : extensions) {
                File file = sourceProvider.getFile(name + suffix);
                if (file != null) {
                    return file;
                }
            }

            File file = sourceProvider.getFile(name);
            if (file != null) {
                return file;
            }

        }
        return null;
    }

    public Node getParsedSource(File file) {
        if (sourceProvider != null) {
            Node node = sourceProvider.getParsedSource(file);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    protected String getSource(String name) {
        if (sourceProvider != null) {
            File file = sourceProvider.getFile(name);
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
        if (sourceProvider != null) {
            File file = sourceProvider.getFile(name);
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

    protected ObjectTree buildFrom(ObjectCompiler root) {
        ObjectTree tree = new ObjectTree(root.getFile(), root.getFile().getName());
        for (ObjectCompiler child : root.childs) {
            tree.add(buildFrom(child));
        }
        return tree;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

}
