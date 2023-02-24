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
import java.util.ArrayList;
import java.util.List;

public class ObjectTree {

    File file;
    List<ObjectTree> childs = new ArrayList<>();

    public ObjectTree() {

    }

    public void add(ObjectTree child) {
        childs.add(child);
    }

    public void remove(ObjectTree child) {
        childs.remove(child);
    }

    public File getFile() {
        return file;
    }

    public List<ObjectTree> getChilds() {
        return childs;
    }

}
