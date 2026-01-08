/*
 * Copyright (c) 2021-2025 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.internal.ImageRegistry;
import com.maccasoft.propeller.internal.LinkImage;

public class AboutDialog extends Dialog {

    Color widgetBackground;
    Color listForeground;
    Color listBackground;

    public AboutDialog(Shell parentShell) {
        super(parentShell);
    }

    public void setTheme(String id) {
        widgetBackground = null;
        listForeground = null;
        listBackground = null;

        if (id == null) {
            widgetBackground = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
            listBackground = Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
        }
        else if ("dark".equals(id)) {
            widgetBackground = new Color(0x50, 0x55, 0x57);
            listForeground = new Color(0xA7, 0xA7, 0xA7);
            listBackground = new Color(0x2B, 0x2B, 0x2B);
        }
        else if ("light".equals(id)) {
            if ("win32".equals(SWT.getPlatform())) {
                widgetBackground = new Color(0xF0, 0xF0, 0xF0);
            }
            else {
                widgetBackground = new Color(0xFA, 0xFA, 0xFA);
            }
            listForeground = new Color(0x00, 0x00, 0x00);
            listBackground = new Color(0xFE, 0xFE, 0xFE);
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("About " + SpinTools.APP_TITLE);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        parent.setBackground(widgetBackground);
        parent.setBackgroundMode(SWT.INHERIT_DEFAULT);
        return super.createButtonBar(parent);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        content.setLayout(layout);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        content.setBackground(listBackground);
        content.setBackgroundMode(SWT.INHERIT_FORCE);

        applyDialogFont(content);

        Label label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
        label.setImage(ImageRegistry.getImageFromResources("about.png"));

        String title = SpinTools.APP_TITLE + " " + SpinTools.APP_VERSION;
        final String epl = "Eclipse Public License v1.0";
        final String eplLink = "http://www.eclipse.org/legal/epl-v10.html";
        final String fugue = "Yusuke Kamiyamane";
        final String fugueLink = "https://p.yusukekamiyamane.com/";
        final String cc = "Creative Commons\r\nAttribution 3.0";
        final String ccLink = "https://creativecommons.org/licenses/by/3.0/";
        final String our = "Marco Maccaferri";
        final String ourLink = "https://www.maccasoft.com/";
        final String forum = "Parallax Forums";
        final String forumLink = "https://forums.parallax.com/discussion/174436/spin-tools-ide";
        final String message = title + "\r\n" + "Copyright (c) 2021-25 " + our + " and others. All rights reserved.\r\n"
            + "\r\n"
            + "Spin2 Interpreter: v52 - 2025.10.08\r\n"
            + "\r\n"
            + "This program and the accompanying materials, unless otherwise specified,\r\n"
            + "are made available under the terms of the " + epl + ".\r\n"
            + "\r\nIcons by " + fugue + " are licensed under the " + cc + " license."
            + "\r\n"
            + "\r\nSupport for this program is provided through the " + forum + ".";

        final StyledText text = new StyledText(content, SWT.READ_ONLY);
        text.setLayoutData(new GridData(SWT.TOP, SWT.RIGHT, true, false));
        text.setCaret(null);
        text.setMargins(0, layout.verticalSpacing, convertHorizontalDLUsToPixels(7), layout.verticalSpacing);
        text.setText(message);
        text.setForeground(listForeground);

        final List<StyleRange> linkRanges = new ArrayList<StyleRange>();

        StyleRange style = new StyleRange();
        style.start = 0;
        style.length = title.length();
        style.fontStyle = SWT.BOLD;
        style.rise = 4;
        text.setStyleRange(style);

        style = new StyleRange();
        style.start = message.indexOf(epl);
        style.length = epl.length();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        text.setStyleRange(style);
        linkRanges.add(style);

        style = new StyleRange();
        style.start = message.indexOf(fugue);
        style.length = fugue.length();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        text.setStyleRange(style);
        linkRanges.add(style);

        style = new StyleRange();
        style.start = message.indexOf(cc);
        style.length = cc.length();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        text.setStyleRange(style);
        linkRanges.add(style);

        style = new StyleRange();
        style.start = message.indexOf(our);
        style.length = our.length();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        text.setStyleRange(style);
        linkRanges.add(style);

        style = new StyleRange();
        style.start = message.indexOf(forum);
        style.length = forum.length();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        text.setStyleRange(style);
        linkRanges.add(style);

        text.addListener(SWT.MouseDown, new Listener() {

            @Override
            public void handleEvent(Event event) {
                try {
                    int offset = text.getOffsetAtPoint(new Point(event.x, event.y));
                    for (StyleRange style : linkRanges) {
                        if (offset >= style.start && offset < (style.start + style.length)) {
                            String link = text.getText(style.start, style.start + style.length - 1);
                            if (link.equals(our)) {
                                link = ourLink;
                            }
                            else if (link.equals(epl)) {
                                link = eplLink;
                            }
                            else if (link.equals(fugue)) {
                                link = fugueLink;
                            }
                            else if (link.equals(cc)) {
                                link = ccLink;
                            }
                            else if (link.equals(forum)) {
                                link = forumLink;
                            }
                            Program.launch(link);
                            break;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // no character under event.x, event.y
                }
            }
        });

        return content;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Label label;

        ((GridData) parent.getLayoutData()).horizontalAlignment = SWT.FILL;

        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = layout.marginWidth = 0;
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        content.setLayout(layout);
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ((GridLayout) parent.getLayout()).numColumns++;

        if (Display.getDefault().getDismissalAlignment() == SWT.RIGHT) {
            createKoFiLabel(content);

            label = new Label(content, SWT.NONE);
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        }

        createButton(content, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);

        if (Display.getDefault().getDismissalAlignment() != SWT.RIGHT) {
            label = new Label(content, SWT.NONE);
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            createKoFiLabel(content);
        }

    }

    void createKoFiLabel(Composite parent) {
        LinkImage link = new LinkImage(parent);
        link.setImage(ImageRegistry.getImageFromResources("support_me_on_kofi.svg"));
        link.setUrl("https://ko-fi.com/maccasoft");
    }

}
