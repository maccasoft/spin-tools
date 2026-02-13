/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.io.File;

import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.widgets.Composite;
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

import com.maccasoft.propeller.SourceEditor.NavigationTarget;
import com.maccasoft.propeller.SourceTokenMarker.TokenId;
import com.maccasoft.propeller.SourceTokenMarker.TokenMarker;
import com.maccasoft.propeller.model.Node;
import com.maccasoft.propeller.model.RootNode;
import com.maccasoft.propeller.model.SourceProvider;
import com.maccasoft.propeller.spin1.Spin1Parser;
import com.maccasoft.propeller.spin1.Spin1TokenMarker;
import com.maccasoft.propeller.spin2.Spin2Parser;
import com.maccasoft.propeller.spin2.Spin2TokenMarker;

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
    public void testGetObjectFilterText() throws Exception {
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

    static class SourceEditorMock extends SourceEditor {

        public SourceEditorMock(Composite parent) {
            super(parent);
            setTokenMarker(new SourceTokenMarker(null) {

                @Override
                public void refreshTokens(String text) {

                }

                @Override
                public Node getSectionAtLine(int lineIndex) {
                    return new Node();
                }

            });
        }

        @Override
        int[] getBlockTabStops(Node node) {
            return new int[] {
                4, 8, 12, 16, 20
            };
        }

    }

    @Test
    public void testTabAlignEmptySpace() throws Exception {
        String text = ""
            + "DAT\n"
            + "            nop             ' comment\n"
            + "";

        SourceEditor subject = new SourceEditorMock(shell);
        subject.styledText.setText(text);
        subject.styledText.setCaretOffset(4);
        subject.styledText.setCaret(subject.alignCaret);

        subject.doTab();
        Assertions.assertEquals(""
            + "DAT\n"
            + "                nop         ' comment\n"
            + "", subject.styledText.getText());
        Assertions.assertEquals(4 + 4, subject.styledText.getCaretOffset());

        subject.doTab();
        Assertions.assertEquals(""
            + "DAT\n"
            + "                    nop     ' comment\n"
            + "", subject.styledText.getText());
        Assertions.assertEquals(4 + 8, subject.styledText.getCaretOffset());
    }

    @Test
    public void testTabAlignWord() throws Exception {
        String text = ""
            + "DAT\n"
            + "            nop             ' comment\n"
            + "";
        String expected = ""
            + "DAT\n"
            + "                nop         ' comment\n"
            + "";

        SourceEditor subject = new SourceEditorMock(shell);
        subject.styledText.setText(text);
        subject.styledText.setCaretOffset(16);
        subject.styledText.setCaret(subject.alignCaret);

        subject.doTab();
        Assertions.assertEquals(expected, subject.styledText.getText());
        Assertions.assertEquals(16 + 4, subject.styledText.getCaretOffset());
    }

    @Test
    public void testBacktabAlignEmptySpace() throws Exception {
        String text = ""
            + "DAT\n"
            + "            nop             ' comment\n"
            + "";

        SourceEditor subject = new SourceEditorMock(shell);
        subject.styledText.setText(text);
        subject.styledText.setCaretOffset(4 + 8);
        subject.styledText.setCaret(subject.alignCaret);

        subject.doBacktab();
        Assertions.assertEquals(""
            + "DAT\n"
            + "        nop                 ' comment\n"
            + "", subject.styledText.getText());
        Assertions.assertEquals(4 + 4, subject.styledText.getCaretOffset());

        subject.doBacktab();
        Assertions.assertEquals(""
            + "DAT\n"
            + "    nop                     ' comment\n"
            + "", subject.styledText.getText());
        Assertions.assertEquals(4, subject.styledText.getCaretOffset());
    }

    @Test
    public void testBacktabAlignWord() throws Exception {
        String text = ""
            + "DAT\n"
            + "            nop             ' comment\n"
            + "";
        String expected = ""
            + "DAT\n"
            + "        nop                 ' comment\n"
            + "";

        SourceEditor subject = new SourceEditorMock(shell);
        subject.styledText.setText(text);
        subject.styledText.setCaretOffset(16);
        subject.styledText.setCaret(subject.alignCaret);

        subject.doBacktab();
        Assertions.assertEquals(expected, subject.styledText.getText());
        Assertions.assertEquals(16 - 4, subject.styledText.getCaretOffset());
    }

    @Test
    public void testTabSelectionEmptyLine() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "    if a > 0\n"
            + "        a--\n"
            + "\n"
            + "    repeat\n"
            + "\n"
            + "";
        String expected = ""
            + "PUB main()\n"
            + "\n"
            + "        if a > 0\n"
            + "            a--\n"
            + "\n"
            + "    repeat\n"
            + "\n"
            + "";

        SourceEditor subject = new SourceEditorMock(shell);
        subject.styledText.setText(text);
        subject.styledText.setSelection(text.indexOf("\n") + 1, text.indexOf("a--") + 3);

        subject.doTab();

        Assertions.assertEquals(expected, subject.styledText.getText());
    }

    @Test
    public void testBacktabSelectionEmptyLine() throws Exception {
        String text = ""
            + "PUB main()\n"
            + "\n"
            + "        if a > 0\n"
            + "            a--\n"
            + "\n"
            + "    repeat\n"
            + "\n"
            + "";
        String expected = ""
            + "PUB main()\n"
            + "\n"
            + "    if a > 0\n"
            + "        a--\n"
            + "\n"
            + "    repeat\n"
            + "\n"
            + "";

        SourceEditor subject = new SourceEditorMock(shell);
        subject.styledText.setText(text);
        subject.styledText.setSelection(text.indexOf("\n") + 1, text.indexOf("a--") + 3);

        subject.doBacktab();

        Assertions.assertEquals(expected, subject.styledText.getText());
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

    @Test
    public void testGetGlobalVariableNavigationTarget() throws Exception {
        String text = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "VAR long a, b, c\n"
            + "    word d, e\n"
            + "\n"
            + "PUB main()\n"
            + "    a := VALUE1\n"
            + "    d := VALUE2\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin2TokenMarker(null));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset1 = text.indexOf("a := ");
        NavigationTarget target1 = subject.getNavigationTarget(offset1);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(3, target1.line);
        Assertions.assertEquals(9, target1.column);
        Assertions.assertEquals(offset1, target1.start);

        int offset2 = text.indexOf("d := ");
        NavigationTarget target2 = subject.getNavigationTarget(offset2);
        Assertions.assertNotNull(target2);
        Assertions.assertEquals(4, target2.line);
        Assertions.assertEquals(9, target2.column);
        Assertions.assertEquals(offset2, target2.start);
    }

    @Test
    public void testGetConstantNavigationTarget() throws Exception {
        String text = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "VAR long a, b, c\n"
            + "    word d, e\n"
            + "\n"
            + "PUB main()\n"
            + "    a := VALUE1\n"
            + "    d := VALUE2\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin2TokenMarker(null));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset1 = text.indexOf(":= VALUE1") + 3;
        NavigationTarget target1 = subject.getNavigationTarget(offset1);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(0, target1.line);
        Assertions.assertEquals(5, target1.column);
        Assertions.assertEquals(offset1, target1.start);

        int offset2 = text.indexOf(":= VALUE2") + 3;
        NavigationTarget target2 = subject.getNavigationTarget(offset2);
        Assertions.assertNotNull(target2);
        Assertions.assertEquals(1, target2.line);
        Assertions.assertEquals(5, target2.column);
        Assertions.assertEquals(offset2, target2.start);
    }

    @Test
    public void testGetSpin2ChildObjectConstantNavigationTarget() throws Exception {
        String text = ""
            + "VAR long a, b, c\n"
            + "    word d, e\n"
            + "\n"
            + "OBJ\n"
            + "    child : \"child\"\n"
            + "\n"
            + "PUB main()\n"
            + "    a := child.VALUE1\n"
            + "    d := child.VALUE2\n"
            + "";

        String childText = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin2TokenMarker(new SourceProvider() {

            @Override
            public File getFile(String name) {
                return new File(name);
            }

            @Override
            public RootNode getParsedSource(File file) {
                Spin2Parser parser = new Spin2Parser(childText);
                return parser.parse();
            }

        }));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset1 = text.indexOf(".VALUE1") + 1;
        NavigationTarget target1 = subject.getNavigationTarget(offset1 + 2);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(0, target1.line);
        Assertions.assertEquals(5, target1.column);
        Assertions.assertSame(subject.tokenMarker.getRoot().getChild(1).getChild(0), target1.object);
        Assertions.assertEquals(offset1, target1.start);

        int offset2 = text.indexOf(".VALUE2") + 1;
        NavigationTarget target2 = subject.getNavigationTarget(offset2 + 2);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(1, target2.line);
        Assertions.assertEquals(5, target2.column);
        Assertions.assertSame(subject.tokenMarker.getRoot().getChild(1).getChild(0), target1.object);
        Assertions.assertEquals(offset2, target2.start);
    }

    @Test
    public void testGetSpin1ChildObjectConstantNavigationTarget() throws Exception {
        String text = ""
            + "VAR long a, b, c\n"
            + "    word d, e\n"
            + "\n"
            + "OBJ\n"
            + "    child : \"child\"\n"
            + "\n"
            + "PUB main()\n"
            + "    a := child#VALUE1\n"
            + "    d := child#VALUE2\n"
            + "";

        String childText = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin1TokenMarker(new SourceProvider() {

            @Override
            public File getFile(String name) {
                return new File(name);
            }

            @Override
            public RootNode getParsedSource(File file) {
                Spin1Parser parser = new Spin1Parser(childText);
                return parser.parse();
            }

        }));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset1 = text.indexOf("#VALUE1") + 1;
        NavigationTarget target1 = subject.getNavigationTarget(offset1 + 2);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(0, target1.line);
        Assertions.assertEquals(5, target1.column);
        Assertions.assertSame(subject.tokenMarker.getRoot().getChild(1).getChild(0), target1.object);
        Assertions.assertEquals(offset1, target1.start);

        int offset2 = text.indexOf("#VALUE2") + 1;
        NavigationTarget target2 = subject.getNavigationTarget(offset2 + 2);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(1, target2.line);
        Assertions.assertEquals(5, target2.column);
        Assertions.assertSame(subject.tokenMarker.getRoot().getChild(1).getChild(0), target1.object);
        Assertions.assertEquals(offset2, target2.start);
    }

    @Test
    public void testGetDatLabelNavigationTarget() throws Exception {
        String text = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "DAT     org $000\n"
            + "\n"
            + "        mov     a, #VALUE1\n"
            + "\n"
            + "a       res     1\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin2TokenMarker(null));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset1 = text.indexOf("a, ");
        NavigationTarget target1 = subject.getNavigationTarget(offset1);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(7, target1.line);
        Assertions.assertEquals(0, target1.column);
        Assertions.assertEquals(offset1, target1.start);

        int offset2 = text.indexOf("#VALUE1") + 1;
        NavigationTarget target2 = subject.getNavigationTarget(offset2 + 2);
        Assertions.assertNotNull(target2);
        Assertions.assertEquals(0, target2.line);
        Assertions.assertEquals(5, target2.column);
        Assertions.assertEquals(offset2, target2.start);
    }

    @Test
    public void testGetSpin2DatChildObjectConstantNavigationTarget() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    child : \"child\"\n"
            + "\n"
            + "DAT     org $000\n"
            + "\n"
            + "        mov     a, #child.VALUE1\n"
            + "\n"
            + "a       res     1\n"
            + "";

        String childText = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin2TokenMarker(new SourceProvider() {

            @Override
            public File getFile(String name) {
                return new File(name);
            }

            @Override
            public RootNode getParsedSource(File file) {
                Spin2Parser parser = new Spin2Parser(childText);
                return parser.parse();
            }

        }));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset = text.indexOf(".VALUE1") + 1;
        NavigationTarget target = subject.getNavigationTarget(offset + 2);
        Assertions.assertNotNull(target);
        Assertions.assertEquals(0, target.line);
        Assertions.assertEquals(5, target.column);
        Assertions.assertEquals(offset, target.start);
    }

    @Test
    public void testGetSpin1DatChildObjectConstantNavigationTarget() throws Exception {
        String text = ""
            + "OBJ\n"
            + "    child : \"child\"\n"
            + "\n"
            + "DAT     org $000\n"
            + "\n"
            + "        mov     a, #child#VALUE1\n"
            + "\n"
            + "a       res     1\n"
            + "";

        String childText = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin1TokenMarker(new SourceProvider() {

            @Override
            public File getFile(String name) {
                return new File(name);
            }

            @Override
            public RootNode getParsedSource(File file) {
                Spin1Parser parser = new Spin1Parser(childText);
                return parser.parse();
            }

        }));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset = text.indexOf("#VALUE1") + 1;
        NavigationTarget target = subject.getNavigationTarget(offset + 2);
        Assertions.assertNotNull(target);
        Assertions.assertEquals(0, target.line);
        Assertions.assertEquals(5, target.column);
        Assertions.assertEquals(offset, target.start);
    }

    @Test
    public void testGetSpin2DatLocalLabelNavigationTarget() throws Exception {
        String text = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "DAT     org $000\n"
            + "\n"
            + "label1\n"
            + "        mov     .a, #VALUE1\n"
            + "\n"
            + ".a      long    0\n"
            + "\n"
            + "label2\n"
            + "        mov     .a, #VALUE2\n"
            + "\n"
            + ".a      long    0\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin2TokenMarker(null));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset1 = text.indexOf(".a, #VALUE1");
        NavigationTarget target1 = subject.getNavigationTarget(offset1 + 1);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(8, target1.line);
        Assertions.assertEquals(0, target1.column);
        Assertions.assertEquals(offset1, target1.start);

        int offset2 = text.indexOf(".a, #VALUE2");
        NavigationTarget target2 = subject.getNavigationTarget(offset2 + 1);
        Assertions.assertNotNull(target2);
        Assertions.assertEquals(13, target2.line);
        Assertions.assertEquals(0, target2.column);
        Assertions.assertEquals(offset2, target2.start);
    }

    @Test
    public void testGetSpin1DatLocalLabelNavigationTarget() throws Exception {
        String text = ""
            + "CON  VALUE1 = 1\n"
            + "     VALUE2 = 2\n"
            + "\n"
            + "DAT     org $000\n"
            + "\n"
            + "label1\n"
            + "        mov     :a, #VALUE1\n"
            + "\n"
            + ":a      long    0\n"
            + "\n"
            + "label2\n"
            + "        mov     :a, #VALUE2\n"
            + "\n"
            + ":a      long    0\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin1TokenMarker(null));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset1 = text.indexOf(":a, #VALUE1");
        NavigationTarget target1 = subject.getNavigationTarget(offset1 + 1);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(8, target1.line);
        Assertions.assertEquals(0, target1.column);
        Assertions.assertEquals(offset1, target1.start);

        int offset2 = text.indexOf(":a, #VALUE2");
        NavigationTarget target2 = subject.getNavigationTarget(offset2 + 1);
        Assertions.assertNotNull(target2);
        Assertions.assertEquals(13, target2.line);
        Assertions.assertEquals(0, target2.column);
        Assertions.assertEquals(offset2, target2.start);
    }

    @Test
    public void testGetNamespaceLabelNavigationTarget() throws Exception {
        String text = ""
            + "DAT     org $000\n"
            + "        namesp  proga\n"
            + "        mov     label, #VALUE1\n"
            + "\n"
            + "label   long    0\n"
            + "\n"
            + "        namesp  progb\n"
            + "        mov     label, #VALUE2\n"
            + "\n"
            + ".a      long    0\n"
            + "";

        SourceEditor subject = new SourceEditor(shell);
        subject.setTokenMarker(new Spin2TokenMarker(null));

        subject.styledText.setText(text);
        subject.tokenMarker.refreshTokens(subject.styledText.getText());

        int offset1 = text.indexOf("label, #VALUE1");
        NavigationTarget target1 = subject.getNavigationTarget(offset1 + 1);
        Assertions.assertNotNull(target1);
        Assertions.assertEquals(4, target1.line);
        Assertions.assertEquals(0, target1.column);
        Assertions.assertEquals(offset1, target1.start);

        int offset2 = text.indexOf("label, #VALUE2");
        NavigationTarget target2 = subject.getNavigationTarget(offset2 + 1);
        Assertions.assertNull(target2);
    }

}
