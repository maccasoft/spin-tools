/*
 * Copyright (c) 2021 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class PreferencesTest {

    @Test
    void testSetSpin1LibraryPath() throws Exception {
        Preferences subject = new Preferences();
        subject.setSpin1LibraryPath("spin1/path");

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.writeValue(os, subject);

        Assertions.assertEquals("{\n"
            + "  \"showLineNumbers\" : false,\n"
            + "  \"spin1LibraryPath\" : \"spin1/path\"\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testGetSpin1LibraryPath() throws Exception {
        StringReader is = new StringReader(""
            + "{\n"
            + "  \"showLineNumbers\" : false,\n"
            + "  \"spin1LibraryPath\" : \"spin1/path\"\n"
            + "}"
            + "");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Preferences subject = mapper.readValue(is, Preferences.class);

        Assertions.assertEquals("spin1/path", subject.getSpin1LibraryPath());
    }

    @Test
    void testSe2Spin1LibraryPath() throws Exception {
        Preferences subject = new Preferences();
        subject.setSpin2LibraryPath("spin2/path");

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.writeValue(os, subject);

        Assertions.assertEquals("{\n"
            + "  \"showLineNumbers\" : false,\n"
            + "  \"spin2LibraryPath\" : \"spin2/path\"\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testGetSpin2LibraryPath() throws Exception {
        StringReader is = new StringReader(""
            + "{\n"
            + "  \"showLineNumbers\" : false,\n"
            + "  \"spin2LibraryPath\" : \"spin2/path\"\n"
            + "}"
            + "");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Preferences subject = mapper.readValue(is, Preferences.class);

        Assertions.assertEquals("spin2/path", subject.getSpin2LibraryPath());
    }

}
