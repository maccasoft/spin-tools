/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.expressions;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2Struct;
import com.maccasoft.propeller.spin2.Spin2Struct.Member;

public class SizeOf extends Expression {

    Token name;
    Context context;

    public SizeOf(Token name, Context context) {
        this.name = name;
        this.context = context;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number getNumber() {
        String[] identifier = name.getText().split("[\\.]");

        Expression expression = context.getLocalSymbol(identifier[0]);
        if (expression instanceof Variable var) {
            int index = 1;
            while (index < identifier.length) {
                var = var.getMember(identifier[index++]);
                if (var == null) {
                    throw new CompilerException("expecting structure member", name);
                }
            }
            return var.getTypeSize() * var.getSize();
        }
        if (context.hasStructureDefinition(identifier[0])) {
            Spin2Struct struct = context.getStructureDefinition(identifier[0]);

            int index = 1;
            while (index < identifier.length) {
                Member member = struct.getMember(identifier[index++]);
                if (member == null) {
                    throw new CompilerException("expecting structure member", name);
                }
                String type = member.getType().getText();

                struct = context.getStructureDefinition(member.getType().getText());
                if (struct == null && member.getType().getText().startsWith("^")) {
                    struct = context.getStructureDefinition(member.getType().getText().substring(1));
                }

                if (struct == null) {
                    if (index < identifier.length) {
                        throw new CompilerException("expecting structure member", name);
                    }
                    long size = member.getSize().getNumber().longValue();
                    if ("BYTE".equalsIgnoreCase(type)) {
                        return size;
                    }
                    if ("WORD".equalsIgnoreCase(type)) {
                        return 2 * size;
                    }
                    if ("LONG".equalsIgnoreCase(type)) {
                        return 4 * size;
                    }
                    if ("^BYTE".equalsIgnoreCase(type) || "^WORD".equalsIgnoreCase(type) || "^LONG".equalsIgnoreCase(type)) {
                        return 4 * size;
                    }

                    throw new CompilerException("expecting structure member", name);
                }
            }

            if (struct == null) {
                throw new CompilerException("expecting structure member", name);
            }
            return struct.getTypeSize();
        }
        if ("BYTE".equalsIgnoreCase(identifier[0])) {
            return 1;
        }
        else if ("WORD".equalsIgnoreCase(identifier[0])) {
            return 2;
        }
        else if ("LONG".equalsIgnoreCase(identifier[0])) {
            return 4;
        }
        else if ("^BYTE".equalsIgnoreCase(identifier[0]) || "^WORD".equalsIgnoreCase(identifier[0]) || "^LONG".equalsIgnoreCase(identifier[0])) {
            return 4;
        }

        throw new CompilerException("expecting type or variable", name);
    }

    @Override
    public String toString() {
        return "sizeof(" + name.getText() + ")";
    }

}
