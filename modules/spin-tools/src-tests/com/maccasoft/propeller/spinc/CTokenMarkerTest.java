/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.SourceTokenMarker.TokenMarker;

class CTokenMarkerTest {

    @Test
    void testComments() {
        String text = ""
            + "/*\n"
            + "     Constant declarations\n"
            + "*/\n"
            + "#define A 1\n"
            + "";

        CTokenMarker subject = new CTokenMarker();
        subject.refreshTokens(text);
        Iterator<TokenMarker> iter = subject.getTokens().iterator();

        TokenMarker entry = iter.next();
        Assertions.assertEquals(TokenId.COMMENT, entry.getId());
    }

}
