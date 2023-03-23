/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.SpinObject.LinkDataObject;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Node;

public abstract class ObjectCompiler {

    public abstract void compile(Node root);

    public abstract void compilePass2();

    public abstract boolean hasErrors();

    public abstract Map<String, Expression> getPublicSymbols();

    public abstract int getVarSize();

    public abstract SpinObject generateObject();

    public abstract SpinObject generateObject(int memoryOffset);

    public abstract List<LinkDataObject> getObjectLinks();

}
