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

public interface ObjectCompiler {

    public SpinObject compileObject(Node root);

    public void compile(Node root);

    public void compilePass2();

    public boolean hasErrors();

    public Map<String, Expression> getPublicSymbols();

    public int getVarSize();

    public SpinObject generateObject();

    public SpinObject generateObject(int memoryOffset);

    public List<LinkDataObject> getObjectLinks();

}
