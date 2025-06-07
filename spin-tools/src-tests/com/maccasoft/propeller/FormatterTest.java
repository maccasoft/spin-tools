/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.model.Token;
import com.maccasoft.propeller.spin2.Spin2Formatter;

class FormatterTest {

    @Test
    void testSectionLowerCase() {
        String text = ""
            + "Con ' Constants\n"
            + "Var ' Variables\n"
            + "Obj ' Objects\n"
            + "Pub Method1 ' Methods\n"
            + "Pri Method2\n"
            + "Dat ' Data\n"
            + "";

        Formatter subject = new Spin2Formatter();
        subject.sectionCase = Formatter.Case.Lower;

        Assertions.assertEquals(""
            + "con ' Constants\n"
            + "var ' Variables\n"
            + "obj ' Objects\n"
            + "pub Method1 ' Methods\n"
            + "pri Method2\n"
            + "dat ' Data\n"
            + "", subject.format(text));
    }

    @Test
    void testSectionUpperCase() {
        String text = ""
            + "Con ' Constants\n"
            + "Var ' Variables\n"
            + "Obj ' Objects\n"
            + "Pub Method1 ' Methods\n"
            + "Pri Method2\n"
            + "Dat ' Data\n"
            + "";

        Formatter subject = new Spin2Formatter();
        subject.sectionCase = Formatter.Case.Upper;

        Assertions.assertEquals(""
            + "CON ' Constants\n"
            + "VAR ' Variables\n"
            + "OBJ ' Objects\n"
            + "PUB Method1 ' Methods\n"
            + "PRI Method2\n"
            + "DAT ' Data\n"
            + "", subject.format(text));
    }

    @Test
    void testBlockCommentAlignIndent() {
        String text = ""
            + "PUB Method\n"
            + "{{ Block 1 }}\n"
            + "    if a > 1\n"
            + "{{ Block 2 }}\n"
            + "";

        Formatter subject = new Spin2Formatter();
        subject.blockCommentIndentAlign = true;

        Assertions.assertEquals(""
            + "PUB Method\n"
            + "{{ Block 1 }}\n"
            + "    if a > 1\n"
            + "    {{ Block 2 }}\n"
            + "", subject.format(text));
    }

    @Test
    void testMultilineBlockCommentAlignIndent() {
        String text = ""
            + "PUB Method\n"
            + "{{\n"
            + "   Block 1\n"
            + "}}\n"
            + "    if a > 1\n"
            + "{{\n"
            + "   Block 2\n"
            + "}}\n"
            + "";

        Formatter subject = new Spin2Formatter();
        subject.blockCommentIndentAlign = true;

        Assertions.assertEquals(""
            + "PUB Method\n"
            + "{{\n"
            + "   Block 1\n"
            + "}}\n"
            + "    if a > 1\n"
            + "    {{\n"
            + "       Block 2\n"
            + "    }}\n"
            + "", subject.format(text));
    }

    @Test
    void testAlignBlockCommentIndent() {
        String text = ""
            + "{{\n"
            + "   Block 1\n"
            + "}}"
            + "";
        Token token = new Token(Token.BLOCK_COMMENT, text);

        Formatter subject = new Spin2Formatter();

        Assertions.assertEquals(""
            + "{{\n"
            + "       Block 1\n"
            + "    }}"
            + "", subject.alignBlockComment(token, 4));
    }

    @Test
    void testAlignBlockCommentUnindent() {
        String text = ""
            + "{{\n"
            + "       Block 1\n"
            + "    }}"
            + "";
        Token token = new Token(Token.BLOCK_COMMENT, text);
        token.column = 4;

        Formatter subject = new Spin2Formatter();

        Assertions.assertEquals(""
            + "{{\n"
            + "   Block 1\n"
            + "}}"
            + "", subject.alignBlockComment(token, 0));
    }

}
