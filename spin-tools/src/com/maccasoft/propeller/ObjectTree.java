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
import java.util.List;

public class ObjectTree {

    File file;
    String name;
    ObjectTree parent;
    List<ObjectTree> childs = new ArrayList<>();

    public ObjectTree(File file, String name) {
        this.file = file;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ObjectTree getParent() {
        return parent;
    }

    public void add(ObjectTree child) {
        child.parent = this;
        childs.add(child);
    }

    public boolean hasChildren() {
        return childs.size() != 0;
    }

    public File getFile() {
        return file;
    }

    public ObjectTree[] getChilds() {
        return childs.toArray(new ObjectTree[0]);
    }

    public boolean contains(File file) {
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i).getFile().equals(file)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int indent) {
        StringBuilder sb = new StringBuilder();

        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                sb.append("     ");
            }
            sb.append(" +-- ");
        }

        sb.append(name);
        sb.append(System.lineSeparator());

        for (ObjectTree child : childs) {
            sb.append(child.toString(indent + 1));
        }

        return sb.toString();
    }
}
