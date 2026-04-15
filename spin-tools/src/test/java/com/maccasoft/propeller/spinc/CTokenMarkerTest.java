/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.spinc;

import java.util.Collection;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.SourceTokenMarker.TokenMarker;
import com.maccasoft.propeller.model.SourceProvider;

class CTokenMarkerTest {

    @Test
    void testComments() {
        String text = ""
            + "/*\n"
            + "     Constant declarations\n"
            + "*/\n"
            + "#define A 1\n"
            + "";

        CTokenMarker subject = new CTokenMarker(SourceProvider.NULL);
        subject.refreshTokens(text);

        Collection<TokenMarker> result = subject.getTokens(1, 3, "     Constant declarations");
        Assertions.assertEquals(1, result.size());

        Iterator<TokenMarker> iter = result.iterator();
        Assertions.assertEquals(TokenId.COMMENT, iter.next().getId());
    }

}
