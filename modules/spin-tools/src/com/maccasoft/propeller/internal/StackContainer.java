/*
 * Copyright (c) 2023 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class StackContainer {

    Composite container;
    StackLayout layout;

    public StackContainer(Composite parent, int style) {
        layout = new StackLayout();

        container = new Composite(parent, style);
        container.setLayout(layout);
    }

    public void setTopControl(Control control) {
        layout.topControl = control;
        container.layout();
    }

    public Composite getContainer() {
        return container;
    }

    public void setVisible(boolean visible) {
        container.setVisible(visible);
    }

}
