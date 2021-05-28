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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.antlr.v4.runtime.Token;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.spin.Spin2TokenMarker.TokenId;

public class Spin2Editor {

    Composite container;
    LineNumbersRuler ruler;
    StyledText styledText;

    Font font;
    Font fontBold;
    Font fontItalic;
    Font fontBoldItalic;

    int currentLine;
    Color currentLineBackground;

    Caret insertCaret;
    Caret overwriteCaret;

    Spin2TokenMarker tokenMarker;
    Map<Spin2TokenMarker.TokenId, TextStyle> styleMap = new HashMap<Spin2TokenMarker.TokenId, TextStyle>();

    private final CaretListener caretListener = new CaretListener() {

        @Override
        public void caretMoved(CaretEvent event) {
            int offset = styledText.getCaretOffset();
            currentLine = styledText.getLineAtOffset(offset);
        }
    };

    final Runnable refreshMarkersRunnable = new Runnable() {

        @Override
        public void run() {
            if (styledText == null || styledText.isDisposed()) {
                return;
            }
            try {
                tokenMarker.refreshTokens(styledText.getText());
                styledText.redraw();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public Spin2Editor(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout containerLayout = new GridLayout(2, false);
        containerLayout.horizontalSpacing = 1;
        containerLayout.marginWidth = containerLayout.marginHeight = 0;
        container.setLayout(containerLayout);

        if ("win32".equals(SWT.getPlatform())) {
            font = new Font(Display.getDefault(), "Courier New", 9, SWT.NONE);
            fontBold = new Font(Display.getDefault(), "Courier New", 9, SWT.BOLD);
            fontItalic = new Font(Display.getDefault(), "Courier New", 9, SWT.ITALIC);
            fontBoldItalic = new Font(Display.getDefault(), "Courier New", 9, SWT.BOLD | SWT.ITALIC);
        }
        else {
            font = new Font(Display.getDefault(), "mono", 9, SWT.NONE);
            fontBold = new Font(Display.getDefault(), "mono", 9, SWT.BOLD);
            fontItalic = new Font(Display.getDefault(), "mono", 9, SWT.ITALIC);
            fontBoldItalic = new Font(Display.getDefault(), "mono", 9, SWT.BOLD | SWT.ITALIC);
        }

        currentLine = 0;
        currentLineBackground = new Color(Display.getDefault(), 0xE8, 0xF2, 0xFE);

        styleMap.put(TokenId.COMMENT, new TextStyle(
            font,
            new Color(Display.getDefault(), 0x7E, 0x7E, 0x7E),
            null));
        styleMap.put(TokenId.SECTION, new TextStyle(
            fontBold,
            new Color(Display.getDefault(), 0x00, 0x00, 0x00),
            null));

        styleMap.put(TokenId.NUMBER, new TextStyle(
            font,
            new Color(Display.getDefault(), 0x00, 0x66, 0x99),
            null));
        styleMap.put(TokenId.STRING, new TextStyle(
            font,
            new Color(Display.getDefault(), 0x7E, 0x00, 0x7E),
            null));

        styleMap.put(TokenId.PUB_METHOD, new TextStyle(
            fontBold,
            new Color(Display.getDefault(), 0x00, 0x00, 0x00),
            null));
        styleMap.put(TokenId.PRI_METHOD, new TextStyle(
            fontBoldItalic,
            new Color(Display.getDefault(), 0x00, 0x00, 0x00),
            null));

        TextStyle warningStyle = new TextStyle();
        warningStyle.underline = true;
        warningStyle.underlineColor = new Color(Display.getDefault(), 0xCC, 0x99, 0x00);
        warningStyle.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
        styleMap.put(TokenId.WARNING, warningStyle);

        TextStyle errorStyle = new TextStyle();
        errorStyle.underline = true;
        errorStyle.underlineColor = new Color(Display.getDefault(), 0xCC, 0x00, 0x00);
        errorStyle.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
        styleMap.put(TokenId.ERROR, errorStyle);

        ruler = new LineNumbersRuler(container);
        ruler.setFont(font);

        styledText = new StyledText(container, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        styledText.setMargins(5, 5, 5, 5);
        styledText.setTabs(8);
        styledText.setFont(font);

        ruler.setText(styledText);

        insertCaret = new Caret(styledText, SWT.NULL);
        insertCaret.setSize(2, styledText.getLineHeight());
        insertCaret.setFont(font);

        GC gc = new GC(styledText);
        Point charSize = gc.stringExtent("A"); //$NON-NLS-1$
        gc.dispose();

        overwriteCaret = new Caret(styledText, SWT.NULL);
        overwriteCaret.setSize(charSize.x, styledText.getLineHeight());
        overwriteCaret.setFont(styledText.getFont());

        tokenMarker = new Spin2TokenMarker();

        styledText.setCaret(insertCaret);
        styledText.addCaretListener(caretListener);
        styledText.setTabStops(new int[] {
            charSize.x * 4,
            charSize.x * 8,
            charSize.x * 12,
            charSize.x * 16,
            charSize.x * 20,
            charSize.x * 24,
            charSize.x * 28,
            charSize.x * 32,
            charSize.x * 36,
            charSize.x * 40,
        });

        styledText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                e.display.timerExec(250, refreshMarkersRunnable);
            }
        });

        styledText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.INSERT && e.stateMask == 0) {
                    styledText.setCaret(styledText.getCaret() == insertCaret ? overwriteCaret : insertCaret);
                }
            }
        });

        styledText.addLineStyleListener(new LineStyleListener() {

            @Override
            public void lineGetStyle(LineStyleEvent event) {
                List<StyleRange> ranges = new ArrayList<StyleRange>();

                try {
                    for (Entry<Token, TokenId> entry : tokenMarker.getLineTokens(event.lineOffset, event.lineOffset + event.lineText.length() - 1).entrySet()) {
                        TextStyle style = styleMap.get(entry.getValue());
                        if (style != null) {
                            StyleRange range = new StyleRange(style);
                            range.start = entry.getKey().getStartIndex();
                            range.length = entry.getKey().getStopIndex() - range.start + 1;
                            ranges.add(range);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                event.styles = ranges.toArray(new StyleRange[ranges.size()]);
            }
        });
        styledText.addLineBackgroundListener(new LineBackgroundListener() {

            @Override
            public void lineGetBackground(LineBackgroundEvent event) {
                if (styledText.getLineAtOffset(event.lineOffset) == currentLine) {
                    event.lineBackground = currentLineBackground;
                }
                else {
                    event.lineBackground = null;
                }
            }
        });

        styledText.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                font.dispose();
                fontBold.dispose();
                fontItalic.dispose();
                fontBoldItalic.dispose();
                currentLineBackground.dispose();
                for (TextStyle style : styleMap.values()) {
                    if (style.foreground != null) {
                        style.foreground.dispose();
                    }
                    if (style.background != null) {
                        style.background.dispose();
                    }
                }
                insertCaret.dispose();
                overwriteCaret.dispose();
            }
        });
    }

    public void dispose() {
        container.dispose();
    }

    public void setText(String text) {
        styledText.setRedraw(false);
        styledText.removeCaretListener(caretListener);
        try {
            text = text.replaceAll("[ \\t]+(\r\n|\n|\r)", "$1");

            currentLine = 0;
            styledText.setText(text);
        } finally {
            styledText.setRedraw(true);
            styledText.addCaretListener(caretListener);
        }
    }

    public String getText() {
        String text = styledText.getText();
        text = text.replaceAll("[ \\t]+(\r\n|\n|\r)", "$1");
        return text;
    }

    public static void main(String[] args) {
        final Display display = new Display();

        display.setErrorHandler(new Consumer<Error>() {

            @Override
            public void accept(Error t) {
                t.printStackTrace();
            }

        });
        display.setRuntimeExceptionHandler(new Consumer<RuntimeException>() {

            @Override
            public void accept(RuntimeException t) {
                t.printStackTrace();
            }

        });

        Realm.runWithDefault(DisplayRealm.getRealm(display), new Runnable() {

            @Override
            public void run() {
                try {
                    Shell shell = new Shell(display);
                    shell.setText("Spin2 Editor");

                    Rectangle screen = display.getClientArea();

                    Rectangle rect = new Rectangle(0, 0, 800, 800);
                    rect.x = (screen.width - rect.width) / 2;
                    rect.y = (screen.height - rect.height) / 2;
                    if (rect.y < 0) {
                        rect.height += rect.y * 2;
                        rect.y = 0;
                    }

                    shell.setLocation(rect.x, rect.y);
                    shell.setSize(rect.width, rect.height);

                    FillLayout layout = new FillLayout();
                    layout.marginWidth = layout.marginHeight = 5;
                    shell.setLayout(layout);

                    new Spin2Editor(shell);

                    shell.open();

                    while (display.getShells().length != 0) {
                        if (!display.readAndDispatch()) {
                            display.sleep();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        display.dispose();
    }
}
