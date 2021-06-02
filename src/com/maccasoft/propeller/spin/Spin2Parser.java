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

import com.maccasoft.propeller.spin.Spin2TokenStream.Token;

public class Spin2Parser {

    static Set<String> instructions = new HashSet<String>(Arrays.asList(new String[] {
        "ORG", "ORGH", "ORGF", "FIT",
        "BYTE", "WORD", "LONG", "RES", "FILE",
        "ASMCLK",
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

    static Set<String> conditions = new HashSet<String>(Arrays.asList(new String[] {
        "_RET_",
        "IF_NC_AND_NZ", "IF_NZ_AND_NC", "IF_GT", "IF_A", "IF_00", "IF_NC_AND_Z", "IF_Z_AND_NC", "IF_01", "IF_NC", "IF_GE", "IF_AE",
        "IF_0X", "IF_C_AND_NZ", "IF_NZ_AND_C", "IF_10", "IF_NZ", "IF_NE", "IF_X0", "IF_C_NE_Z", "IF_Z_NE_C", "IF_DIFF",
        "IF_NC_OR_NZ", "IF_NZ_OR_NC", "IF_NOT_11", "IF_C_AND_Z", "IF_Z_AND_C", "IF_11", "IF_C_EQ_Z", "IF_Z_EQ_C", "IF_SAME",
        "IF_Z", "IF_E", "IF_X1", "IF_NC_OR_Z", "IF_Z_OR_NC", "IF_NOT_10", "IF_C", "IF_LT", "IF_B", "IF_1X", "IF_C_OR_NZ",
        "IF_NZ_OR_C", "IF_NOT_01", "IF_C_OR_Z", "IF_Z_OR_C", "IF_LE", "IF_BE", "IF_NOT_00",
    }));

    static Set<String> modifiers = new HashSet<String>(Arrays.asList(new String[] {
        "WC", "WZ", "WCZ",
        "ANDC", "ANDZ", "ORC", "ORZ", "XORC", "XORZ",
    }));

    static Set<String> types = new HashSet<String>(Arrays.asList(new String[] {
        "LONG", "WORD", "BYTE",
    }));

    final Spin2TokenStream stream;

    public class Node {

        final Node parent;
        final List<Token> tokens = new ArrayList<Token>();
        final List<Node> childs = new ArrayList<Node>();

        public Node(Node parent) {
            this(parent, true);
        }

        public Node(Node parent, boolean addToParent) {
            this.parent = parent;
            if (parent != null && addToParent) {
                parent.childs.add(this);
            }
        }

        public void accept(Spin2ParserVisitor visitor) {
            for (Node child : childs) {
                child.accept(visitor);
            }
        }

        public List<Token> getTokens() {
            return tokens;
        }

        public Token getToken(int index) {
            return tokens.get(index);
        }

        public Node getParent() {
            return parent;
        }

        public List<Node> getChilds() {
            return childs;
        }

        public int getStartIndex() {
            return tokens.size() != 0 ? tokens.get(0).start : -1;
        }

        public int getStopIndex() {
            return tokens.size() != 0 ? tokens.get(tokens.size() - 1).stop : -1;
        }

        public String getText() {
            StringBuilder sb = new StringBuilder();
            for (Token token : tokens) {
                sb.append(token.getText());
            }
            return sb.toString();
        }

    }

    public class ErrorNode extends Node {

        public ErrorNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitError(this);
        }

    }

    Node root;

    public Spin2Parser(Spin2TokenStream stream) {
        this.stream = stream;
    }

    public Node parse() {
        root = new Node(null);

        while (true) {
            Token token = stream.nextToken();
            if (token.type == Spin2TokenStream.EOF) {
                break;
            }
            if (token.type == Spin2TokenStream.NL) {
                continue;
            }
            if (!parseSection(token)) {
                Node node = new ErrorNode(root);
                node.tokens.add(token);
            }
        }

        return root;
    }

    boolean parseSection(Token token) {
        if ("CON".equalsIgnoreCase(token.getText())) {
            parseCon(token);
            return true;
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
        return false;
    }

    public class ExpressionNode extends Node {

        public ExpressionNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitExpression(this);
            super.accept(visitor);
        }

    }

    public class ConstantsNode extends Node {

        public ConstantsNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitConstants(this);
            super.accept(visitor);
        }

    }

    public class ConstantSetEnumNode extends Node {

        ExpressionNode start;
        ExpressionNode step;

        public ConstantSetEnumNode(Node parent, List<Token> tokens) {
            super(parent);
            this.start = new ExpressionNode(this);

            int i = 1;
            while (i < tokens.size()) {
                Token token = tokens.get(i++);
                if ("[".equals(token.getText())) {
                    break;
                }
                this.start.tokens.add(token);
            }
            if (i < tokens.size()) {
                this.step = new ExpressionNode(this);
                this.step.tokens.addAll(tokens.subList(i, tokens.size() - 1));
            }
            this.tokens.addAll(tokens);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitConstantSetEnum(this);
            super.accept(visitor);
        }

    }

    public class ConstantAssignEnumNode extends Node {

        Node identifier;
        ExpressionNode multiplier;

        public ConstantAssignEnumNode(Node parent, List<Token> tokens) {
            super(parent);

            int i = 0;
            this.identifier = new Node(this);
            this.identifier.tokens.add(tokens.get(i++));

            if (i < tokens.size() && "[".equals(tokens.get(i).getText())) {
                this.multiplier = new ExpressionNode(this);
                this.multiplier.tokens.addAll(tokens.subList(i + 1, tokens.size() - 1));
            }

            this.tokens.addAll(tokens);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitConstantAssignEnum(this);
            super.accept(visitor);
        }

        public Node getIdentifier() {
            return identifier;
        }

        public ExpressionNode getMultiplier() {
            return multiplier;
        }

    }

    public class ConstantAssignNode extends Node {

        Node identifier;
        ExpressionNode expression;

        public ConstantAssignNode(Node parent, List<Token> childs) {
            super(parent);
            this.identifier = new Node(this);
            this.identifier.tokens.add(childs.get(0));
            this.expression = new ExpressionNode(this);
            this.expression.tokens.addAll(childs.subList(2, childs.size()));
            this.tokens.addAll(tokens);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitConstantAssign(this);
            super.accept(visitor);
        }

        public Node getIdentifier() {
            return identifier;
        }

        public ExpressionNode getExpression() {
            return expression;
        }

    }

    void parseCon(Token start) {
        List<Token> list = new ArrayList<Token>();

        ConstantsNode node = new ConstantsNode(root);
        node.tokens.add(start);

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
                    if (",".equals(token.getText()) || token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF) {
                        Node child = null;

                        if (list.size() == 1) {
                            new ConstantAssignEnumNode(node, list);
                        }
                        else if (list.size() >= 2) {
                            if ("#".equals(list.get(0).getText())) {
                                new ConstantSetEnumNode(node, list);
                            }
                            else if (list.size() >= 3) {
                                if ("=".equals(list.get(1).getText())) {
                                    new ConstantAssignNode(node, list);
                                }
                                else if ("[".equals(list.get(1).getText())) {
                                    new ConstantAssignEnumNode(node, list);
                                }
                                else {
                                    child = new ErrorNode(node);
                                }
                            }
                            else {
                                child = new ErrorNode(node);
                            }
                        }

                        if (child != null) {
                            child.tokens.addAll(list);
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

    class VariablesNode extends Node {

        public VariablesNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitVariables(this);
            super.accept(visitor);
        }

    }

    class VariableNode extends Node {

        Node type;
        Node identifier;
        ExpressionNode size;

        public VariableNode(Node parent, List<Token> tokens) {
            super(parent);

            int i = 0;
            if (types.contains(tokens.get(i).getText().toUpperCase())) {
                this.type = new Node(this);
                this.type.tokens.add(tokens.get(i));
                i++;
            }

            if (i < tokens.size()) {
                this.identifier = new Node(this);
                this.identifier.tokens.add(tokens.get(i++));
            }

            if (i < tokens.size() && "[".equals(tokens.get(i).getText())) {
                this.size = new ExpressionNode(this);
                this.size.tokens.addAll(tokens.subList(i + 1, tokens.size() - 1));
            }

            this.tokens.addAll(tokens);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitVariable(this);
        }

        public Node getType() {
            return type;
        }

        public Node getIdentifier() {
            return identifier;
        }

        public ExpressionNode getSize() {
            return size;
        }

    }

    void parseVar(Token start) {
        List<Token> list = new ArrayList<Token>();

        Node node = new VariablesNode(root);
        node.tokens.add(start);

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
                    if (",".equals(token.getText()) || token.type == Spin2TokenStream.NL || token.type == Spin2TokenStream.EOF) {
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

    public class ObjectsNode extends Node {

        public ObjectsNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitObjects(this);
            super.accept(visitor);
        }

    }

    public class ObjectNode extends Node {

        public ObjectNode(Node parent) {
            super(parent);
        }

    }

    void parseObj(Token start) {
        List<Token> list = new ArrayList<Token>();

        Node node = new ObjectsNode(root);
        node.tokens.add(start);

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
                            child.tokens.addAll(list);
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

    public class MethodNode extends Node {

        Node type;
        Node name;
        List<Node> parameters = new ArrayList<Node>();
        List<Node> returnVariables = new ArrayList<Node>();
        List<LocalVariableNode> localVariables = new ArrayList<LocalVariableNode>();

        public MethodNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitMethod(this);
            super.accept(visitor);
        }

        public Node getType() {
            return type;
        }

        public Node getName() {
            return name;
        }

        public List<Node> getParameters() {
            return parameters;
        }

        public Node getParameter(int index) {
            return parameters.get(index);
        }

        public List<Node> getReturnVariables() {
            return returnVariables;
        }

        public Node getReturnVariable(int index) {
            return returnVariables.get(index);
        }

        public List<LocalVariableNode> getLocalVariables() {
            return localVariables;
        }

        public LocalVariableNode getLocalVariable(int index) {
            return localVariables.get(index);
        }

    }

    class LocalVariableNode extends Node {

        Node type;
        Node identifier;
        ExpressionNode size;

        public LocalVariableNode(Node parent) {
            super(parent);
        }

        public Node getType() {
            return type;
        }

        public Node getIdentifier() {
            return identifier;
        }

        public ExpressionNode getSize() {
            return size;
        }

    }

    class StatementNode extends Node {

        public StatementNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitStatement(this);
            super.accept(visitor);
        }

    }

    void parseMethod(Token start) {
        MethodNode node = new MethodNode(root);
        node.type = new Node(node);
        node.type.tokens.add(start);

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

                    if ("ORG".equalsIgnoreCase(token.getText())) {
                        parseInlineCode(child, token);
                        break;
                    }

                    if (child.tokens.size() != 0) {
                        while (token.column < child.tokens.get(0).column && child != node) {
                            child = child.getParent();
                            parent = child.getParent();
                        }

                        if (token.column > child.tokens.get(0).column) {
                            parent = child;
                        }
                    }

                    child = new StatementNode(parent);
                    state = 1;
                    // fall-through
                case 1:
                    child.tokens.add(token);
                    break;

                case 2:
                    if (token.type == 0) {
                        node.name = new Node(node);
                        node.name.tokens.add(token);
                        state = 3;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
                    state = 1;
                    break;

                case 3:
                    if ("(".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    else if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    else if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
                    state = 1;
                    break;

                case 4:
                    if (")".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    param = new Node(node);
                    param.tokens.add(token);
                    node.parameters.add(param);
                    state = 5;
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        state = 4;
                        break;
                    }
                    if (")".equals(token.getText())) {
                        state = 6;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
                    state = 1;
                    break;

                case 6:
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
                    state = 1;
                    break;

                case 7:
                    ret = new Node(node);
                    ret.tokens.add(token);
                    node.returnVariables.add(ret);
                    state = 8;
                    break;
                case 8:
                    if (",".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    else if ("|".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
                    state = 1;
                    break;

                case 9:
                    local = new LocalVariableNode(node);
                    node.localVariables.add(local);
                    if (types.contains(token.getText().toUpperCase())) {
                        local.type = new Node(local);
                        local.type.tokens.add(token);
                        state = 10;
                        break;
                    }
                    // fall-through
                case 10:
                    if (token.type == 0) {
                        local.identifier = new Node(local);
                        local.identifier.tokens.add(token);
                        state = 11;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
                    state = 1;
                    break;
                case 11:
                    if (",".equals(token.getText())) {
                        state = 9;
                        break;
                    }
                    if ("[".equals(token.getText())) {
                        local.size = new ExpressionNode(local);
                        state = 12;
                        break;
                    }
                    if (":".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    local.identifier.tokens.add(token);
                    break;
                case 12:
                    if ("]".equals(token.getText())) {
                        state = 11;
                        break;
                    }
                    local.size.tokens.add(token);
                    break;
            }
        }
    }

    void parseInlineCode(Node parent, Token token) {

        while (true) {
            parseDatLine(parent, token);

            token = stream.nextToken();
            if (token.type == Spin2TokenStream.EOF) {
                return;
            }
            if ("END".equalsIgnoreCase(token.getText())) {
                return;
            }
        }
    }

    public class DataNode extends Node {

        public DataNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitData(this);
            super.accept(visitor);
        }

    }

    public class DataLineNode extends Node {

        public Node label;
        public Node condition;
        public Node instruction;
        public List<ParameterNode> parameters = new ArrayList<ParameterNode>();
        public Node modifier;

        public DataLineNode(Node parent) {
            super(parent);
        }

        @Override
        public void accept(Spin2ParserVisitor visitor) {
            visitor.visitDataLine(this);
            super.accept(visitor);
        }

    }

    public class ParameterNode extends Node {

        public ExpressionNode count;

        public ParameterNode(Node parent) {
            super(parent);
        }

        public ExpressionNode getCount() {
            return count;
        }

    }

    void parseDat(Token start) {
        Node node = new DataNode(root);
        node.tokens.add(start);

        Node parent = node;
        while (true) {
            Token token = stream.nextToken();
            if (token.type == Spin2TokenStream.EOF) {
                return;
            }
            if (parseSection(token)) {
                return;
            }
            if ("ORG".equalsIgnoreCase(token.getText()) || "ORGH".equalsIgnoreCase(token.getText())) {
                parent = node;
            }
            parseDatLine(parent, token);
            if ("ORG".equalsIgnoreCase(token.getText()) || "ORGH".equalsIgnoreCase(token.getText())) {
                parent = node.childs.get(node.childs.size() - 1);
            }
        }
    }

    void parseDatLine(Node node, Token token) {
        int state = 0;
        DataLineNode parent = null;
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
                    parent = new DataLineNode(node);
                    state = 1;
                    // fall-through
                case 1:
                    if (token.column == 0) {
                        parent.label = new Node(parent);
                        parent.label.tokens.add(token);
                        state = 2;
                        break;
                    }
                    // fall-through
                case 2:
                    if (conditions.contains(token.getText().toUpperCase())) {
                        parent.condition = new Node(parent);
                        parent.condition.tokens.add(token);
                        state = 3;
                        break;
                    }
                    // fall-through
                case 3:
                    if (types.contains(token.getText().toUpperCase())) {

                    }
                    if (instructions.contains(token.getText().toUpperCase())) {
                        parent.instruction = new Node(parent);
                        parent.instruction.tokens.add(token);
                        state = 4;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
                    state = 9;
                    break;
                case 4:
                    if (modifiers.contains(token.getText().toUpperCase())) {
                        parent.modifier = new Node(parent);
                        parent.modifier.tokens.add(token);
                        state = 5;
                        break;
                    }
                    parameter = new ParameterNode(parent);
                    parameter.tokens.add(token);
                    parent.parameters.add(parameter);
                    state = 7;
                    break;
                case 5:
                    if (",".equals(token.getText())) {
                        parent.modifier.tokens.add(token);
                        state = 6;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
                    state = 9;
                    break;
                case 6:
                    if (modifiers.contains(token.getText().toUpperCase())) {
                        parent.modifier.tokens.add(token);
                        state = 5;
                        break;
                    }
                    child = new ErrorNode(parent);
                    child.tokens.add(token);
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
                        parent.modifier.tokens.add(token);
                        state = 5;
                        break;
                    }

                    if (parameter.tokens.size() != 0) {
                        Token prev = parameter.tokens.get(parameter.tokens.size() - 1);
                        if (prev.type != Spin2TokenStream.NUMBER) {
                            if ("++".equals(token.getText()) || "--".equals(token.getText()) || "[".equals(token.getText())) {
                                parameter.tokens.set(parameter.tokens.size() - 1, stream.new Token(prev, token));
                                break;
                            }
                            if ("++".equals(prev.getText()) || "--".equals(prev.getText())) {
                                parameter.tokens.set(parameter.tokens.size() - 1, stream.new Token(prev, token));
                                break;
                            }
                            if (prev.getText().contains("[") && !prev.getText().contains("]")) {
                                parameter.tokens.set(parameter.tokens.size() - 1, stream.new Token(prev, token));
                                break;
                            }
                        }
                    }

                    if ("[".equals(token.getText())) {
                        parameter.count = new ExpressionNode(parameter);
                        state = 10;
                        break;
                    }

                    parameter.tokens.add(token);
                    break;
                case 9:
                    child.tokens.add(token);
                    break;
                case 10:
                    if ("]".equals(token.getText())) {
                        state = 7;
                        break;
                    }
                    parameter.count.tokens.add(token);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        String text = ""
            + "CON\n"
            + "    _clkfreq    = 160_000_000\n"
            + "\n"
            + "PUB main() | ct\n"
            + "\n"
            + "    ct := getct()                   ' get current timer\n"
            + "    repeat\n"
            + "        pint(56)                    ' toggle pin 56\n"
            + "        waitct(ct += _clkfreq / 2)  ' wait half second\n"
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
        for (Token token : node.tokens) {
            System.out.print(" [" + token.getText().replaceAll("\n", "\\n") + "]");
        }
        System.out.println();

        for (Node child : node.childs) {
            print(child, indent + 1);
        }
    }
}
