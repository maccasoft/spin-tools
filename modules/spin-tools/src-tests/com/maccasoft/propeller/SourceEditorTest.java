/*
 * Copyright (c) 2021-23 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.SourceTokenMarker.TokenMarker;

@TestInstance(Lifecycle.PER_CLASS)
public class SourceEditorTest {

    Display display;
    Shell shell;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @BeforeEach
    void setUp() {
        shell = new Shell(display);
    }

    @AfterEach
    void tearDown() {
        while (display.readAndDispatch()) {

        }
        shell.dispose();
        while (display.readAndDispatch()) {

        }
    }

    @AfterAll
    void terminate() {
        display.dispose();
    }

    @Test
    public void testGetFilterText() throws Exception {
        SourceEditor subject = new SourceEditor(shell);
        Assertions.assertEquals("met", subject.getFilterText(null, "    method", 7));
    }

    @Test
    public void testGetIbjectFilterText() throws Exception {
        SourceEditor subject = new SourceEditor(shell);
        Assertions.assertEquals("obj", subject.getFilterText(null, "    object.method", 7));
        Assertions.assertEquals("object.", subject.getFilterText(null, "    object.method", 11));
        Assertions.assertEquals("object.met", subject.getFilterText(null, "    object.method", 14));
    }

    @Test
    public void testGetObjectArrayFilterText() throws Exception {
        SourceEditor subject = new SourceEditor(shell);
        Assertions.assertEquals("obj", subject.getFilterText(null, "    object[0].method", 7));
        Assertions.assertEquals("object.", subject.getFilterText(null, "    object[0].method", 14));
        Assertions.assertEquals("object.met", subject.getFilterText(null, "    object[0].method", 17));
    }

    @Test
    public void testGetObjectArrayExpressionFilterText() throws Exception {
        SourceEditor subject = new SourceEditor(shell);
        Assertions.assertEquals("obj", subject.getFilterText(null, "    object[byte[a][0]].method", 7));
        Assertions.assertEquals("object.", subject.getFilterText(null, "    object[byte[a][0]].method", 23));
        Assertions.assertEquals("object.met", subject.getFilterText(null, "    object[byte[a][0]].method", 26));
    }

    class StyledTextContentMock implements StyledTextContent {

        @Override
        public void addTextChangeListener(TextChangeListener listener) {
        }

        @Override
        public int getCharCount() {
            return 0;
        }

        @Override
        public String getLine(int lineIndex) {
            return null;
        }

        @Override
        public int getLineAtOffset(int offset) {
            return 0;
        }

        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public String getLineDelimiter() {
            return null;
        }

        @Override
        public int getOffsetAtLine(int lineIndex) {
            return 0;
        }

        @Override
        public String getTextRange(int start, int length) {
            return null;
        }

        @Override
        public void removeTextChangeListener(TextChangeListener listener) {
        }

        @Override
        public void replaceTextRange(int start, int replaceLength, String text) {
        }

        @Override
        public void setText(String text) {
        }

    }

    @Test
    public void testAdjustInsertBefore() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 5;
        event.newCharCount = 1;
        event.replaceCharCount = 0;

        adjust(marker, event);

        Assertions.assertEquals(11, marker.start);
        Assertions.assertEquals(21, marker.stop);
    }

    @Test
    public void testAdjustInsertAfter() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 25;
        event.newCharCount = 1;
        event.replaceCharCount = 0;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(20, marker.stop);
    }

    @Test
    public void testAdjustInsertMiddle() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 15;
        event.newCharCount = 1;
        event.replaceCharCount = 0;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(21, marker.stop);
    }

    @Test
    public void testAdjustRemoveBefore() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 5;
        event.newCharCount = 0;
        event.replaceCharCount = 1;

        adjust(marker, event);

        Assertions.assertEquals(9, marker.start);
        Assertions.assertEquals(19, marker.stop);
    }

    @Test
    public void testAdjustRemoveAfter() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 25;
        event.newCharCount = 0;
        event.replaceCharCount = 1;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(20, marker.stop);
    }

    @Test
    public void testAdjustRemoveMiddle() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 15;
        event.newCharCount = 0;
        event.replaceCharCount = 1;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(19, marker.stop);
    }

    @Test
    public void testAdjustReplaceBefore() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 5;
        event.newCharCount = 1;
        event.replaceCharCount = 3;

        adjust(marker, event);

        Assertions.assertEquals(8, marker.start);
        Assertions.assertEquals(18, marker.stop);
    }

    @Test
    public void testAdjustreplaceAfter() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 25;
        event.newCharCount = 1;
        event.replaceCharCount = 3;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(20, marker.stop);
    }

    @Test
    public void testAdjustReplaceMiddle() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 10;
        event.newCharCount = 1;
        event.replaceCharCount = 3;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(18, marker.stop);
    }

    @Test
    public void testAdjustInsertBeginning() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 10;
        event.newCharCount = 1;
        event.replaceCharCount = 0;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(21, marker.stop);
    }

    @Test
    public void testAdjustInsertEnding() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 20;
        event.newCharCount = 1;
        event.replaceCharCount = 0;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(21, marker.stop);
    }

    @Test
    public void testAdjustRemoveAll() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 10;
        event.newCharCount = 0;
        event.replaceCharCount = 10;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(10, marker.stop);
    }

    @Test
    public void testAdjustRemovePartialBefore() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 5;
        event.newCharCount = 0;
        event.replaceCharCount = 10;

        adjust(marker, event);

        Assertions.assertEquals(5, marker.start);
        Assertions.assertEquals(10, marker.stop);
    }

    @Test
    public void testAdjustRemovePartialAfter() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 15;
        event.newCharCount = 0;
        event.replaceCharCount = 10;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(15, marker.stop);
    }

    @Test
    public void testAdjustRemoveAllCross() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 5;
        event.newCharCount = 0;
        event.replaceCharCount = 20;

        adjust(marker, event);

        Assertions.assertEquals(10, marker.start);
        Assertions.assertEquals(10, marker.stop);
    }

    @Test
    public void testAdjustRemoveAndInsertPartialBefore() throws Exception {
        TokenMarker marker = new TokenMarker(10, 20, TokenId.KEYWORD);

        TextChangingEvent event = new TextChangingEvent(new StyledTextContentMock());
        event.start = 5;
        event.newCharCount = 3;
        event.replaceCharCount = 10;

        adjust(marker, event);

        Assertions.assertEquals(5, marker.start);
        Assertions.assertEquals(13, marker.stop);
    }

    void adjust(TokenMarker entry, TextChangingEvent event) {
        if (event.replaceCharCount != 0) {
            if (event.start + event.replaceCharCount <= entry.start) {
                entry.start -= event.replaceCharCount;
                entry.stop -= event.replaceCharCount;
            }
            else if (event.start >= entry.start && event.start <= entry.stop) {
                if (event.start + event.replaceCharCount > entry.stop) {
                    entry.stop -= entry.stop - event.start;
                }
                else {
                    entry.stop -= event.replaceCharCount;
                }
            }
            else if (event.start < entry.start) {
                if (event.start + event.replaceCharCount <= entry.stop) {
                    entry.stop -= (event.start + event.replaceCharCount) - entry.start;
                    entry.stop -= entry.start - event.start;
                    entry.start -= entry.start - event.start;
                }
                else {
                    entry.stop = entry.start;
                }
            }
        }
        if (event.newCharCount != 0) {
            if (event.start < entry.start) {
                entry.start += event.newCharCount;
                entry.stop += event.newCharCount;
            }
            else if (event.start >= entry.start && event.start <= entry.stop) {
                entry.stop += event.newCharCount;
            }
        }
    }
}
