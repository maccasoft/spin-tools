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
import com.maccasoft.propeller.spin2.Spin2Struct.Spin2StructMember;

public class OffsetOf extends Expression {

    Token name;
    Context context;

    public OffsetOf(Token name, Context context) {
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
        Spin2Struct struct = null;
        String[] identifier = name.getText().split("[\\.]");
        if (identifier.length == 1) {
            throw new CompilerException("expecting structure member", name);
        }

        Expression expression = context.getLocalSymbol(identifier[0]);
        if (expression instanceof Variable var) {
            struct = context.getStructureDefinition(var.getType());
            if (struct == null && var.getType().startsWith("^")) {
                struct = context.getStructureDefinition(var.getType().substring(1));
            }
        }
        else if (context.hasStructureDefinition(identifier[0])) {
            struct = context.getStructureDefinition(identifier[0]);
        }

        if (struct == null) {
            throw new CompilerException("expecting structure member", name);
        }

        int offset = 0;

        int index = 1;
        while (index < identifier.length) {
            Spin2StructMember member = struct.getMember(identifier[index++]);
            if (member == null) {
                throw new CompilerException("expecting structure member", name);
            }
            offset += member.getOffset();
            struct = context.getStructureDefinition(member.getType().getText());
            if (struct == null && member.getType().getText().startsWith("^")) {
                struct = context.getStructureDefinition(member.getType().getText().substring(1));
            }
            if (struct == null && index < identifier.length) {
                throw new CompilerException("expecting structure member", name);
            }
        }

        return offset;
    }

    @Override
    public String toString() {
        return "offsetof(" + name + ")";
    }

}
