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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maccasoft.propeller.CompilerException;
import com.maccasoft.propeller.expressions.Context;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.spin2.bytecode.InlinePAsm;
import com.maccasoft.propeller.spin2.bytecode.InlinePAsmExec;
import com.maccasoft.propeller.spin2.instructions.Debug;
import com.maccasoft.propeller.spin2.instructions.Orgh;

public class Spin2MethodLine {

    Context scope;

    String statement;

    Spin2MethodLine parent;
    List<Spin2MethodLine> childs = new ArrayList<Spin2MethodLine>();

    String text;
    List<Spin2Bytecode> source = new ArrayList<Spin2Bytecode>();

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    int startAddress;
    int endAddress;
    boolean addressChanged;

    public Spin2MethodLine(Context scope) {
        this.scope = new Context(scope);
    }

    public Spin2MethodLine(Context scope, String statement) {
        this.scope = new Context(scope);
        this.statement = statement;
    }

    public Spin2MethodLine(Context scope, String statement, Node node) {
        this.scope = new Context(scope);
        this.statement = statement;
        this.data = node;
        this.text = node.toString().replaceAll("[\\r\\n]", "");
    }

    public Spin2MethodLine(Context scope, Spin2MethodLine parent) {
        this.scope = new Context(scope);
        this.parent = parent;
    }

    public Spin2MethodLine(Context scope, Spin2MethodLine parent, String statement, Node node) {
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
        for (Spin2MethodLine line : childs) {
            line.register(context);
        }
    }

    public int resolve(int address, boolean debug) {
        int pasmAddress = 0, pasmCount = 0;
        boolean hubMode = false;

        addressChanged = startAddress != address;
        startAddress = address;

        scope.setAddress(address);

        for (Spin2Bytecode bc : source) {
            if (bc instanceof InlinePAsm) {
                Spin2PAsmLine pasmLine = ((InlinePAsm) bc).getLine();
                if (pasmLine.getInstructionFactory() instanceof Orgh) {
                    hubMode = true;
                }
                if (!hubMode && (pasmAddress >> 2) >= 0x120) {
                    throw new CompilerException("inline cog address exceeds $120 limit", pasmLine.getData());
                }
                if (debug || !(pasmLine.getInstructionFactory() instanceof Debug)) {
                    pasmAddress = pasmLine.resolve(pasmAddress, hubMode);
                    address += bc.getSize();
                    pasmCount += bc.getSize();
                }
                else {
                    pasmLine.resolve(pasmAddress, hubMode);
                }
            }
            else {
                address = bc.resolve(address);
            }
        }
        int padding = 0;
        if (pasmCount > 0 && (pasmCount % 4) != 0) {
            padding = 4 - (pasmCount % 4);
            address += padding;
            pasmCount += padding;
        }
        if (pasmCount != 0) {
            InlinePAsmExec bc = (InlinePAsmExec) source.get(0);
            bc.setSize(pasmCount >> 2);
        }

        for (Spin2MethodLine line : childs) {
            address = line.resolve(address, debug);
            addressChanged |= line.isAddressChanged();
        }

        addressChanged |= endAddress != address;
        endAddress = address;

        return address;
    }

    public boolean isAddressChanged() {
        return addressChanged;
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

    public void writeTo(Spin2Object obj, boolean debug) {
        int pasmAddress = 0, pasmCount = 0;
        CompilerException msgs = new CompilerException();

        if (text != null) {
            obj.writeComment(text);
        }

        for (Spin2Bytecode bc : source) {
            if (bc instanceof InlinePAsm) {
                Spin2PAsmLine pasmLine = ((InlinePAsm) bc).getLine();
                try {
                    byte[] code = pasmLine.getInstructionObject().getBytes();
                    if (!debug && (pasmLine.getInstructionFactory() instanceof Debug)) {
                        code = new byte[0];
                    }
                    pasmAddress = pasmLine.getScope().getAddress();
                    obj.writeBytes(pasmAddress, false, code, pasmLine.toString());
                    pasmAddress += code.length;
                    pasmCount += code.length;
                } catch (CompilerException e) {
                    msgs.addMessage(e);
                } catch (Exception e) {
                    msgs.addMessage(new CompilerException(e.getMessage(), pasmLine.getData()));
                }
            }
            else {
                obj.writeBytes(bc.getBytes(), bc.toString());
            }
        }
        if (pasmCount > 0 && (pasmCount % 4) != 0) {
            obj.writeBytes(pasmAddress, false, new byte[4 - (pasmCount % 4)], "PADDING");
        }

        if (msgs.hasChilds()) {
            throw msgs;
        }

        for (Spin2MethodLine line : childs) {
            line.writeTo(obj, debug);
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
