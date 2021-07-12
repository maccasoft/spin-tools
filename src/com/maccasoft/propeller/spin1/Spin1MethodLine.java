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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.expressions.ContextLiteral;

public class Spin1MethodLine {

    Spin1Context scope;
    String label;
    String statement;
    List<Spin1StatementNode> arguments;

    Spin1MethodLine parent;
    List<Spin1MethodLine> childs = new ArrayList<Spin1MethodLine>();

    String text;
    List<Spin1Bytecode> source = new ArrayList<Spin1Bytecode>();

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    public Spin1MethodLine(Spin1Context scope, String label, String statement, Spin1StatementNode argument) {
        this.scope = new Spin1Context(scope);
        this.label = label;
        this.statement = statement;
        this.arguments = Collections.singletonList(argument);
    }

    public Spin1MethodLine(Spin1Context scope, String label, String statement, List<Spin1StatementNode> arguments) {
        this.scope = new Spin1Context(scope);
        this.label = label;
        this.statement = statement;
        this.arguments = arguments;
    }

    public Spin1Context getScope() {
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

    public void register(Spin1Context context) {
        if (label != null) {
            context.addSymbol(label, new ContextLiteral(scope));
        }
        for (Spin1MethodLine line : childs) {
            line.register(context);
        }
    }

    public int resolve(int address) {
        scope.setAddress(address);
        for (Spin1Bytecode bc : source) {
            address = bc.resolve(address);
        }
        for (Spin1MethodLine line : childs) {
            address = line.resolve(address);
        }
        return address;
    }

    public int getArgumentsCount() {
        return arguments.size();
    }

    public List<Spin1StatementNode> getArguments() {
        return arguments;
    }

    public Spin1StatementNode getArgument(int index) {
        return arguments.get(index);
    }

    public Spin1MethodLine getParent() {
        return parent;
    }

    public void addChild(Spin1MethodLine line) {
        line.parent = this;
        childs.add(line);
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
