/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.model.Node;

public class Spin1MethodLine {

    Context scope;

    String statement;

    Spin1MethodLine parent;
    List<Spin1MethodLine> childs = new ArrayList<>();

    String text;
    List<Spin1Bytecode> source = new ArrayList<>();

    protected Node data;
    protected Map<String, Object> keyedData = new HashMap<>();

    int startAddress;
    int endAddress;
    boolean addressChanged;

    public Spin1MethodLine(Context scope) {
        this.scope = new Context(scope);
    }

    public Spin1MethodLine(Context scope, String statement) {
        this.scope = new Context(scope);
        this.statement = statement;
    }

    public Spin1MethodLine(Context scope, String statement, Node node) {
        this.scope = new Context(scope);
        this.statement = statement;
        this.data = node;
        this.text = node.toString().replaceAll("[\\r\\n]", "");
    }

    public Spin1MethodLine(Context scope, Spin1MethodLine parent) {
        this.scope = new Context(scope);
        this.parent = parent;
    }

    public Spin1MethodLine(Context scope, Spin1MethodLine parent, String statement, Node node) {
        this.scope = new Context(scope);
        this.parent = parent;
        this.statement = statement;
        this.data = node;
        this.text = node.toString().replaceAll("[\\r\\n]", "");
    }

    public Context getScope() {
        return scope;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void register(Context context) {
        for (Spin1MethodLine line : childs) {
            line.register(context);
        }
    }

    public int resolve(int address) {
        addressChanged = startAddress != address;
        startAddress = address;

        scope.setAddress(address);
        scope.setObjectAddress(address);
        for (Spin1Bytecode bc : source) {
            address = bc.resolve(address);
        }
        for (Spin1MethodLine line : childs) {
            address = line.resolve(address);
            addressChanged |= line.isAddressChanged();
        }

        addressChanged |= endAddress != address;
        endAddress = address;

        return address;
    }

    public boolean isAddressChanged() {
        return addressChanged;
    }

    public Spin1MethodLine getParent() {
        return parent;
    }

    public void addChild(Spin1MethodLine line) {
        line.parent = this;
        childs.add(line);
    }

    public void addChild(int index, Spin1MethodLine line) {
        line.parent = this;
        childs.add(index, line);
    }

    public void addChilds(Collection<Spin1MethodLine> lines) {
        for (Spin1MethodLine line : lines) {
            line.parent = this;
        }
        childs.addAll(lines);
    }

    public int getChildsCount() {
        return childs.size();
    }

    public Spin1MethodLine getChild(int index) {
        return childs.get(index);
    }

    public List<Spin1MethodLine> getChilds() {
        return childs;
    }

    public void addSource(Spin1Bytecode line) {
        source.add(line);
    }

    public void addSource(Collection<Spin1Bytecode> lines) {
        source.addAll(lines);
    }

    public List<Spin1Bytecode> getSource() {
        return source;
    }

    public Node getData() {
        return data;
    }

    public void setData(Node data) {
        this.data = data;
    }

    public Object getData(String key) {
        return keyedData.get(key);
    }

    public void setData(String key, Object data) {
        this.keyedData.put(key, data);
    }

    public void writeTo(Spin1Object obj) {
        if (text != null) {
            obj.writeComment(text);
        }

        for (Spin1Bytecode bc : source) {
            obj.writeBytes(bc.getBytes(), bc.toString());
        }

        for (Spin1MethodLine line : childs) {
            line.writeTo(obj);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (statement != null) {
            sb.append(statement.toUpperCase());
        }
        if (text != null) {
            while (sb.length() < 15) {
                sb.append(" ");
            }
            sb.append(" | ");
            sb.append(text);
        }
        return sb.toString();
    }

}
