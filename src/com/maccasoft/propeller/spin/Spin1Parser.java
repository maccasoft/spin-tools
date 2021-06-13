/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin;

import java.util.ArrayList;
import java.util.List;

import com.maccasoft.propeller.model.ConstantAssignEnumNode;
import com.maccasoft.propeller.model.ConstantAssignNode;
import com.maccasoft.propeller.model.ConstantSetEnumNode;
import com.maccasoft.propeller.model.ConstantsNode;
import com.maccasoft.propeller.model.DataLineNode;
import com.maccasoft.propeller.model.DataNode;
import com.maccasoft.propeller.model.ErrorNode;
import com.maccasoft.propeller.model.ExpressionNode;
import com.maccasoft.propeller.model.LocalVariableNode;
import com.maccasoft.propeller.model.MethodNode;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.ObjectNode;
import com.maccasoft.propeller.model.ObjectsNode;
import com.maccasoft.propeller.model.ParameterNode;
import com.maccasoft.propeller.model.StatementNode;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;
import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class Spin1Parser {

    final Spin2TokenStream stream;

    Node root;

    public Spin1Parser(Spin2TokenStream stream) {
        this.stream = stream;
    }

    public Node parse() {
        root = new Node();

        while (true) {
            Token token = stream.nextToken();
            if (token.type == Spin2TokenStream.EOF) {
                root.addToken(token);
                break;
            }
            if (token.type == Spin2TokenStream.NL) {
                continue;
            }
            if (!parseSection(token)) {
                ConstantsNode node = new ConstantsNode(root);
                parseConstants(node, token);
            }
        }

        return root;
    }

    boolean parseSection(Token token) {
        if (token.type == Spin2TokenStream.EOF) {
            return false;
        }
        if ("VAR".equalsIgnoreCase(token.getText())) {
            parseVar(token);
            return true;
        }
        if ("OBJ".equalsIgnoreCase(token.getText())) {
            parseObj(token);
            return true;
        }
        if ("PUB".equalsIgnoreCase(token.getText())) {
            parseMethod(token);
            return true;
        }
        if ("PRI".equalsIgnoreCase(token.getText())) {
            parseMethod(token);
            return true;
        }
        if ("DAT".equalsIgnoreCase(token.getText())) {
            parseDat(token);
            return true;
        }
        if ("CON".equalsIgnoreCase(token.getText())) {
            ConstantsNode node = new ConstantsNode(root, token);
            node.addToken(token);
            parseConstants(node, stream.nextToken());
            return true;
        }
        return false;
    }

    void parseConstants(ConstantsNode parent, Token token) {
        List<Token> list = new ArrayList<Token>();

        int state = 1;
        while (true) {
            switch (state) {
                case 0:
                    if (parseSection(token)) {
                        return;
                    }
                    state = 1;
                    // fall-through
                case 1:
                    if (token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF || ",".equals(token.getText())) {
                        Node child = null;

                        if (list.size() == 1) {
                            new ConstantAssignEnumNode(parent, list);
                        }
                        else if (list.size() >= 2) {
                            if ("#".equals(list.get(0).getText())) {
                                new ConstantSetEnumNode(parent, list);
                            }
                            else if (list.size() >= 3) {
                                if ("=".equals(list.get(1).getText())) {
                                    new ConstantAssignNode(parent, list);
                                }
                                else if ("[".equals(list.get(1).getText())) {
                                    new ConstantAssignEnumNode(parent, list);
                                }
                                else {
                                    child = new ErrorNode(parent, "Syntax error");
                                }
                            }
                            else {
                                child = new ErrorNode(parent, "Syntax error");
                            }
                        }

                        if (child != null) {
                            child.addAllTokens(list);
                        }
                        list.clear();

                        if (token.type == Spin2TokenStream.EOF) {
                            return;
                        }
                        if (token.type == Spin2TokenStream.NL) {
                            state = 0;
                        }
                        break;
                    }
                    list.add(token);
                    break;
            }
            token = stream.nextToken();
        }
    }

    void parseVar(Token start) {
        List<Token> list = new ArrayList<Token>();

        Node node = new VariablesNode(root);
        node.addToken(start);

        int state = 1;
        while (true) {
            Token token = stream.nextToken();
            switch (state) {
                case 0:
                    if (parseSection(token)) {
                        return;
                    }
                    state = 1;
                    // fall-through
                case 1:
                    if (token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF || ",".equals(token.getText())) {
                        if (list.size() >= 1) {
                            new VariableNode(node, list);
                        }
                        list.clear();

                        if (token.type == Spin2TokenStream.EOF) {
                            return;
                        }
                        if (token.type == Spin2TokenStream.NL) {
                            state = 0;
                        }
                        break;
                    }
                    list.add(token);
                    break;
            }
        }
    }

    void parseObj(Token start) {
        List<Token> list = new ArrayList<Token>();

        Node node = new ObjectsNode(root);
        node.addToken(start);

        int state = 1;
        while (true) {
            Token token = stream.nextToken();
            switch (state) {
                case 0:
                    if (parseSection(token)) {
                        return;
                    }
                    state = 1;
                    // fall-through
                case 1:
                    if (token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF) {
                        Node child = null;

                        if (list.size() >= 3) {
                            if (":".equals(list.get(list.size() - 2).getText())) {
                                child = new ObjectNode(node);
                            }
                            else {
                                child = new ErrorNode(node);
                            }
                        }
                        else if (list.size() > 0) {
                            child = new ErrorNode(node);
                        }

                        if (child != null) {
                            child.addAllTokens(list);
                        }
                        list.clear();

                        if (token.type == Spin2TokenStream.EOF) {
                            return;
                        }
                        if (token.type == Spin2TokenStream.NL) {
                            state = 0;
                        }
                        break;
                    }
                    list.add(token);
                    break;
            }
        }
    }

    void parseMethod(Token start) {
        MethodNode node = new MethodNode(root, start);

        int state = 2;
        Node parent = node;
        Node child = node;
        Node param = null;
        Node ret = null;
        LocalVariableNode local = null;

        while (true) {
            Token token = stream.nextToken();
            if (token.type == Spin2TokenStream.EOF) {
                return;
            }
            if (token.type == Spin2TokenStream.NL) {
                state = 0;
                continue;
            }
            switch (state) {
                case 0:
                    if (parseSection(token)) {
                        return;
                    }

                    if (child.getTokens().size() != 0) {
                        while (token.column < child.getToken(0).column && child != node) {
                            child = child.getParent();
                            parent = child.getParent();
                        }

                        if (token.column > child.getToken(0).column) {
                            parent = child;
                        }
                    }

                    child = parseStatement(parent, token);
                    break;

                case 1:
                    child.addToken(token);
                    break;

                case 2:
                    if (token.type == 0) {
                        node.name = token;
                        node.addToken(token);
                        state = 3;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.addToken(token);
                    state = 1;
                    break;

                case 3:
                    if ("(".equals(token.getText())) {
                        node.addToken(token);
                        state = 4;
                        break;
                    }
                    else if (":".equals(token.getText())) {
                        node.addToken(token);
                        state = 7;
                        break;
                    }
                    else if ("|".equals(token.getText())) {
                        node.addToken(token);
                        state = 9;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.addToken(token);
                    state = 1;
                    break;

                case 4:
                    if (")".equals(token.getText())) {
                        node.addToken(token);
                        state = 6;
                        break;
                    }
                    if (Spin1Model.isType(token.getText())) {
                        child = new ErrorNode(parent);
                        child.addToken(token);
                        state = 1;
                        break;
                    }
                    param = new Node(node);
                    param.addToken(token);
                    node.parameters.add(param);
                    state = 5;
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        node.addToken(token);
                        state = 4;
                        break;
                    }
                    if (")".equals(token.getText())) {
                        node.addToken(token);
                        state = 6;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.addToken(token);
                    state = 1;
                    break;

                case 6:
                    if ("|".equals(token.getText())) {
                        node.addToken(token);
                        state = 9;
                        break;
                    }
                    else if (":".equals(token.getText())) {
                        node.addToken(token);
                        state = 7;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.addToken(token);
                    state = 1;
                    break;

                case 7:
                    if (Spin1Model.isType(token.getText())) {
                        child = new ErrorNode(parent);
                        child.addToken(token);
                        state = 1;
                        break;
                    }
                    ret = new Node(node);
                    ret.addToken(token);
                    node.returnVariables.add(ret);
                    state = 8;
                    break;
                case 8:
                    if ("|".equals(token.getText())) {
                        node.addToken(token);
                        state = 9;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.addToken(token);
                    state = 1;
                    break;

                case 9:
                    local = new LocalVariableNode(node);
                    node.localVariables.add(local);
                    if (Spin1Model.isType(token.getText())) {
                        local.type = new Node(local);
                        local.type.addToken(token);
                        state = 10;
                        break;
                    }
                    // fall-through
                case 10:
                    if (token.type == 0) {
                        local.identifier = new Node(local);
                        local.identifier.addToken(token);
                        state = 11;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.addToken(token);
                    state = 1;
                    break;
                case 11:
                    if (",".equals(token.getText())) {
                        node.addToken(token);
                        state = 9;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        node.addToken(token);
                        local.size = new ExpressionNode(local);
                        state = 12;
                        break;
                    }
                    if (":".equals(token.getText())) {
                        node.addToken(token);
                        state = 7;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.addToken(token);
                    state = 1;
                    break;
                case 12:
                    if ("]".equals(token.getText())) {
                        node.addToken(token);
                        state = 11;
                        break;
                    }
                    local.size.addToken(token);
                    break;
            }
        }
    }

    Node parseStatement(Node parent, Token token) {
        Node statement = new StatementNode(parent);

        while (true) {
            statement.addToken(token);
            if ("(".equals(token.getText())) {
                token = parseSubStatement(statement, token);
                if (token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF) {
                    break;
                }
                if (")".equals(token.getText())) {
                    statement.addToken(token);
                }
                token = stream.nextToken();
            }
            else {
                token = stream.nextToken();
            }
            if (token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF) {
                break;
            }
        }

        return statement;
    }

    Token parseSubStatement(Node parent, Token token) {
        Node node = new Node(parent);

        while (true) {
            token = stream.nextToken();
            if (token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF) {
                return token;
            }
            if ("(".equals(token.getText())) {
                token = parseSubStatement(node, token);
                if (token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF) {
                    return token;
                }
                if (")".equals(token.getText())) {
                    parent.addToken(token);
                    continue;
                }
            }
            if (",".equals(token.getText())) {
                parent.addToken(token);
                node = new Node(parent);
                continue;
            }
            else if (")".equals(token.getText())) {
                if (node.getParent() != parent) {
                    node = node.getParent();
                }
                return token;
            }
            node.addToken(token);
        }
    }

    void parseDat(Token start) {
        Node node = new DataNode(root);
        node.addToken(start);

        Node parent = node;
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Spin2TokenStream.EOF) {
                return;
            }
            if (token.type == Spin2TokenStream.NL) {
                continue;
            }
            if (parseSection(token)) {
                return;
            }
            if ("ORG".equalsIgnoreCase(token.getText())) {
                parent = node;
            }
            parseDatLine(parent, token);
            if ("ORG".equalsIgnoreCase(token.getText())) {
                parent = node.getChild(node.getChilds().size() - 1);
            }
        }
    }

    void parseDatLine(Node node, Token token) {
        int state = 0;
        DataLineNode parent = new DataLineNode(node);
        ParameterNode parameter = null;
        Node child = null;

        while (true) {
            if (state != 0) {
                token = stream.nextToken();
            }
            if (token.type == Spin2TokenStream.EOF || token.type == Spin2TokenStream.NL) {
                return;
            }
            switch (state) {
                case 0:
                    state = 1;
                    // fall-through
                case 1:
                    if (token.column == 0) {
                        parent.label = new Node(parent);
                        parent.label.addToken(token);
                        state = 2;
                        break;
                    }
                    // fall-through
                case 2:
                    if (Spin1Model.isCondition(token.getText())) {
                        parent.condition = new Node(parent);
                        parent.condition.addToken(token);
                        state = 3;
                        break;
                    }
                    // fall-through
                case 3:
                    if (Spin1Model.isInstruction(token.getText()) || Spin1Model.isType(token.getText())) {
                        parent.instruction = new Node(parent);
                        parent.instruction.addToken(token);
                        state = 4;
                        break;
                    }
                    child = new ErrorNode(parent, parent.condition == null ? "Invalid condition or instruction" : "Invalid instruction");
                    child.addToken(token);
                    child = parent;
                    state = 9;
                    break;
                case 4:
                    if (Spin1Model.isModifier(token.getText())) {
                        parent.modifier = new Node(parent);
                        parent.modifier.addToken(token);
                        state = 5;
                        break;
                    }
                    parameter = new ParameterNode(parent);
                    parameter.addToken(token);
                    parent.parameters.add(parameter);
                    state = 7;
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        parent.modifier.addToken(token);
                        state = 6;
                        break;
                    }
                    child = new ErrorNode(parent, "Syntax error");
                    child.addToken(token);
                    state = 9;
                    break;
                case 6:
                    if (Spin1Model.isModifier(token.getText())) {
                        parent.modifier.addToken(token);
                        state = 5;
                        break;
                    }
                    child = new ErrorNode(parent, "Invalid modifier");
                    child.addToken(token);
                    state = 9;
                    break;
                case 7:
                    if (",".equals(token.getText())) {
                        parameter = new ParameterNode(parent);
                        parent.parameters.add(parameter);
                        break;
                    }
                    if (Spin1Model.isModifier(token.getText())) {
                        parent.modifier = new Node(parent);
                        parent.modifier.addToken(token);
                        state = 5;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        parameter.count = new ExpressionNode();
                        parameter.addChild(parameter.count);
                        state = 10;
                        break;
                    }
                    parameter.addToken(token);
                    break;
                case 9:
                    child.addToken(token);
                    break;
                case 10:
                    if ("]".equals(token.getText())) {
                        parent.addToken(token);
                        state = 7;
                        break;
                    }
                    parameter.count.addToken(token);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        String text = ""
            + "CON\n"
            + "\n"
            + "    _XINFREQ = 5_000_000\n"
            + "    _CLKMODE = XTAL1 + PLL16X\n"
            + "\n"
            + "    ' Modes\n"
            + "\n"
            + "    #0\n"
            + "    MODE_TEXT_80x25\n"
            + "    MODE_TEXT_80x30\n"
            + "    MODE_TEXT_320x240\n"
            + "    MODE_TMS9918\n"
            + "\n"
            + "VAR\n"
            + "\n"
            + "OBJ\n"
            + "\n"
            + "    map  : \"gpu_map\"\n"
            + "    vga  : \"vga_80x25\"\n"
            + "    vga2 : \"vga_640x240\"\n"
            + "    vga3 : \"vga_320x240\"\n"
            + "    vt   : \"vt100_80x25\"\n"
            + "    vt2  : \"vt100_80x30\"\n"
            + "    tms  : \"tms9918\"\n"
            + "    fpu  : \"fpu\"\n"
            + "    rtx  : \"gpu_hs_rtx\"\n"
            + "\n"
            + "PUB start\n"
            + "\n"
            + "    bytefill(map#GPU_REG_BASE, $00, constant($8000 - map#GPU_REG_BASE))\n"
            + "    BYTE[map#GPU_REG0] := $A0 ' VGA text + VT100\n"
            + "    BYTE[map#GPU_REG2] := $00 ' screen\n"
            + "    BYTE[map#GPU_REG3] := $94 ' palette\n"
            + "    BYTE[map#GPU_REG4] := $98 ' font\n"
            + "    BYTE[map#GPU_REG8] := constant(CURSOR_ON|CURSOR_ULINE|CURSOR_FLASH)\n"
            + "    BYTE[map#GPU_REG10] := 4\n"
            + "    vga.init\n"
            + "    fpu.init\n"
            + "    rtx.init\n"
            + "\n"
            + "    bus_if_addr := (@bus_interface_end - @bus_interface) << 16 | @bus_interface\n"
            + "    mode_switch_ovl := (@mode_switch_end - @mode_switch) << 16 | @mode_switch\n"
            + "\n"
            + "    vga_text_driver := (2048 << 16) | vga.get_driver\n"
            + "    vga_640x240_driver := (2048 << 16) | vga2.get_driver\n"
            + "    vga_320x240_driver := (2048 << 16) | vga3.get_driver\n"
            + "    vt100_driver := (vt.get_size << 16) | vt.get_driver\n"
            + "    vt100_80x30_driver := (vt2.get_size << 16) | vt2.get_driver\n"
            + "    tms_driver := (2048 << 16) | tms.get_driver\n"
            + "\n"
            + "    coginit(cogid, @driver, 0)\n"
            + "\n"
            + "DAT\n"
            + "\n"
            + "driver              org     0\n"
            + "\n"
            + "                    jmp     #overlay_start\n"
            + "\n"
            + "' resident code\n"
            + "\n"
            + "' I2C Block Read\n"
            + "'\n"
            + "' i2c_addr = address to read from\n"
            + "' i2c_length = bytes to read\n"
            + "' i2c_hub_addr = hub address to write to\n"
            + "\n"
            + "i2c_read_block\n"
            + "                    call    #i2c_start\n"
            + "\n"
            + "                    mov     i2c_data, #$A0              ' address device for write\n"
            + "                    call    #i2c_write                  ' |\n"
            + "                    mov     i2c_data, i2c_addr\n"
            + "                    ror     i2c_data, #8                ' high address byte\n"
            + "                    call    #i2c_write                  ' |\n"
            + "                    rol     i2c_data, #8                ' low address byte\n"
            + "                    call    #i2c_write                  ' |\n"
            + "";

        try {
            Spin2TokenStream stream = new Spin2TokenStream(text);
            Spin1Parser subject = new Spin1Parser(stream);
            Node root = subject.parse();
            print(root, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void print(Node node, int indent) {
        if (indent != 0) {
            for (int i = 1; i < indent; i++) {
                System.out.print("|    ");
            }
            System.out.print("+--- ");
        }

        System.out.print(node.getClass().getSimpleName());
        //for (Token token : node.tokens) {
        //    System.out.print(" [" + token.getText().replaceAll("\n", "\\n") + "]");
        //}
        System.out.println(" [" + node.getText().replaceAll("\n", "\\\\n") + "]");

        for (Node child : node.getChilds()) {
            print(child, indent + 1);
        }
    }

}
