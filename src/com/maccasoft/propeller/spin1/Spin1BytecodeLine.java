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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.maccasoft.propeller.spin1.bytecode.Constant;
import com.maccasoft.propeller.spin1.bytecode.Empty;
import com.maccasoft.propeller.spin1.bytecode.MemAddress;

public class Spin1BytecodeLine {

    Spin1Context scope;
    String label;
    String mnemonic;
    List<Spin1BytecodeExpression> arguments;

    Spin1BytecodeInstructionFactory instructionFactory;
    Spin1BytecodeInstructionObject instructionObject;

    //String originalText;
    List<String> annotations = new ArrayList<String>();

    public Spin1BytecodeLine(Spin1Context scope, String label, String mnemonic, List<Spin1BytecodeExpression> arguments) {
        this.scope = scope;
        this.label = label;
        this.mnemonic = mnemonic;
        this.arguments = arguments;

        if (mnemonic != null) {
            this.instructionFactory = Spin1BytecodeInstructionFactory.get(mnemonic);
            if (this.instructionFactory == null) {
                if (mnemonic.startsWith("@")) {
                    this.instructionFactory = new MemAddress();
                }
                else if (!"(".equals(mnemonic) && !",".equals(mnemonic)) {
                    this.instructionFactory = new Constant();
                }
            }
            if (this.instructionFactory == null) {
                this.instructionFactory = Empty.instance;
                //this.annotations.add("error: invalid instruction " + mnemonic);
            }
        }
    }

    public Spin1Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

    public boolean isLocalLabel() {
        return label != null && label.startsWith(":");
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public List<Spin1BytecodeExpression> getArguments() {
        return arguments;
    }

    public int getArgumentCount() {
        return arguments.size();
    }

    public Spin1BytecodeExpression getArgument(int index) {
        return arguments.get(index);
    }

    public Spin1BytecodeInstructionFactory getInstructionFactory() {
        return instructionFactory;
    }

    public List<Spin1BytecodeLine> expand() {
        if (instructionFactory != null) {
            return instructionFactory.expand(this);
        }
        return Collections.singletonList(this);
    }

    public int resolve(int address) {
        if (instructionObject == null && instructionFactory != null) {
            instructionObject = instructionFactory.createObject(this);
        }
        if (instructionObject != null) {
            return instructionObject.resolve(address);
        }
        return address;
    }

    public Spin1BytecodeInstructionObject getInstructionObject() {
        return instructionObject;
    }

    public void generateObjectCode(OutputStream output) throws IOException {
        if (instructionObject != null) {
            instructionObject.generateObjectCode(output);
        }
    }

    public void addAnnotation(String s) {
        annotations.add(s);
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (label != null) {
            sb.append(label);
            sb.append(" ");
        }
        while (sb.length() < 16) {
            sb.append(" ");
        }
        if (instructionFactory != null) {
            sb.append(instructionFactory.getClass().getSimpleName());
            sb.append(" ");
        }
        else if (mnemonic != null) {
            sb.append(mnemonic);
        }
        if (arguments.size() != 0) {
            while (sb.length() < 24) {
                sb.append(" ");
            }
            for (int i = 0; i < arguments.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(arguments.get(i).toString());
            }
        }
        return sb.toString();
    }

}
