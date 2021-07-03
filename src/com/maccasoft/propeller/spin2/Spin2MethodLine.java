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

public class Spin2MethodLine {

    Spin2Context scope;
    String label;

    Spin2StatementNode tree;
    String text;

    public Spin2MethodLine(Spin2Context scope, String label, Spin2StatementNode tree) {
        this.scope = new Spin2Context(scope);
        this.label = label;
        this.tree = tree;
    }

    public Spin2Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void resolve(int address) {
        scope.setAddress(address);
    }

    public Spin2StatementNode getTree() {
        return tree;
    }

}
