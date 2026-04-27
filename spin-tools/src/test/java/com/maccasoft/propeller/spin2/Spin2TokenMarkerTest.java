/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spin2;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.SourceTokenMarker.TokenMarker;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;

class Spin2TokenMarkerTest {

    @Test
    void testSectionConstants() {
        String text = "CON  EnableFlow = 8                ' Single assignments";

        Spin2TokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL);
        subject.setRoot(new RootNode());

        Collection<TokenMarker> result = subject.getTokens(0, 0, text);

        TokenMarker[] markers = result.toArray(new TokenMarker[0]);
        Assertions.assertEquals(4, markers.length);
        Assertions.assertEquals(TokenId.SECTION, markers[0].getId());
        Assertions.assertEquals(TokenId.CONSTANT, markers[1].getId());
        Assertions.assertEquals(TokenId.NUMBER, markers[2].getId());
        Assertions.assertEquals(TokenId.COMMENT, markers[3].getId());
    }

    @Test
    void testLineStartsInBlockComment() {
        String text = ""
            + "CON {\n"
            + "  Constant declarations\n"
            + "  } EnableFlow = 8 ' Single assignments\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL);
        subject.refreshTokens(text);

        Collection<TokenMarker> result = subject.getTokens(2, 30, text.substring(30, 69));

        TokenMarker[] markers = result.toArray(new TokenMarker[0]);
        Assertions.assertEquals(4, markers.length);
        Assertions.assertEquals(TokenId.COMMENT, markers[0].getId());
        Assertions.assertEquals(TokenId.CONSTANT, markers[1].getId());
        Assertions.assertEquals(TokenId.NUMBER, markers[2].getId());
        Assertions.assertEquals(TokenId.COMMENT, markers[3].getId());
    }

    @Test
    void testReferenceOtherConstants() {
        String text = ""
            + "  A = 1\n"
            + "  B = A + 1\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL);
        subject.refreshTokens(text);

        Collection<TokenMarker> result = subject.getTokens(1, 8, text.substring(8, 19));

        TokenMarker[] markers = result.toArray(new TokenMarker[0]);
        Assertions.assertEquals(3, markers.length);
        Assertions.assertEquals(TokenId.CONSTANT, markers[0].getId());
        Assertions.assertEquals(TokenId.CONSTANT, markers[1].getId());
        Assertions.assertEquals(TokenId.NUMBER, markers[2].getId());
    }

    @Test
    void testStructureMembers() {
        String lineText;
        TokenMarker[] markers;

        String text = ""
            + "VAR\n"
            + "    sLine line\n"
            + "\n"
            + "PUB main() | a, local, idx\n"
            + "\n"
            + "    a := line.x\n"
            + "    a := line[idx + 1].x\n"
            + "\n"
            + "    a := local.x\n"
            + "    a := local[idx + 1].x\n"
            + "\n"
            + "    a := line.x[idx].a.b\n"
            + "";

        Spin2TokenMarker subject = new Spin2TokenMarker(SourceProvider.NULL);
        subject.refreshTokens(text);

        lineText = "    a := line.x";
        markers = subject.getTokens(5, text.indexOf(lineText), lineText).toArray(new TokenMarker[0]);
        Assertions.assertEquals(3, markers.length);
        Assertions.assertEquals(TokenId.METHOD_LOCAL, markers[0].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[1].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[2].getId());

        lineText = "    a := line[0].x";
        markers = subject.getTokens(6, text.indexOf(lineText), lineText).toArray(new TokenMarker[0]);
        Assertions.assertEquals(4, markers.length);
        Assertions.assertEquals(TokenId.METHOD_LOCAL, markers[0].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[1].getId());
        Assertions.assertEquals(TokenId.NUMBER, markers[2].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[3].getId());

        lineText = "    a := local.x";
        markers = subject.getTokens(8, text.indexOf(lineText), lineText).toArray(new TokenMarker[0]);
        Assertions.assertEquals(3, markers.length);
        Assertions.assertEquals(TokenId.METHOD_LOCAL, markers[0].getId());
        Assertions.assertEquals(TokenId.METHOD_LOCAL, markers[1].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[2].getId());

        lineText = "    a := local[0].x";
        markers = subject.getTokens(9, text.indexOf(lineText), lineText).toArray(new TokenMarker[0]);
        Assertions.assertEquals(4, markers.length);
        Assertions.assertEquals(TokenId.METHOD_LOCAL, markers[0].getId());
        Assertions.assertEquals(TokenId.METHOD_LOCAL, markers[1].getId());
        Assertions.assertEquals(TokenId.NUMBER, markers[2].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[3].getId());

        lineText = "    line.x[idx].a.b";
        markers = subject.getTokens(9, text.indexOf(lineText), lineText).toArray(new TokenMarker[0]);
        Assertions.assertEquals(5, markers.length);
        Assertions.assertEquals(TokenId.VARIABLE, markers[0].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[1].getId());
        Assertions.assertEquals(TokenId.METHOD_LOCAL, markers[2].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[3].getId());
        Assertions.assertEquals(TokenId.VARIABLE, markers[4].getId());
    }

}
