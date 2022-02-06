/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2.instructions;

import com.maccasoft.propeller.spin2.Spin2Context;
import com.maccasoft.propeller.spin2.Spin2InstructionObject;

public class FileInc extends Spin2InstructionObject {

    byte[] data;

    public FileInc(Spin2Context context, byte[] data) {
        super(context);
        this.data = data;
    }

    @Override
    public int getSize() {
        return data.length;
    }

    @Override
    public byte[] getBytes() {
        return data;
    }

}
