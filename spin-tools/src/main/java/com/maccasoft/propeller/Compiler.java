/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.internal.FileUtils;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.model.Token;

public abstract class Compiler {

    public static class ObjectInfo {

        public File file;
        public ObjectCompiler compiler;
        public Map<String, Expression> parameters;

        public String text;
        public long offset;
        public Expression count;

        public RootNode root;

        public ObjectInfo(File file, ObjectCompiler compiler, Map<String, Expression> parameters) {
            this.file = file;
            this.compiler = compiler;
            this.parameters = parameters;
        }

        public ObjectInfo(ObjectInfo info, Expression count) {
            this.file = info.file;
            this.compiler = info.compiler;
            this.parameters = info.parameters;
            this.count = count;
            this.root = info.root;
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

        final List<File> searchPaths;

        public FileSourceProvider(File[] searchPaths) {
            this.searchPaths = new ArrayList<>(Arrays.asList(searchPaths));
        }

        @Override
        public File getFile(String name) {
            File localFile = new File(name);
            if (localFile.exists()) {
                File parent = localFile.getParentFile();
                if (!searchPaths.contains(parent)) {
                    searchPaths.add(parent);
                }
                return localFile;
            }

            for (File file : searchPaths) {
                localFile = new File(file, name);
                if (localFile.exists()) {
                    File parent = localFile.getParentFile();
                    if (!searchPaths.contains(parent)) {
                        searchPaths.add(parent);
                    }
                    return localFile;
                }
            }

            return null;
        }

    }

    protected SourceProvider sourceProvider;

    boolean caseSensitive;
    Map<String, List<Token>> defines = new HashMap<>();

    boolean debugEnabled;
    boolean removeUnusedMethods;

    boolean warnUnusedMethods;
    boolean warnUnusedMethodVariables;
    boolean warnUnusedVariables;
    boolean warnRemovedUnusedMethods;

    protected boolean errors;
    protected List<CompilerException> messages = new ArrayList<CompilerException>();

    protected RootNode root;
    protected ObjectTree tree;

    public Compiler() {

    }

    public Compiler(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void setSourceProvider(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public boolean removeUnusedMethods() {
        return removeUnusedMethods;
    }

    public void setRemoveUnusedMethods(boolean removeUnusedMethods) {
        this.removeUnusedMethods = removeUnusedMethods;
    }

    public boolean warnUnusedMethods() {
        return warnUnusedMethods;
    }

    public void setWarnUnusedMethods(boolean warnUnusedMethods) {
        this.warnUnusedMethods = warnUnusedMethods;
    }

    public boolean warnUnusedMethodVariables() {
        return warnUnusedMethodVariables;
    }

    public void setWarnUnusedMethodVariables(boolean warnUnusedMethodVariables) {
        this.warnUnusedMethodVariables = warnUnusedMethodVariables;
    }

    public boolean warnUnusedVariables() {
        return warnUnusedVariables;
    }

    public void setWarnUnusedVariables(boolean warnUnusedVariables) {
        this.warnUnusedVariables = warnUnusedVariables;
    }

    public boolean warnRemovedUnusedMethods() {
        return warnRemovedUnusedMethods;
    }

    public void setWarnRemovedUnusedMethods(boolean warnRemovedUnusedMethods) {
        this.warnRemovedUnusedMethods = warnRemovedUnusedMethods;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }

    public SpinObject compile(File file) throws Exception {
        String text = getSource(file.getAbsolutePath());
        if (text == null) {
            throw new FileNotFoundException();
        }
        SpinObject object = compile(file, text);
        if (hasErrors()) {
            throw new RuntimeException("Compile failed");
        }
        return object;
    }

    public abstract SpinObject compile(File file, String text);

    public abstract Context getContext();

    public RootNode getRoot() {
        return root;
    }

    public ObjectInfo getObjectInfo(ObjectCompiler parent, File file, Map<String, Expression> parameters) throws Exception {
        return null;
    }

    protected void logMessage(CompilerException message) {
        if (message.type == CompilerException.ERROR) {
            errors = true;
        }
        messages.add(message);
    }

    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerException> getMessages() {
        return messages;
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

    public RootNode getParsedSource(File file) {
        if (sourceProvider != null) {
            RootNode node = sourceProvider.getParsedSource(file);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    public String getSource(File file) {
        if (sourceProvider != null) {
            String text = sourceProvider.getSource(file);
            if (text != null) {
                return text;
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

    public byte[] getBinaryFile(String fileName) {
        return getResource(fileName);
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

    public abstract void addDefine(String key, String value);

    public void addDefine(String key, List<Token> value) {
        this.defines.put(key, value);
    }

    public Map<String, List<Token>> getDefines() {
        return defines;
    }

}
