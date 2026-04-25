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

}
