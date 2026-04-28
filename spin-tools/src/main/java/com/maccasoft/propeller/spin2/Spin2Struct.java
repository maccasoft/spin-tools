/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.expressions.AddpinsRange;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.expressions.Expression;
import com.maccasoft.propeller.expressions.NumberLiteral;
import com.maccasoft.propeller.model.Token;

public class Spin2Struct {

    public class Member {

        Token type;
        Token identifier;
        Expression size;

        int offset;
        List<Member> members;

        public Member(Token type, Token identifier, Expression size) {
            this.type = type;
            this.identifier = identifier;
            this.size = size;
            this.members = new ArrayList<>();
        }

        public Token getType() {
            return type;
        }

        public Token getIdentifier() {
            return identifier;
        }

        public Expression getSize() {
            return size instanceof AddpinsRange ? new NumberLiteral(1) : size;
        }

        public int getBitfield() {
            if (size instanceof AddpinsRange addPins) {
                int arg0 = addPins.getTerm1().getNumber().intValue();
                int arg1 = addPins.getTerm2().getNumber().intValue();
                return ((arg0 - arg1) & 0x1F) << 5 | arg1;
            }
            return -1;
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

        public boolean hasBitfields() {
            return !members.isEmpty();
        }

        public void addMember(Token identifier, Expression size) {
            members.add(new Member(type, identifier, size));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            if (type != null) {
                sb.append(type.getText()).append(" ");
            }
            if (identifier != null) {
                if (size instanceof AddpinsRange) {
                    sb.append(".");
                }
                sb.append(identifier.getText());
            }
            if (size != null) {
                sb.append("[").append(size).append("]");
            }

            for (Member member : members) {
                if (!sb.isEmpty()) {
                    sb.append(" ");
                }
                sb.append(member);
            }

            return sb.toString();
        }

    }

    Context scope;

    int typeSize;
    List<Member> members;

    public Spin2Struct(Context context) {
        this.scope = context;
        this.members = new ArrayList<>();
    }

    public Spin2Struct(Context context, Spin2Struct orig) {
        this.scope = context;
        this.members = new ArrayList<>(orig.getMembers());
    }

    public Member addMember(Token type, Token identifier, Expression size) {
        Member member = new Member(type, identifier, size);
        members.add(member);
        return member;
    }

    public List<Member> getMembers() {
        return members;
    }

    public int getTypeSize() {
        return typeSize;
    }

    public void setTypeSize(int typeSize) {
        this.typeSize = typeSize;
    }

    public Member getMember(String identifier) {
        for (Member top : members) {
            if (top.identifier != null && top.identifier.getText().equalsIgnoreCase(identifier)) {
                return top;
            }
            for (Member bitfield : top.members) {
                if (bitfield.identifier != null && bitfield.identifier.getText().equalsIgnoreCase(identifier)) {
                    return bitfield;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Member member : members) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(member);
        }

        return sb.toString();
    }

}
