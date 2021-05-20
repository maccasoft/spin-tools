/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class Spin2PAsmLine {

    Spin2Context scope;
    String label;
    String condition;
    String mnemonic;
    List<Spin2PAsmExpression> arguments;
    String effect;

    Spin2PAsmInstructionFactory instructionFactory;
    Spin2InstructionObject instructionObject;

    String originalText;

    public Spin2PAsmLine(Spin2Context scope, String label, String condition, String mnemonic, List<Spin2PAsmExpression> arguments, String effect) {
        this.scope = scope;
        this.label = label;
        this.condition = condition;
        this.mnemonic = mnemonic;
        this.arguments = arguments;
        this.effect = effect;
    }

    public Spin2Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

    public boolean isLocalLabel() {
        return label != null && label.startsWith(".");
    }

    public String getCondition() {
        return condition;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public List<Spin2PAsmExpression> getArguments() {
        return arguments;
    }

    public String getEffect() {
        return effect;
    }

    public Spin2PAsmInstructionFactory getInstructionFactory() {
        if (instructionFactory == null) {
            instructionFactory = Spin2PAsmInstructionFactory.get(mnemonic);
        }
        return instructionFactory;
    }

    public List<Spin2PAsmLine> expand() {
        try {
            return getInstructionFactory().expand(this);
        } catch (Exception e) {
            return Collections.singletonList(this);
        }
    }

    public int resolve(int address) {
        try {
            if (instructionObject == null) {
                instructionObject = getInstructionFactory().createObject(scope, condition, arguments, effect);
            }
            if (instructionObject != null) {
                return instructionObject.resolve(address);
            }
        } catch (Exception e) {
            System.err.println(this);
            e.printStackTrace();
        }
        return address;
    }

    public void generateObjectCode(OutputStream output) throws IOException {
        try {
            if (instructionObject == null) {
                instructionObject = getInstructionFactory().createObject(scope, condition, arguments, effect);
            }
            if (instructionObject != null) {
                instructionObject.generateObjectCode(output);
            }
        } catch (Exception e) {
            System.err.println(this);
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (label != null) {
            sb.append(label);
            sb.append(" ");
        }
        if (condition != null) {
            while (sb.length() < 8) {
                sb.append(" ");
            }
            sb.append(condition);
            sb.append(" ");
        }
        while (sb.length() < 20) {
            sb.append(" ");
        }
        if (mnemonic != null) {
            sb.append(mnemonic);
        }
        if (arguments.size() != 0) {
            while (sb.length() < 28) {
                sb.append(" ");
            }
            for (int i = 0; i < arguments.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(arguments.get(i).toString());
            }
        }
        if (effect != null) {
            sb.append(" ");
            while (sb.length() < 44) {
                sb.append(" ");
            }
            sb.append(effect);
        }
        return sb.toString();
    }

}
