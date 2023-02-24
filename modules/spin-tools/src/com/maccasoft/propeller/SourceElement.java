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

import com.maccasoft.propeller.model.ObjectNode;

public class SourceElement {

    public ObjectNode object;
    public int line;
    public int column;

    public SourceElement(ObjectNode object, int line, int column) {
        this.object = object;
        this.line = line;
        this.column = column;
    }

}
