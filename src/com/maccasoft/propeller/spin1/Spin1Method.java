/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.List;

public class Spin1Method {

    Spin1Context scope;
    List<Spin1BytecodeLine> source = new ArrayList<Spin1BytecodeLine>();

    int localSize;

    public Spin1Method(Spin1Context scope) {
        this.scope = scope;
        this.localSize = 4;
    }

    public Spin1Context getScope() {
        return scope;
    }

    public void expand() {
        List<Spin1BytecodeLine> lines = new ArrayList<Spin1BytecodeLine>();
        for (Spin1BytecodeLine line : source) {
            lines.addAll(line.expand());
        }
        lines.add(new Spin1BytecodeLine(new Spin1Context(scope), null, "return", null));
        source = lines;
    }

    public int resolve(int address) {
        scope.setAddress(address);
        return address;
    }

    public void setLocalSize(int localSize) {
        this.localSize = localSize;
    }

    public int getLocalSize() {
        return localSize;
    }

}
