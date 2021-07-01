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

public class Spin2Bytecode {

    Spin2Context context;
    String label;

    public Spin2Bytecode(Spin2Context context, String label) {
        this.context = new Spin2Context(context);
        this.label = label;
    }

    public Spin2Context getContext() {
        return context;
    }

    public String getLabel() {
        return label;
    }

    public void resolve(int address) {
        context.setAddress(address);
    }

    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return label != null ? label : "";
    }

}
