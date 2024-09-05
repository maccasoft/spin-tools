/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Node;

public abstract class ObjectCompiler {

    ObjectCompiler parent;
    File file;
    protected Context scope;

    List<ObjectCompiler> childs = new ArrayList<>();

    boolean errors;
    List<CompilerException> messages = new ArrayList<>();

    public ObjectCompiler(File file, Context scope) {
        this.parent = null;
        this.file = file;
        this.scope = new Context(scope);
    }

    public ObjectCompiler(ObjectCompiler parent, File file, Context scope) {
        this.parent = parent;
        this.file = file;
        this.scope = new Context(scope);
        if (parent != null) {
            parent.childs.add(this);
        }
    }

    public ObjectCompiler getParent() {
        return parent;
    }

    public File getFile() {
        return file;
    }

    public Context getScope() {
        return scope;
    }

    public abstract SpinObject compileObject(Node root);

    public abstract void compileStep1(Node root);

    public abstract void compileStep2(boolean keepFirst);

    public abstract Map<String, Expression> getPublicSymbols();

    public abstract int getVarSize();

    public abstract SpinObject generateObject(int memoryOffset);

    public abstract List<LinkDataObject> getObjectLinks();

    protected void logMessage(CompilerException message) {
        message.fileName = file.getName();
        if (message.hasChilds()) {
            for (CompilerException msg : message.getChilds()) {
                logMessage(msg);
            }
        }
        else {
            if (message.type == CompilerException.ERROR) {
                errors = true;
            }
            if (!messages.contains(message)) {
                messages.add(message);
            }
        }
    }

    public boolean hasErrors() {
        return errors;
    }

    public List<CompilerException> getMessages() {
        return messages;
    }

}
