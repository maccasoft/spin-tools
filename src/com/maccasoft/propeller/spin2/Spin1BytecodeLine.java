/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

public class Spin1BytecodeLine {

    Spin2Context scope;
    String label;

    public Spin1BytecodeLine(Spin2Context scope, String label) {
        this.scope = scope;
        this.label = label;
    }

    public Spin2Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

}
