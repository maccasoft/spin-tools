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

import com.maccasoft.propeller.spin1.instructions.Empty;

public class Spin1PAsmLine {

    Spin1Context scope;
    String label;
    String condition;
    String mnemonic;
    List<Spin1PAsmExpression> arguments;
    String effect;

    Spin1PAsmInstructionFactory instructionFactory;
    Spin1InstructionObject instructionObject;

    //String originalText;
    List<String> annotations = new ArrayList<String>();

    public Spin1PAsmLine(Spin1Context scope, String label, String condition, String mnemonic, List<Spin1PAsmExpression> arguments, String effect) {
        this.scope = new Spin1Context(scope);
        this.label = label;
        this.condition = condition;
        this.mnemonic = mnemonic;
        this.arguments = arguments;
        this.effect = effect;

        if (mnemonic != null) {
            this.instructionFactory = Spin1PAsmInstructionFactory.get(mnemonic);
            if (this.instructionFactory == null) {
                this.annotations.add("error: invalid instruction " + mnemonic);
            }
        }
        if (this.instructionFactory == null) {
            this.instructionFactory = Empty.instance;
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

    public int resolve(int address) {
        if (instructionObject == null && instructionFactory != null) {
            instructionObject = instructionFactory.createObject(this);
        }
        if (instructionObject != null) {
            return instructionObject.resolve(address);
        }
        return address;
    }

    public Spin1InstructionObject getInstructionObject() {
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
