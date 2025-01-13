/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller;

import java.io.File;

public class SourceLocation {

    public int offset;
    public int topPixel;
    public File file;

    public SourceLocation(File file, int offset, int topPixel) {
        this.file = file;
        this.offset = offset;
        this.topPixel = topPixel;
    }

}
