/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.model.Token;

public class Spin2Struct {

    int typeSize;
    List<Spin2StructMember> members;

    Context scope;

    public class Spin2StructMember {

        private Token type;
        private Token identifier;
        private Expression size;

        private int offset;

        public Spin2StructMember(Token type, Token identifier, Expression size) {
            this.type = type;
            this.identifier = identifier;
            this.size = size;
        }

        public Token getType() {
            return type;
        }

        public Token getIdentifier() {
            return identifier;
        }

        public Expression getSize() {
            return size;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public Spin2Struct getStructureDefinition() {
            Spin2Struct struct = null;
            if (type != null) {
                struct = scope.getStructureDefinition(type.getText());
                if (struct == null && type.getText().startsWith("^")) {
                    struct = scope.getStructureDefinition(type.getText().substring(1));
                }
            }
            return struct;
        }

    }

    public Spin2Struct(Context context) {
        this.scope = context;
        this.members = new ArrayList<>();
    }

    public Spin2Struct(Context context, Spin2Struct orig) {
        this.scope = context;
        this.members = new ArrayList<>(orig.getMembers());
    }

    public boolean containsMember(Token identifier) {
        for (Spin2StructMember m : members) {
            if (m.identifier.getText().equalsIgnoreCase(identifier.getText())) {
                return true;
            }
        }
        return false;
    }

    public void addMember(Token type, Token identifier, Expression size) {
        members.add(new Spin2StructMember(type, identifier, size));
    }

    public List<Spin2StructMember> getMembers() {
        return members;
    }

    public int getTypeSize() {
        return typeSize;
    }

    public void setTypeSize(int typeSize) {
        this.typeSize = typeSize;
    }

    public Spin2StructMember getMember(String identifier) {
        for (Spin2StructMember m : members) {
            if (m.identifier.getText().equalsIgnoreCase(identifier)) {
                return m;
            }
        }
        return null;
    }

}
