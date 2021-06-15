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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.model.VariableNode;
import com.maccasoft.propeller.model.VariablesNode;

public class Spin2Parser {

    public static Set<String> blockStart = new HashSet<String>(Arrays.asList(new String[] {
        "IF", "IFNOT", "ELSEIF", "ELSEIFNOT", "ELSE", "CASE", "CASE_FAST", "OTHER", "REPEAT",
    }));

    public static Set<String> instructions = new HashSet<String>(Arrays.asList(new String[] {
        "ORG", "ORGH", "ORGF", "FIT",
        "RES", "FILE", "ASMCLK",
        "NOP", "ROL", "ROR", "SHR", "SHL", "RCR", "RCL", "SAR", "SAL", "ADD", "ADDX", "ADDS", "ADDSX", "SUB", "SUBX", "SUBS",
        "SUBSX", "CMP", "CMPX", "CMPS", "CMPSX", "CMPR", "CMPM", "SUBR", "CMPSUB", "FGE", "FLE", "FGES", "FLES", "SUMC", "SUMNC",
        "SUMZ", "SUMNZ", "TESTB", "TESTBN", "BITL", "BITH", "BITC", "BITNC", "BITZ", "BITNZ", "BITNC", "BITRND", "BITNOT", "AND",
        "ANDN", "OR", "XOR", "MUXC", "MUXNC", "MUXZ", "MUXNZ", "MOV", "NOT", "ABS", "NEG", "NEGC", "NEGNC", "NEGZ", "NEGNZ",
        "INCMOD", "DECMOD", "ZEROX", "SIGNX", "ENCOD", "ONES", "TEST", "TESTN", "SETNIB", "GETNIB", "ROLNIB", "SETBYTE", "GETBYTE",
        "ROLBYTE", "SETWORD", "GETWORD", "ROLWORD", "ALTSN", "ALTGN", "ALTSB", "ALTGB", "ALTSW", "ALTGW", "ALTR", "ALTD", "ALTS",
        "ALTB", "ALTI", "SETR", "SETD", "SETS", "DECOD", "BMASK", "CRCBIT", "CRCNIB", "MUXNITS", "MUXNIBS", "MUXQ", "MOVBYTS",
        "MUL", "MULS", "SCA", "SCAS", "ADDPIX", "MULPIX", "BLNPIX", "MIXPIX", "ADDCT1", "ADDCT2", "ADDCT3", "WMLONG", "RQPIN",
        "RDPIN", "RDLUT", "RDBYTE", "RDWORD", "RDLONG", "POPA", "POPB", "CALLD", "RESI3", "RESI2", "RESI1", "RESI0", "REST3",
        "REST2", "REST1", "REST0", "CALLPA", "CALLPB", "DJZ", "DJNZ", "DJF", "DJNF", "IJZ", "IJNZ", "TJZ", "TJNZ", "TJF", "TJNF",
        "TJS", "TJNS", "JINT", "JCT1", "JCT2", "JCT3", "JSE1", "JSE2", "JSE3", "JSE4", "JPAT", "JFBW", "JXMT", "JXFI", "JXRO",
        "JXRL", "JATN", "JQMT", "JNINT", "JNCT1", "JNCT2", "JNCT3", "JNSE1", "JNSE2", "JNSE3", "JNSE4", "JNPAT", "JNFBW", "JNXMT",
        "JNXFI", "JNXRO", "JNXRL", "JNATN", "JNQMT", "SETPAT", "AKPIN", "WRPIN", "WXPIN", "WYPIN", "WRLUT",
        "WRBYTE", "WRWORD", "WRLONG", "PUSHA", "PUSHB", "RDFAST", "WRFAST", "FBLOCK", "XINIT", "XSTOP", "XZERO", "XCONT", "REP",
        "COGINIT", "QMUL", "QDIV", "QFRAC", "QSQRT", "QROTATE", "QVECTOR", "HUBSET", "COGID", "COGSTOP", "LOCKNEW", "LOCKRET",
        "LOCKTRY", "LOCKREL", "QLOG", "QEXP", "RFBYTE", "RFWORD", "RFLONG", "RFVAR", "RFVARS", "WFBYTE", "WFWORD", "WFLONG",
        "GETQX", "GETQY", "GETCT", "GETRND", "SETDACS", "SETXFRQ", "GETXACC", "WAITX", "SETSE1", "SETSE2", "SETSE3", "SETSE4",
        "POLLINT", "POLLCT1", "POLLCT2", "POLLCT3", "POLLSE1", "POLLSE2", "POLLSE3", "POLLSE4", "POLLPAT", "POLLFBW", "POLLXMT",
        "POLLXFI", "POLLXRO", "POLLXRL", "POLLATN", "POLLQMT", "WAITINT", "WAITCT1", "WAITCT2", "WAITCT3", "WAITSE1", "WAITSE2",
        "WAITSE3", "WAITSE4", "WAITPAT", "WAITFBW", "WAITXMT", "WAITXFI", "WAITXRO", "WAITXRL", "WAITATN", "ALLOWI", "STALLI",
        "TRIGINT1", "TRIGINT2", "TRIGINT3", "NIXINT1", "NIXINT2", "NIXINT3", "SETINT1", "SETINT2", "SETINT3", "SETQ", "SETQ2",
        "PUSH", "POP", "JMP", "CALL", "RET", "CALLA", "RETA", "CALLB", "RETB", "JMPREL", "SKIP", "SKIPF", "EXECF", "GETPTR",
        "GETBRK", "COGBRK", "BRK", "SETLUTS", "SETCY", "SETCI", "SETCQ", "SETCFRQ", "SETCMOD", "SETPIV", "SETPIX", "COGATN",
        "TESTP", "TESTPN", "DIRL", "DIRH", "DIRC", "DIRNC", "DIRZ", "DIRNZ", "DIRRND", "DIRNOT", "OUTL", "OUTH", "OUTC", "OUTNC",
        "OUTZ", "OUTNZ", "OUTRND", "OUTNOT", "FLTL", "FLTH", "FLTC", "FLTNC", "FLTZ", "FLTNZ", "FLTRND", "FLTNOT", "DRVL", "DRVH",
        "DRVC", "DRVNC", "DRVZ", "DRVNZ", "DRVRND", "DRVNOT", "SPLITB", "MERGEB", "SPLITW", "MERGEW", "SEUSSF", "SEUSSR", "RGBSQZ",
        "RGBEXP", "XORO32", "REV", "RCZR", "RCZL", "WRC", "WRNC", "WRZ", "WRNZ", "MODCZ", "MODC", "MODZ", "SETSCP", "GETSCP",
        "JMP", "CALL", "CALLA", "CALLB", "CALLD", "LOC", "AUGS", "AUGD",
    }));

    public static Set<String> conditions = new HashSet<String>(Arrays.asList(new String[] {
        "_RET_",
        "IF_NC_AND_NZ", "IF_NZ_AND_NC", "IF_GT", "IF_A", "IF_00", "IF_NC_AND_Z", "IF_Z_AND_NC", "IF_01", "IF_NC", "IF_GE", "IF_AE",
        "IF_0X", "IF_C_AND_NZ", "IF_NZ_AND_C", "IF_10", "IF_NZ", "IF_NE", "IF_X0", "IF_C_NE_Z", "IF_Z_NE_C", "IF_DIFF",
        "IF_NC_OR_NZ", "IF_NZ_OR_NC", "IF_NOT_11", "IF_C_AND_Z", "IF_Z_AND_C", "IF_11", "IF_C_EQ_Z", "IF_Z_EQ_C", "IF_SAME",
        "IF_Z", "IF_E", "IF_X1", "IF_NC_OR_Z", "IF_Z_OR_NC", "IF_NOT_10", "IF_C", "IF_LT", "IF_B", "IF_1X", "IF_C_OR_NZ",
        "IF_NZ_OR_C", "IF_NOT_01", "IF_C_OR_Z", "IF_Z_OR_C", "IF_LE", "IF_BE", "IF_NOT_00",
    }));

    public static Set<String> modifiers = new HashSet<String>(Arrays.asList(new String[] {
        "WC", "WZ", "WCZ",
        "ANDC", "ANDZ", "ORC", "ORZ", "XORC", "XORZ",
    }));

    public static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE",
    }));

    final Spin2TokenStream stream;

    Node root;

    public Spin2Parser(Spin2TokenStream stream) {
        this.stream = stream;
    }

    public Node parse() {
        root = new Node();

        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                root.addToken(token);
                break;
            }
            if (token.type == Token.NL) {
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
        if (token.type == Token.EOF) {
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
                    if (token.type == Token.NL || token.type == Token.EOF || ",".equals(token.getText())) {
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

                        if (token.type == Token.EOF) {
                            return;
                        }
                        if (token.type == Token.NL) {
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
                    if (token.type == Token.NL || token.type == Token.EOF || ",".equals(token.getText())) {
                        if (list.size() >= 1) {
                            new VariableNode(node, list);
                        }
                        list.clear();

                        if (token.type == Token.EOF) {
                            return;
                        }
                        if (token.type == Token.NL) {
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
        Node parent = new ObjectsNode(root);
        parent.addToken(start);

        ErrorNode error = null;
        ObjectNode object = null;
        int state = 1;
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                return;
            }
            if (token.type == Token.NL) {
                state = 0;
                continue;
            }
            switch (state) {
                case 0:
                    if (parseSection(token)) {
                        return;
                    }
                    state = 1;
                    // fall-through
                case 1:
                    object = new ObjectNode(parent);
                    object.addToken(token);
                    object.name = token;
                    state = 2;
                    break;
                case 2:
                    if ("[".equals(token.getText())) {
                        object.count = new Node(object);
                        state = 5;
                        break;
                    }
                    // fall-through
                case 3:
                    if (":".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    error = new ErrorNode(object, "Syntax error");
                    error.addToken(token);
                    state = 9;
                    break;
                case 4:
                    object.addToken(token);
                    object.file = token;
                    state = 8;
                    break;

                case 5:
                    if ("]".equals(token.getText())) {
                        state = 3;
                        break;
                    }
                    object.count.addToken(token);
                    break;

                case 8:
                    error = new ErrorNode(object, "Syntax error");
                    state = 9;
                    // fall-through
                case 9:
                    error.addToken(token);
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
            if (token.type == Token.EOF) {
                return;
            }
            if (token.type == Token.NL) {
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
                            if (blockStart.contains(child.getToken(0).getText().toUpperCase())) {
                                parent = child;
                            }
                            else if (child.getText().endsWith(":")) {
                                parent = child;
                            }
                        }
                    }

                    if ("ORG".equalsIgnoreCase(token.getText())) {
                        parseInlineCode(parent, token);
                        break;
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
                    if (types.contains(token.getText().toUpperCase())) {
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
                    if (":".equals(token.getText())) {
                        node.addToken(token);
                        state = 7;
                        break;
                    }
                    if ("|".equals(token.getText())) {
                        node.addToken(token);
                        state = 9;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.addToken(token);
                    state = 1;
                    break;

                case 7:
                    if (types.contains(token.getText().toUpperCase())) {
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
                    if (",".equals(token.getText())) {
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

                case 9:
                    local = new LocalVariableNode(node);
                    node.localVariables.add(local);
                    if (types.contains(token.getText().toUpperCase())) {
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
            if (statement.getTokens().size() == 2) {
                if (":".equals(token.getText())) {
                    break;
                }
            }
            if ("(".equals(token.getText())) {
                token = parseSubStatement(statement, token);
                if (token.type == Token.NL || token.type == Token.EOF) {
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
            if (token.type == Token.NL || token.type == Token.EOF) {
                break;
            }
        }

        return statement;
    }

    Token parseSubStatement(Node parent, Token token) {
        Node node = new Node(parent);

        while (true) {
            token = stream.nextToken();
            if (token.type == Token.NL || token.type == Token.EOF) {
                return token;
            }
            if ("(".equals(token.getText())) {
                token = parseSubStatement(node, token);
                if (token.type == Token.NL || token.type == Token.EOF) {
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

    void parseInlineCode(Node parent, Token token) {

        while (true) {
            parseDatLine(parent, token);

            token = stream.nextToken();
            if (token.type == Token.EOF) {
                return;
            }
            if ("END".equalsIgnoreCase(token.getText())) {
                Node statement = new StatementNode(parent);
                statement.addToken(token);
                return;
            }
        }
    }

    void parseDat(Token start) {
        Node node = new DataNode(root);
        node.addToken(start);

        Node parent = node;
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Token.EOF) {
                return;
            }
            if (token.type == Token.NL) {
                continue;
            }
            if (parseSection(token)) {
                return;
            }
            if ("ORG".equalsIgnoreCase(token.getText()) || "ORGH".equalsIgnoreCase(token.getText())) {
                parent = node;
            }
            parseDatLine(parent, token);
            if ("ORG".equalsIgnoreCase(token.getText()) || "ORGH".equalsIgnoreCase(token.getText())) {
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
            if (token.type == Token.EOF || token.type == Token.NL) {
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
                    if (conditions.contains(token.getText().toUpperCase())) {
                        parent.condition = new Node(parent);
                        parent.condition.addToken(token);
                        state = 3;
                        break;
                    }
                    // fall-through
                case 3:
                    if (instructions.contains(token.getText().toUpperCase()) || types.contains(token.getText().toUpperCase())) {
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
                    if (modifiers.contains(token.getText().toUpperCase())) {
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
                    if (modifiers.contains(token.getText().toUpperCase())) {
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
                    if (modifiers.contains(token.getText().toUpperCase())) {
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
            + "' Program drives a DAC pin with a ramp pattern, use ctrl-F10 to run\n"
            + "'\n"
            + "' Jumper:       P8  to P0\n"
            + "\n"
            + "CON\n"
            + "  _clkfreq = 300_000_000                'set clock frequency\n"
            + "  _pin     = 8                          'operate DAC on this pin\n"
            + "\n"
            + "OBJ scope : \"simple_SCOPE\"\n"
            + "\n"
            + "PUB go()\n"
            + "\n"
            + "  scope.start(0 addpins 0, $1000_0000, p_adc_1x)\n"
            + "\n"
            + "  org\n"
            + "\n"
            + "        dirh    #_pin\n"
            + "\n"
            + "        rep     @loop,#0\n"
            + "        add     level,#1                'increment level variable\n"
            + "        setbyte mode,level,#1           'write level byte into mode\n"
            + "        wrpin   mode,#_pin              'write mode to pin to update DAC\n"
            + "loop\n"
            + "\n"
            + "mode    long    p_dac_124r_3v           '%0000_0000_000_10110_xxxxxxxx_00_00000_0\n"
            + "level   res     1                       'level variable, gets written to %xxxxxxxx\n"
            + "\n"
            + "  end\n"
            + "";

        try {
            Spin2TokenStream stream = new Spin2TokenStream(text);
            Spin2Parser subject = new Spin2Parser(stream);
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
