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

import org.eclipse.jface.viewers.StructuredSelection;

public class SourceSelection extends StructuredSelection {

    public int line;
    public int column;

    public SourceSelection(Object element) {
        super(element);
    }

    public SourceSelection(Object element, int line, int column) {
        super(element);
        this.line = line;
        this.column = column;
    }

}
