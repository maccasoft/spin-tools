/*
 * Copyright (c) 2021-22 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.bytecode.InlinePAsm;

public class Spin2MethodLine {

    Spin2Context scope;

    String statement;
    List<Spin2StatementNode> arguments = new ArrayList<Spin2StatementNode>();

    Spin2MethodLine parent;
    List<Spin2MethodLine> childs = new ArrayList<Spin2MethodLine>();

    String text;
    List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    int startAddress;
    int endAddress;
    boolean addressChanged;

    public Spin2MethodLine(Spin2Context scope) {
        this.scope = new Spin2Context(scope);
    }

    public Spin2MethodLine(Spin2Context scope, String statement) {
        this.scope = new Spin2Context(scope);
        this.statement = statement;
    }

    public Spin2MethodLine(Spin2Context scope, String statement, Node node) {
        this.scope = new Spin2Context(scope);
        this.statement = statement;
        this.data = node;
        this.text = node.toString().replaceAll("[\\r\\n]", "");
    }

    public Spin2MethodLine(Spin2Context scope, Spin2MethodLine parent) {
        this.scope = new Spin2Context(scope);
        this.parent = parent;
    }

    public Spin2MethodLine(Spin2Context scope, Spin2MethodLine parent, String statement, Node node) {
        this.scope = new Spin2Context(scope);
        this.parent = parent;
        this.statement = statement;
        this.data = node;
        this.text = node.toString().replaceAll("[\\r\\n]", "");
    }

    public Spin2Context getScope() {
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

    public void register(Spin2Context context) {
        for (Spin2MethodLine line : childs) {
            line.register(context);
        }
    }

    public int resolve(int address) {
        int pasmAddress = 0;

        addressChanged = startAddress != address;
        startAddress = address;

        scope.setAddress(address);
        for (Spin2Bytecode bc : source) {
            if (bc instanceof InlinePAsm) {
                pasmAddress = bc.resolve(pasmAddress);
                address += bc.getSize();
            }
            else {
                address = bc.resolve(address);
            }
        }

        for (Spin2MethodLine line : childs) {
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

    public void addArgument(Spin2StatementNode node) {
        arguments.add(node);
    }

    public int getArgumentsCount() {
        return arguments.size();
    }

    public List<Spin2StatementNode> getArguments() {
        return arguments;
    }

    public Spin2StatementNode getArgument(int index) {
        return arguments.get(index);
    }

    public Spin2MethodLine getParent() {
        return parent;
    }

    public void addChild(Spin2MethodLine line) {
        line.parent = this;
        childs.add(line);
    }

    public void addChild(int index, Spin2MethodLine line) {
        line.parent = this;
        childs.add(index, line);
    }

    public void addChilds(Collection<Spin2MethodLine> lines) {
        for (Spin2MethodLine line : lines) {
            line.parent = this;
        }
        childs.addAll(lines);
    }

    public List<Spin2MethodLine> getChilds() {
        return childs;
    }

    public void addSource(Spin2Bytecode line) {
        source.add(line);
    }

    public void addSource(int index, Spin2Bytecode line) {
        source.add(index, line);
    }

    public void addSource(Collection<Spin2Bytecode> lines) {
        source.addAll(lines);
    }

    public List<Spin2Bytecode> getSource() {
        return source;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData(String key) {
        return keyedData.get(key);
    }

    public void setData(String key, Object data) {
        this.keyedData.put(key, data);
    }

    public void writeTo(Spin2Object obj) {
        if (text != null) {
            obj.writeComment(text);
        }

        for (Spin2Bytecode bc : source) {
            if (bc instanceof InlinePAsm) {
                Spin2PAsmLine line = ((InlinePAsm) bc).getLine();
                obj.writeBytes(line.getScope().getAddress(), false, line.getInstructionObject().getBytes(), line.toString());
            }
            else {
                obj.writeBytes(bc.getBytes(), bc.toString());
            }
        }

        for (Spin2MethodLine line : childs) {
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
