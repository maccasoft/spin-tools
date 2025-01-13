/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.maccasoft.propeller.Preferences.ExternalTool;
import com.maccasoft.propeller.Preferences.SerializedPreferences;
import com.maccasoft.propeller.model.MethodNode;

class PreferencesTest {

    @Test
    void testDefaultPreferences() throws Exception {
        Preferences subject = new Preferences();

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{ }", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testSetSpin1LibraryPath() throws Exception {
        File path = new File("spin1/path");

        Preferences subject = new Preferences();
        subject.setSpin1LibraryPath(new File[] {
            path
        });

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{\n"
            + "  \"spin1LibraryPath\" : [ \"" + path.getAbsolutePath() + "\" ]\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testGetSpin1LibraryPath() throws Exception {
        Preferences subject = new Preferences();
        StringReader is = new StringReader(""
            + "{\n"
            + "  \"spin1LibraryPath\" : [ \"spin1/path\" ]\n"
            + "}"
            + "");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        subject.preferences = mapper.readValue(is, SerializedPreferences.class);

        Assertions.assertArrayEquals(new File[] {
            new File("spin1/path")
        }, subject.getSpin1LibraryPath());
    }

    @Test
    void testGetSpin1LibraryWithDefaultPath() throws Exception {
        Preferences subject = new Preferences();
        StringReader is = new StringReader(""
            + "{\n"
            + "  \"spin1LibraryPath\" : [ \"spin1/path\", null ]\n"
            + "}"
            + "");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        subject.preferences = mapper.readValue(is, SerializedPreferences.class);

        Assertions.assertArrayEquals(new File[] {
            new File("spin1/path"),
            Preferences.defaultSpin1LibraryPath
        }, subject.getSpin1LibraryPath());
    }

    @Test
    void testSetSpin1DefaultLibraryPath() throws Exception {
        Preferences subject = new Preferences();
        subject.setSpin1LibraryPath(subject.getSpin1LibraryPath());

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{ }", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testSetSpin1LibraryWithDefaultPath() throws Exception {
        File path = new File("spin1/path");

        Preferences subject = new Preferences();
        subject.setSpin1LibraryPath(new File[] {
            path,
            Preferences.defaultSpin1LibraryPath
        });

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{\n"
            + "  \"spin1LibraryPath\" : [ \"" + path.getAbsolutePath() + "\", null ]\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testSetSpin2LibraryPath() throws Exception {
        File path = new File("spin2/path");

        Preferences subject = new Preferences();
        subject.setSpin2LibraryPath(new File[] {
            path
        });

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{\n"
            + "  \"spin2LibraryPath\" : [ \"" + path.getAbsolutePath() + "\" ]\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testSetSpin2DefaultLibraryPath() throws Exception {
        Preferences subject = new Preferences();
        subject.setSpin2LibraryPath(subject.getSpin2LibraryPath());

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{ }", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testSetSpin2LibraryWithDefaultPath() throws Exception {
        File path = new File("spin2/path");

        Preferences subject = new Preferences();
        subject.setSpin2LibraryPath(new File[] {
            path,
            Preferences.defaultSpin2LibraryPath
        });

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{\n"
            + "  \"spin2LibraryPath\" : [ \"" + path.getAbsolutePath() + "\", null ]\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testGetSpin2LibraryPath() throws Exception {
        Preferences subject = new Preferences();
        StringReader is = new StringReader(""
            + "{\n"
            + "  \"spin2LibraryPath\" : [ \"spin2/path\" ]\n"
            + "}"
            + "");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        subject.preferences = mapper.readValue(is, SerializedPreferences.class);

        Assertions.assertArrayEquals(new File[] {
            new File("spin2/path")
        }, subject.getSpin2LibraryPath());
    }

    @Test
    void testGetSpin2LibraryWithDefaultPath() throws Exception {
        Preferences subject = new Preferences();
        StringReader is = new StringReader(""
            + "{\n"
            + "  \"spin2LibraryPath\" : [ \"spin2/path\", null ]\n"
            + "}"
            + "");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        subject.preferences = mapper.readValue(is, SerializedPreferences.class);

        Assertions.assertArrayEquals(new File[] {
            new File("spin2/path"),
            Preferences.defaultSpin2LibraryPath
        }, subject.getSpin2LibraryPath());
    }

    @Test
    void testGetDefaultTabStops() throws Exception {
        Preferences subject = new Preferences();
        Assertions.assertArrayEquals(Preferences.defaultTabStops.get("pub"), subject.getTabStops(MethodNode.class));
    }

    @Test
    void testSetTabStops() throws Exception {
        Preferences subject = new Preferences();
        subject.setTabStops(MethodNode.class, new int[] {
            8, 16
        });

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{\n"
            + "  \"sectionTabStops\" : {\n"
            + "    \"pub\" : [ 8, 16 ]\n"
            + "  }\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testGetTabStops() throws Exception {
        Preferences subject = new Preferences();
        StringReader is = new StringReader(""
            + "{\n"
            + "  \"sectionTabStops\" : {\n"
            + "    \"pub\" : [ 8, 16 ]\n"
            + "  }\n"
            + "}"
            + "");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        subject.preferences = mapper.readValue(is, SerializedPreferences.class);

        Assertions.assertArrayEquals(new int[] {
            8, 16
        }, subject.getTabStops(MethodNode.class));
    }

    @Test
    void testSpin1DefaultLibraryNotifications() throws Exception {
        AtomicBoolean notify = new AtomicBoolean(false);

        Preferences subject = new Preferences();
        subject.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                notify.set(true);
            }
        });

        File[] paths = subject.getSpin1LibraryPath();
        subject.setSpin1LibraryPath(paths);

        Assertions.assertFalse(notify.get());
    }

    @Test
    void testSpin2DefaultLibraryNotifications() throws Exception {
        AtomicBoolean notify = new AtomicBoolean(false);

        Preferences subject = new Preferences();
        subject.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                notify.set(true);
            }
        });

        File[] paths = subject.getSpin2LibraryPath();
        subject.setSpin2LibraryPath(paths);

        Assertions.assertFalse(notify.get());
    }

    @Test
    void testSetVisiblePathsNotifications() throws Exception {
        AtomicBoolean notify = new AtomicBoolean(false);
        AtomicReference<File[]> ref = new AtomicReference<>();

        File[] data = new File[] {
            new File("a"), new File("b"), new File("c")
        };

        Preferences subject = new Preferences();
        subject.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ref.set((File[]) evt.getNewValue());
                notify.set(true);
            }
        });

        subject.setRoots(data);

        Assertions.assertTrue(notify.get());
        Assertions.assertArrayEquals(data, ref.get());
        Assertions.assertArrayEquals(data, subject.preferences.roots);
    }

    @Test
    void testUpdateVisiblePathsNotifications() throws Exception {
        AtomicBoolean notify = new AtomicBoolean(false);
        AtomicReference<File[]> ref = new AtomicReference<>();

        File[] data = new File[] {
            new File("a"), new File("b")
        };

        Preferences subject = new Preferences();
        subject.preferences.roots = new File[] {
            new File("a"), new File("b"), new File("c")
        };
        subject.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ref.set((File[]) evt.getNewValue());
                notify.set(true);
            }
        });

        subject.setRoots(data);

        Assertions.assertTrue(notify.get());
        Assertions.assertArrayEquals(data, ref.get());
        Assertions.assertArrayEquals(data, subject.preferences.roots);
    }

    @Test
    void testDefaultVisiblePathsNotifications() throws Exception {
        AtomicBoolean notify = new AtomicBoolean(false);
        AtomicReference<File[]> ref = new AtomicReference<>();

        Preferences subject = new Preferences();
        subject.preferences.roots = new File[] {
            new File("a"), new File("b"), new File("c")
        };
        subject.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ref.set((File[]) evt.getNewValue());
                notify.set(true);
            }
        });

        subject.setRoots(null);

        Assertions.assertTrue(notify.get());
        Assertions.assertArrayEquals(Preferences.defaultVisiblePaths, ref.get());
        Assertions.assertNull(subject.preferences.roots);
    }

    @Test
    void testSameVisiblePathsNotifications() throws Exception {
        AtomicBoolean notify = new AtomicBoolean(false);
        AtomicReference<File[]> ref = new AtomicReference<>();

        File[] data = new File[] {
            new File("a"), new File("b"), new File("c")
        };

        Preferences subject = new Preferences();
        subject.preferences.roots = new File[] {
            new File("a"), new File("b"), new File("c")
        };
        subject.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ref.set((File[]) evt.getNewValue());
                notify.set(true);
            }
        });

        subject.setRoots(data);

        Assertions.assertFalse(notify.get());
        Assertions.assertArrayEquals(data, subject.preferences.roots);
    }

    @Test
    void testSpin1Defines() throws Exception {
        Preferences subject = new Preferences();

        Map<String, String> defines = new HashMap<>();
        defines.put("KEY", "VALUE");
        defines.put("EMPTY_KEY", "");
        subject.setSpin1Defines(defines);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{\n"
            + "  \"spin1Defines\" : {\n"
            + "    \"EMPTY_KEY\" : \"\",\n"
            + "    \"KEY\" : \"VALUE\"\n"
            + "  }\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testSpin2Defines() throws Exception {
        Preferences subject = new Preferences();

        Map<String, String> defines = new HashMap<>();
        defines.put("KEY", "VALUE");
        defines.put("EMPTY_KEY", "");
        subject.setSpin2Defines(defines);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{\n"
            + "  \"spin2Defines\" : {\n"
            + "    \"EMPTY_KEY\" : \"\",\n"
            + "    \"KEY\" : \"VALUE\"\n"
            + "  }\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testSetExternalTools() throws Exception {
        Preferences subject = new Preferences();

        ExternalTool tool = new ExternalTool("flexspin", "/usr/bin/flexpin", "-2");
        subject.setExternalTools(new ExternalTool[] {
            tool
        });

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        mapper.writeValue(os, subject.preferences);

        Assertions.assertEquals(""
            + "{\n"
            + "  \"externalTools\" : [ {\n"
            + "    \"name\" : \"flexspin\",\n"
            + "    \"program\" : \"/usr/bin/flexpin\",\n"
            + "    \"arguments\" : \"-2\"\n"
            + "  } ]\n"
            + "}", os.toString().replaceAll("\\r\\n", "\n"));
    }

    @Test
    void testGetExternalTools() throws Exception {
        Preferences subject = new Preferences();
        StringReader is = new StringReader(""
            + "{\n"
            + "  \"externalTools\" : [ {\n"
            + "    \"name\" : \"flexspin\",\n"
            + "    \"program\" : \"/usr/bin/flexpin\",\n"
            + "    \"arguments\" : \"-2\"\n"
            + "  } ]\n"
            + "}");

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        subject.preferences = mapper.readValue(is, SerializedPreferences.class);

        ExternalTool[] tools = subject.getExternalTools();

        Assertions.assertEquals(1, tools.length);
        Assertions.assertEquals("flexspin", tools[0].name);
    }

}
