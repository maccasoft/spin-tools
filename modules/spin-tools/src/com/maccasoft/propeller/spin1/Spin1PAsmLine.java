/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
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

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.spin1.instructions.Empty;

public class Spin1PAsmLine {

    Context scope;
    String label;
    String condition;
    String mnemonic;
    List<Spin1PAsmExpression> arguments;
    String effect;

    Spin1PAsmInstructionFactory instructionFactory;
    Spin1InstructionObject instructionObject;

    List<CompilerException> annotations = new ArrayList<CompilerException>();
    Object data;

    public Spin1PAsmLine(Context scope, String label, String condition, String mnemonic, List<Spin1PAsmExpression> arguments, String effect) {
        this.scope = scope;
        this.label = label;
        this.condition = condition;
        this.mnemonic = mnemonic;
        this.arguments = arguments;
        this.effect = effect;

        if (mnemonic != null) {
            this.instructionFactory = Spin1PAsmInstructionFactory.get(mnemonic);
            if (this.instructionFactory == null) {
                throw new RuntimeException("invalid instruction " + mnemonic);
            }
        }
        if (this.instructionFactory == null) {
            this.instructionFactory = Empty.instance;
        }
    }

    public Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
    }

    public boolean isLocalLabel() {
        return label != null && label.startsWith(":");
    }

    public String getCondition() {
        return condition;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public List<Spin1PAsmExpression> getArguments() {
        return arguments;
    }

    public int getArgumentCount() {
        return arguments.size();
    }

    public Spin1PAsmExpression getArgument(int index) {
        return arguments.get(index);
    }

    public String getEffect() {
        return effect;
    }

    public Spin1PAsmInstructionFactory getInstructionFactory() {
        return instructionFactory;
    }

    public List<Spin1PAsmLine> expand() {
        if (instructionFactory != null) {
            return instructionFactory.expand(this);
        }
        return Collections.singletonList(this);
    }

    public int resolve(int address, int memoryAddress) {
        scope.setAddress(address);
        scope.setMemoryAddress(memoryAddress);
        return getInstructionObject().resolve(address, memoryAddress);
    }

    public void setInstructionObject(Spin1InstructionObject instructionObject) {
        this.instructionObject = instructionObject;
    }

    public Spin1InstructionObject getInstructionObject() {
        if (instructionObject == null) {
            instructionObject = getInstructionFactory().createObject(this);
        }
        return instructionObject;
    }

    public void generateObjectCode(OutputStream output) throws IOException {
        if (instructionObject != null) {
            instructionObject.generateObjectCode(output);
        }
    }

    public void addAnnotation(CompilerException message) {
        annotations.add(message);
    }

    public List<CompilerException> getAnnotations() {
        return annotations;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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
