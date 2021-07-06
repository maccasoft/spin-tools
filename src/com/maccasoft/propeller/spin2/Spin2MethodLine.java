/*
 * Copyright (c) 2021 Marco Maccaferri and others.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.expressions.ContextLiteral;

public class Spin2MethodLine {

    Spin2Context scope;
    String label;
    String statement;
    List<Spin2StatementNode> arguments;

    Spin2MethodLine parent;
    List<Spin2MethodLine> childs = new ArrayList<Spin2MethodLine>();

    String text;
    List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    public Spin2MethodLine(Spin2Context scope, String label, String statement, Spin2StatementNode argument) {
        this.scope = new Spin2Context(scope);
        this.label = label;
        this.statement = statement;
        this.arguments = Collections.singletonList(argument);
    }

    public Spin2MethodLine(Spin2Context scope, String label, String statement, List<Spin2StatementNode> arguments) {
        this.scope = new Spin2Context(scope);
        this.label = label;
        this.statement = statement;
        this.arguments = arguments;
    }

    public Spin2Context getScope() {
        return scope;
    }

    public String getLabel() {
        return label;
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

    public List<Spin2MethodLine> expand() {
        List<Spin2MethodLine> list = new ArrayList<Spin2MethodLine>();
        list.add(this);
        for (Spin2MethodLine line : childs) {
            list.addAll(line.expand());
        }
        return list;
    }

    public void register(Spin2Context context) {
        if (label != null) {
            context.addSymbol(label, new ContextLiteral(scope));
        }
        for (Spin2MethodLine line : childs) {
            line.register(context);
        }
    }

    public int resolve(int address) {
        scope.setAddress(address);
        for (Spin2Bytecode bc : source) {
            address = bc.resolve(address);
        }
        for (Spin2MethodLine line : childs) {
            address = line.resolve(address);
        }
        return address;
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
            obj.writeBytes(bc.getBytes(), bc.toString());
        }

        for (Spin2MethodLine line : childs) {
            line.writeTo(obj);
        }
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
        if (statement != null) {
            sb.append(statement);
        }
        while (sb.length() < 22) {
            sb.append(" ");
        }
        if (text != null) {
            sb.append(" | ");
            sb.append(text);
        }
        return sb.toString();
    }

}
