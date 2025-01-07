/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.maccasoft.propeller.Preferences;
import com.maccasoft.propeller.internal.CircularBuffer;

public class DebugPAsmWindow {

    static final int COLS = 123;
    static final int ROWS = 77;

    static final int mCOGN = 0; // debugger message longs
    static final int mBRKCZ = 1;
    static final int mBRKC = 2;
    static final int mBRKZ = 3;
    static final int mCTH2 = 4;
    static final int mCTL2 = 5;
    static final int mSTK0 = 6;
    static final int mSTK1 = 7;
    static final int mSTK2 = 8;
    static final int mSTK3 = 9;
    static final int mSTK4 = 10;
    static final int mSTK5 = 11;
    static final int mSTK6 = 12;
    static final int mSTK7 = 13;
    static final int mIRET = 14;
    static final int mFPTR = 15;
    static final int mPTRA = 16;
    static final int mPTRB = 17;
    static final int mFREQ = 18;
    static final int mCOND = 19;

    static final Color cRed = new Color(0xFF, 0x00, 0x00);
    static final Color cRed1 = new Color(0xBF, 0x00, 0x00);
    static final Color cRed2 = new Color(0x7F, 0x00, 0x00);
    static final Color cRed3 = new Color(0x3F, 0x00, 0x00);
    static final Color cRed4 = new Color(0x1F, 0x00, 0x00);
    static final Color cRed5 = new Color(0x0F, 0x00, 0x00);
    static final Color cGreen = new Color(0x00, 0xFF, 0x00);
    static final Color cGreen1 = new Color(0x00, 0xBF, 0x00);
    static final Color cGreen2 = new Color(0x00, 0x7F, 0x00);
    static final Color cGreen3 = new Color(0x00, 0x3F, 0x00);
    static final Color cGreen4 = new Color(0x00, 0x1F, 0x00);
    static final Color cGreen5 = new Color(0x00, 0x0F, 0x00);
    static final Color cBlue = new Color(0x00, 0x00, 0xFF);
    static final Color cBlue1 = new Color(0x00, 0x00, 0xBF);
    static final Color cBlue2 = new Color(0x00, 0x00, 0x7F);
    static final Color cBlue3 = new Color(0x00, 0x00, 0x3F);
    static final Color cBlue4 = new Color(0x00, 0x00, 0x1F);
    static final Color cBlue5 = new Color(0x00, 0x00, 0x0F);
    static final Color cYellow = new Color(0xFF, 0xFF, 0x00);
    static final Color cYellow1 = new Color(0xBF, 0xBF, 0x00);
    static final Color cYellow2 = new Color(0x7F, 0x7F, 0x00);
    static final Color cYellow3 = new Color(0x3F, 0x3F, 0x00);
    static final Color cYellow4 = new Color(0x1F, 0x1F, 0x00);
    static final Color cYellow5 = new Color(0x0F, 0x0F, 0x00);
    static final Color cMagenta = new Color(0xFF, 0x00, 0xFF);
    static final Color cMagenta1 = new Color(0xBF, 0x00, 0xBF);
    static final Color cMagenta2 = new Color(0x7F, 0x00, 0x7F);
    static final Color cMagenta3 = new Color(0x3F, 0x00, 0x3F);
    static final Color cMagenta4 = new Color(0x1F, 0x00, 0x1F);
    static final Color cMagenta5 = new Color(0x0F, 0x00, 0x0F);
    static final Color cCyan = new Color(0x00, 0xFF, 0xFF);
    static final Color cCyan1 = new Color(0x00, 0xBF, 0xBF);
    static final Color cCyan2 = new Color(0x00, 0x7F, 0x7F);
    static final Color cCyan3 = new Color(0x00, 0x3F, 0x3F);
    static final Color cCyan4 = new Color(0x00, 0x1F, 0x1F);
    static final Color cCyan5 = new Color(0x00, 0x0F, 0x0F);
    static final Color cOrange = new Color(0xFF, 0x7F, 0x00);
    static final Color cOrange1 = new Color(0xBF, 0x5F, 0x00);
    static final Color cOrange2 = new Color(0x7F, 0x3F, 0x00);
    static final Color cOrange3 = new Color(0x3F, 0x1F, 0x00);
    static final Color cOrange4 = new Color(0x1F, 0x0F, 0x00);
    static final Color cOrange5 = new Color(0x0F, 0x07, 0x00);
    static final Color cWhite = new Color(0xFF, 0xFF, 0xFF);
    static final Color cGray1 = new Color(0xBF, 0xBF, 0xBF);
    static final Color cGray2 = new Color(0x7F, 0x7F, 0x7F);
    static final Color cGray3 = new Color(0x3F, 0x3F, 0x3F);
    static final Color cGray4 = new Color(0x1F, 0x1F, 0x1F);
    static final Color cGray5 = new Color(0x0F, 0x0F, 0x0F);
    static final Color cBlack = new Color(0x00, 0x00, 0x00);

    static final int REGMAPl = 2;
    static final int REGMAPt = 1;
    static final int REGMAPw = 9;
    static final int REGMAPh = 75;
    static final int LUTMAPl = 13;
    static final int LUTMAPt = 1;
    static final int LUTMAPw = 9;
    static final int LUTMAPh = 75;
    static final int CFl = 24;
    static final int CFt = 1;
    static final int CFw = 3;
    static final int CFh = 2;
    static final int ZFl = 29;
    static final int ZFt = 1;
    static final int ZFw = 3;
    static final int ZFh = 2;
    static final int PCl = 34;
    static final int PCt = 1;
    static final int PCw = 8;
    static final int PCh = 2;
    static final int SKIPl = 44;
    static final int SKIPt = 1;
    static final int SKIPw = 41;
    static final int SKIPh = 2;
    static final int XBYTEl = 87;
    static final int XBYTEt = 1;
    static final int XBYTEw = 12;
    static final int XBYTEh = 2;
    static final int CTl = 101;
    static final int CTt = 1;
    static final int CTw = 20;
    static final int CTh = 2;
    static final int DISl = 24;
    static final int DISt = 4;
    static final int DISw = 56;
    static final int DISh = 32;
    static final int WATCHl = 82;
    static final int WATCHt = 4;
    static final int WATCHw = 12;
    static final int WATCHh = 32;
    static final int SFRl = 96;
    static final int SFRt = 4;
    static final int SFRw = 18;
    static final int SFRh = 32;
    static final int EVENTl = 116;
    static final int EVENTt = 4;
    static final int EVENTw = 5;
    static final int EVENTh = 32;
    static final int EXECl = 24;
    static final int EXECt = 35;
    static final int EXECw = 4;
    static final int EXECh = 4;
    static final int STACKl = 30;
    static final int STACKt = 37;
    static final int STACKw = 77;
    static final int STACKh = 2;
    static final int INTl = 24;
    static final int INTt = 40;
    static final int INTw = 13;
    static final int INTh = 6;
    static final int PTRl = 39;
    static final int PTRt = 40;
    static final int PTRw = 68;
    static final int PTRh = 6;
    static final int STATUSl = 24;
    static final int STATUSt = 47;
    static final int STATUSw = 6;
    static final int STATUSh = 6;
    static final int PINl = 32;
    static final int PINt = 47;
    static final int PINw = 75;
    static final int PINh = 6;
    static final int SMARTl = 24;
    static final int SMARTt = 54;
    static final int SMARTw = 97;
    static final int SMARTh = 2;
    static final int HUBl = 24;
    static final int HUBt = 57;
    static final int HUBw = 97;
    static final int HUBh = 16;
    static final int HINTl = 29;
    static final int HINTt = 74;
    static final int HINTw = 92;
    static final int HINTh = 2;

    static final int q1 = 1 << 7;
    static final int q2 = 2 << 7;
    static final int q3 = 3 << 7;

    static final int Bl = 109;
    static final int Bt = 37;
    static final int Bw = 12;
    static final int Bh = 16;
    static final int bBREAKl = Bl + 1;
    static final int bBREAKt = Bt + 1;
    static final int bBREAKw = 5;
    static final int bBREAKh = 2;
    static final int bADDRl = Bl + 1;
    static final int bADDRt = Bt + 2 + 1;
    static final int bADDRw = 5;
    static final int bADDRh = 2;
    static final int bINT3El = Bl + 1;
    static final int bINT3Et = Bt + 4 + 1;
    static final int bINT3Ew = 5;
    static final int bINT3Eh = 2;
    static final int bINT2El = Bl + 1;
    static final int bINT2Et = Bt + 6 + 1;
    static final int bINT2Ew = 5;
    static final int bINT2Eh = 2;
    static final int bINT1El = Bl + 1;
    static final int bINT1Et = Bt + 8 + 1;
    static final int bINT1Ew = 5;
    static final int bINT1Eh = 2;
    static final int bDEBUGl = Bl + 1;
    static final int bDEBUGt = Bt + 10 + 1;
    static final int bDEBUGw = 5;
    static final int bDEBUGh = 2;
    static final int bINITl = Bl + 7 + q1;
    static final int bINITt = Bt + 1;
    static final int bINITw = 4;
    static final int bINITh = 2;
    static final int bEVENTl = Bl + 7 + q1;
    static final int bEVENTt = Bt + 2 + 1;
    static final int bEVENTw = 4;
    static final int bEVENTh = 2;
    static final int bINT3l = Bl + 7 + q1;
    static final int bINT3t = Bt + 4 + 1;
    static final int bINT3w = 4;
    static final int bINT3h = 2;
    static final int bINT2l = Bl + 7 + q1;
    static final int bINT2t = Bt + 6 + 1;
    static final int bINT2w = 4;
    static final int bINT2h = 2;
    static final int bINT1l = Bl + 7 + q1;
    static final int bINT1t = Bt + 8 + 1;
    static final int bINT1w = 4;
    static final int bINT1h = 2;
    static final int bMAINl = Bl + 7 + q1;
    static final int bMAINt = Bt + 10 + 1;
    static final int bMAINw = 4;
    static final int bMAINh = 2;
    static final int bGOl = Bl + 1;
    static final int bGOt = Bt + 13 + q1;
    static final int bGOw = 10 + q1;
    static final int bGOh = 2;

    static final int TIMEOUT = 500;

    static final String[] DebugROM = {
        "setq    #$F     " + (char) (39) + "DEBUG Entry",
        "wrlong  0,#$FFF80-cog<<7",
        "setq    #$F",
        "rdlong  0,#$FFFC0-cog<<7",
        "jmp     #\0",
        "setq    #$F     " + (char) (39) + "DEBUG Exit",
        "rdlong  0,#$FFF80-cog<<7",
        "reti0"
    };

    static final String[] ModeName = {
        "MAIN", "INT1", "INT2", "INT3"
    };

    static final String[] EventName = {
        "INT", "CT1", "CT2", "CT3", "SE1", "SE2", "SE3", "SE4",
        "PAT", "FBW", "XMT", "XFI", "XRO", "XRL", "ATN", "QMT"
    };

    static final String[] RegName = {
        "IJMP3", "IRET3", "IJMP2", "IRET2", "IJMP1", "IRET1",
        "   PA", "   PB", " PTRA", " PTRB",
        " DIRA", " DIRB", " OUTA", " OUTB", "  INA", "  INB"
    };

    static final int dmPC = 0;
    static final int dmCOG = 1;
    static final int dmHUB = 2;

    static final int DIS_LINES = 16;
    static final int DIS_LINE_IDEAL = 3;
    static final int DIS_SCROLL_THRESHOLD = 8;

    static final int STALL_CMD = 0x00000800;

    static final int HIT_DECAY_RATE = 2;

    static final int PTR_BYTES = 14;
    static final int PTR_CENTER = 6;

    static final int COG_SIZE = 0x400;
    static final int COG_BLOCK_SIZE = 0x10;
    static final int COG_BLOCKS = COG_SIZE / COG_BLOCK_SIZE;

    static final int HUB_SIZE = 0x7C000;
    static final int HUB_BLOCK_SIZE = 0x1000;
    static final int HUB_BLOCKS = HUB_SIZE / HUB_BLOCK_SIZE;
    static final int HUB_SUB_BLOCK_SIZE = 0x80;
    static final int HUB_SUB_BLOCKS = HUB_SIZE / HUB_SUB_BLOCK_SIZE;
    static final int HUB_BLOCK_RATIO = HUB_BLOCK_SIZE / HUB_SUB_BLOCK_SIZE;

    static final int HUB_MAP_WIDTH = 64;
    static final int HUB_MAP_HEIGHT = HUB_SUB_BLOCKS / HUB_MAP_WIDTH;

    static final int REG_WATCH_SIZE = 0x1F0;
    static final int REG_WATCH_LIST_SIZE = 16;

    static final int SMART_PINS = 64;
    static final int SMART_WATCH_SIZE = SMART_PINS;
    static final int SMART_WATCH_LIST_SIZE = 7;

    Display display;
    Shell shell;

    Canvas canvas;

    Font font;
    Image image;
    Image baseImage;
    Image regMapImage;
    Image lutMapImage;
    Image hubMapImage;

    GC imageGc;

    int COGN, BRKCZ, BRKC, BRKZ, CTH2, CTL2;
    int IRET, FPTR, PTRA, PTRB, FREQ, COND;
    int[] STK = new int[8];

    Color cBackground = cBlack;
    Color cBox = cYellow4;
    Color cBox2 = cGreen4;
    Color cBox3 = cOrange2;
    Color cData = cWhite;
    Color cData2 = cGreen2;
    Color cDataDim = cYellow5;
    Color cIndicator = cOrange;
    Color cName = cYellow;
    Color cHighSame = cYellow3;
    Color cLowSame = cYellow5;
    Color cHighDiff = cYellow;
    Color cLowDiff = cYellow2;
    Color cModeButton = cYellow2;
    Color cModeText = cWhite;
    Color cModeButtonDim = cYellow3;
    Color cModeTextDim = cYellow5;
    Color cCmdButton = cOrange1;
    Color cCmdText = cWhite;
    Color cCmdButtonDim = cOrange3;
    Color cCmdTextDim = cOrange4;

    int textSize;
    int charWidth;
    int charHeight;
    Point imageSize;

    Rectangle RegBox;
    Rectangle RegMap;
    Rectangle LutBox;
    Rectangle LutMap;
    Rectangle CF;
    Rectangle ZF;
    Rectangle PC;
    Rectangle Skip;
    Rectangle XBYTE;
    Rectangle CT;
    Rectangle Dis;
    Rectangle RegWatch;
    Rectangle SFRBox;
    Rectangle SFRData;
    Rectangle EventsBox;
    Rectangle Events;
    Rectangle Exec;
    Rectangle StackBox;
    Rectangle StackData;
    Rectangle IntBox;
    Rectangle PtrBox;
    Rectangle PtrAddr;
    Rectangle PtrData;
    Rectangle PtrChr;
    Rectangle Status;
    Rectangle PinBox;
    Rectangle PinData;
    Rectangle SmartWatch;
    Rectangle HubTab;
    Rectangle HubBox;
    Rectangle HubAddr;
    Rectangle HubData;
    Rectangle HubChr;
    Rectangle HubMap;
    Rectangle ButtonBox;
    Rectangle ButtonBreak;
    Rectangle ButtonAddr;
    Rectangle ButtonInt3E;
    Rectangle ButtonInt2E;
    Rectangle ButtonInt1E;
    Rectangle ButtonDebug;
    Rectangle ButtonInit;
    Rectangle ButtonEvent;
    Rectangle ButtonInt3;
    Rectangle ButtonInt2;
    Rectangle ButtonInt1;
    Rectangle ButtonMain;
    Rectangle ButtonGo;

    int mouseX;
    int mouseY;

    boolean inRegBox;
    boolean inRegMap;
    boolean inLutBox;
    boolean inLutMap;
    boolean inCF;
    boolean inZF;
    boolean inPC;
    boolean inSkip;
    boolean inXBYTE;
    boolean inCT;
    boolean inDis;
    boolean inRegWatch;
    boolean inSFRBox;
    boolean inSFRData;
    boolean inEventsBox;
    boolean inEvents;
    boolean inExec;
    boolean inStackBox;
    boolean inStackData;
    boolean inIntBox;
    boolean inPtrBox;
    boolean inPtrAddr;
    boolean inPtrData;
    boolean inPtrChr;
    boolean inStatusBox;
    boolean inPinBox;
    boolean inPinData;
    boolean inSmartWatch;
    boolean inHubTab;
    boolean inHubBox;
    boolean inHubAddr;
    boolean inHubData;
    boolean inHubChr;
    boolean inHubMap;
    boolean inButtonBox;
    boolean inButtonBreak;
    boolean inButtonAddr;
    boolean inButtonInt3E;
    boolean inButtonInt2E;
    boolean inButtonInt1E;
    boolean inButtonDebug;
    boolean inButtonInit;
    boolean inButtonEvent;
    boolean inButtonInt3;
    boolean inButtonInt2;
    boolean inButtonInt1;
    boolean inButtonMain;
    boolean inButtonGo;

    int[] cogImage = new int[COG_SIZE];
    int[] cogImageOld = new int[COG_SIZE];
    //int[] cogImageHit = new int[COG_SIZE];

    int[] cogBlock = new int[COG_SIZE / COG_BLOCK_SIZE];
    int[] cogBlockOld = new int[COG_SIZE / COG_BLOCK_SIZE];

    int[] hubBlock = new int[HUB_SIZE / HUB_BLOCK_SIZE];
    int[] hubBlockOld = new int[HUB_SIZE / HUB_BLOCK_SIZE];
    int[] hubSubBlock = new int[HUB_SIZE / HUB_SUB_BLOCK_SIZE];
    int[] hubSubBlockOld = new int[HUB_SIZE / HUB_SUB_BLOCK_SIZE];

    int[] smartBuff = new int[SMART_PINS];
    int[] smartBuffOld = new int[SMART_PINS];

    int[] watchReg = new int[REG_WATCH_SIZE];
    int[] watchRegList = new int[REG_WATCH_LIST_SIZE];

    int[] watchSmart = new int[SMART_WATCH_SIZE];
    int[] watchSmartList = new int[SMART_WATCH_LIST_SIZE];
    boolean watchSmartAll;

    int breakAddr = 0x00000;
    int breakEvent = 1;
    int breakValue;
    int stallBrk = STALL_CMD;
    boolean repeatMode;
    boolean firstBreak = true;

    int oldPC;
    int disMode = dmPC;
    int curDisMode;
    int disAddr;
    int curDisAddr;
    int cogAddr;
    int hubAddr;
    int mapCogAddr;
    int mapHubAddr;
    int disScrollTimer;
    long oldTickCount;

    int requestCOGBRK;

    AtomicBoolean pendingPaint;

    public DebugPAsmWindow(int cogn) {
        this.COGN = cogn;

        Arrays.fill(cogBlock, -1);
        Arrays.fill(hubBlock, -1);
        Arrays.fill(hubSubBlock, -1);
        Arrays.fill(smartBuff, -1);

        resetRegWatch();
        resetSmartWatch();

        pendingPaint = new AtomicBoolean(false);
    }

    void resetRegWatch() {
        for (int i = 0; i < watchReg.length; i++) {
            watchReg[i] = 0xFFFF;
        }
        for (int i = 0; i < watchRegList.length; i++) {
            watchRegList[i] = 0xFFFF0000;
        }
    }

    void resetSmartWatch() {
        for (int i = 0; i < watchSmart.length; i++) {
            watchSmart[i] = 0xFFFF;
        }
        for (int i = 0; i < watchSmartList.length; i++) {
            watchSmartList[i] = 0xFFFF0000;
        }
    }

    public void open() {
        if (shell == null) {
            create();
        }

        shell.pack();
        shell.open();
    }

    public void create() {
        display = Display.getDefault();

        shell = new Shell(display, SWT.CLOSE | SWT.MIN | SWT.TITLE);
        shell.setData(this);
        shell.setText("Debugger - Cog" + COGN);

        FillLayout layout = new FillLayout();
        layout.marginWidth = layout.marginHeight = 0;
        shell.setLayout(layout);

        createContents(shell);
    }

    protected void createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);
        container.setBackgroundMode(SWT.INHERIT_DEFAULT);

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (Preferences.getInstance().getEditorFont() != null) {
            fontData = StringConverter.asFontData(Preferences.getInstance().getEditorFont());
        }
        fontData.setStyle(SWT.NONE);
        font = new Font(display, fontData.getName(), fontData.getHeight(), SWT.NONE);

        canvas = new Canvas(container, SWT.DOUBLE_BUFFERED);

        GC gc = new GC(canvas);
        try {
            gc.setFont(font);
            Point extent = gc.stringExtent("X");
            charWidth = extent.x;
            charHeight = extent.y;
        } finally {
            gc.dispose();
        }
        imageSize = new Point(COLS * charWidth, (ROWS * charHeight) >> 1);

        createBaseImage();

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                e.gc.setAdvanced(true);
                e.gc.setInterpolation(SWT.NONE);
                e.gc.drawImage(image, 0, 0);
                pendingPaint.set(false);
            }

        });

        canvas.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                image.dispose();
                imageGc.dispose();
                baseImage.dispose();
            }

        });

        canvas.addMouseMoveListener(new MouseMoveListener() {

            @Override
            public void mouseMove(MouseEvent e) {
                if (RegMap.contains(e.x, e.y)) {
                    mapCogAddr = within(((e.y - RegMap.y) << 9) / (RegMap.height) - 8, 0x000, 0x1F0);
                }
                else if (LutMap.contains(e.x, e.y)) {
                    mapCogAddr = within(((e.y - LutMap.y) << 9) / (LutMap.height) - 8, 0x000, 0x1F0) + 0x200;
                }
                else if (HubMap.contains(e.x, e.y)) {
                    mapHubAddr = ((e.y - HubMap.y) * HUB_MAP_HEIGHT) / HubMap.height * HUB_MAP_WIDTH * HUB_SUB_BLOCK_SIZE +
                        ((e.x - HubMap.x) * HUB_MAP_WIDTH) / HubMap.width * HUB_SUB_BLOCK_SIZE;
                }
                mouseX = e.x;
                mouseY = e.y;
            }

        });

        canvas.addMouseListener(new MouseAdapter() {
            int i, j;

            @Override
            public void mouseDown(MouseEvent e) {
                if (ButtonGo.contains(e.x, e.y)) {
                    if (repeatMode) {
                        stallBrk = STALL_CMD;
                        repeatMode = false;
                    }
                    else {
                        if (e.button == 1) {
                            stallBrk = breakValue;
                        }
                        else if (e.button == 3) {
                            oldTickCount = System.currentTimeMillis();
                            repeatMode = true;
                        }
                    }
                }
                else if (ButtonMain.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000001;
                    }
                    else if (e.button == 3) {
                        breakValue = (breakValue & 0xFFFFFFEF) ^ 0x00000001;
                    }
                }
                else if (ButtonInt1.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000002;
                    }
                    else if (e.button == 3) {
                        breakValue = (breakValue & 0xFFFFFFEF) ^ 0x00000002;
                    }
                }
                else if (ButtonInt2.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000004;
                    }
                    else if (e.button == 3) {
                        breakValue = (breakValue & 0xFFFFFFEF) ^ 0x00000004;
                    }
                }
                else if (ButtonInt3.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000008;
                    }
                    else if (e.button == 3) {
                        breakValue = (breakValue & 0xFFFFFFEF) ^ 0x00000008;
                    }
                }
                else if (ButtonDebug.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000010;
                    }
                    else if (e.button == 3) {
                        breakValue = (breakValue & 0x00000110) ^ 0x00000010;
                    }
                }
                else if (ButtonInt1E.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000020;
                    }
                    else if (e.button == 3) {
                        breakValue = (breakValue & 0xFFFFFFEF) ^ 0x00000020;
                    }
                }
                else if (ButtonInt2E.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000040;
                    }
                    else if (e.button == 3) {
                        breakValue = (breakValue & 0xFFFFFFEF) ^ 0x00000040;
                    }
                }
                else if (ButtonInt3E.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000080;
                    }
                    else if (e.button == 3) {
                        breakValue = (breakValue & 0xFFFFFFEF) ^ 0x00000080;
                    }
                }
                else if (ButtonInit.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue |= 0x00000100;
                    }
                    else if (e.button == 3) {
                        breakValue ^= 0x00000100;
                    }
                }
                else if (ButtonAddr.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        breakValue = (breakValue & 0x00000100) | 0x00000400 | (breakAddr << 12);
                    }
                    else if (e.button == 3) {
                        if ((breakValue & 0x00000400) != 0) {
                            breakValue = breakValue & 0x00000BEF;
                        }
                        else {
                            breakValue = (breakValue & 0x00000DEF) | 0x00000400 | (breakAddr << 12);
                        }
                    }
                }
                else if (ButtonBreak.contains(e.x, e.y)) {
                    breakValue = breakValue & 0x00000100;
                }
                else if (RegMap.contains(e.x, e.y) || LutMap.contains(e.x, e.y)) {
                    disMode = dmCOG;
                    cogAddr = mapCogAddr;
                }
                else if (PC.contains(e.x, e.y)) {
                    disMode = dmPC;
                }
                else if (Dis.contains(e.x, e.y)) {
                    if (e.button == 1) {
                        disMode = dmPC;
                    }
                    else if (e.button == 3) { // Set breakpoint
                        i = (mouseY - Dis.y) / charHeight;
                        if (!((curDisMode == dmHUB) && (curDisAddr + (i << 2) < 0x400))) {
                            if ((curDisMode == dmHUB) || (curDisAddr >= 0x400)) {
                                j = (curDisAddr + (i << 2)) & 0xFFFFF;
                            }
                            else {
                                j = (curDisAddr + i) & 0x3FF;
                            }
                            if (((breakValue & 0x00000400) != 0) && (breakAddr == j)) {
                                breakValue = breakValue & 0x00000BFF;
                            }
                            else {
                                breakAddr = j;
                                breakValue = (breakValue & 0x00000DFF) | 0x00000400 | (breakAddr << 12);
                            }
                        }
                    }
                }
                else if (RegWatch.contains(e.x, e.y)) {
                    resetRegWatch();
                }
                else if (SFRData.contains(e.x, e.y)) {
                    i = (mouseY - SFRData.y) / charHeight;
                    j = cogImage[0x1F0 + i] & 0xFFFFF;
                    if ((j < 0x400) && (i < 6)) { // treat IJMP3..IRET1 as code pointers
                        disMode = dmCOG;
                        cogAddr = j;
                    }
                    else { // treat PA..PTRB as hub data pointers
                        disMode = dmHUB;
                        hubAddr = j;
                    }
                }
                else if (StackData.contains(e.x, e.y)) {
                    i = (mouseX - StackData.x) / charWidth;
                    j = STK[i / 9] & 0xFFFFF;
                    if (j < 0x400) {
                        disMode = dmCOG;
                        cogAddr = j;
                    }
                    else {
                        disMode = dmHUB;
                        hubAddr = j;
                    }
                }
                else if (PtrAddr.contains(e.x, e.y)) {
                    disMode = dmHUB;
                    switch ((mouseY - PtrAddr.y) / charHeight) {
                        case 0:
                            hubAddr = FPTR & 0xFFFFF;
                            break;
                        case 1:
                            hubAddr = PTRA & 0xFFFFF;
                            break;
                        case 2:
                            hubAddr = PTRB & 0xFFFFF;
                            break;
                    }
                }
                else if (PtrData.contains(e.x, e.y)) {
                    i = (mouseX - PtrData.x) / charWidth;
                    switch ((mouseY - PtrAddr.y) / charHeight) {
                        case 0:
                            j = FPTR & 0xFFFFF;
                            break;
                        case 1:
                            j = PTRA & 0xFFFFF;
                            break;
                        case 2:
                            j = PTRB & 0xFFFFF;
                            break;
                    }
                    j = (j - PTR_CENTER) & 0xFFFFF;
                    disMode = dmHUB;
                    hubAddr = (j + (i / 3)) & 0xFFFFF;
                }
                else if (SmartWatch.contains(e.x, e.y)) {
                    resetSmartWatch();
                    if (e.button == 3) {
                        watchSmartAll = !watchSmartAll;
                    }
                }
                else if (HubData.contains(e.x, e.y)) {
                    i = ((mouseY - HubData.y) / charHeight) << 4 +
                        ((mouseX - HubData.x) / charWidth) / 3;
                    disMode = dmHUB;
                    hubAddr = (hubAddr + i) & 0xFFFFF;
                }
                else if (HubMap.contains(e.x, e.y)) {
                    hubAddr = mapHubAddr;
                }
            }

        });

        canvas.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseScrolled(MouseEvent e) {
                if (HubBox.contains(e.x, e.y)) {
                    if ((e.stateMask & SWT.SHIFT) != 0) {
                        hubAddr = (hubAddr - (0x1000 * (e.count / 3))) & 0xFFFFF;
                    }
                    else if ((e.stateMask & SWT.CTRL) != 0) {
                        hubAddr = (hubAddr - (HUB_SUB_BLOCK_SIZE * (e.count / 3))) & 0xFFFFF;
                    }
                    else {
                        hubAddr = (hubAddr - (0x10 * (e.count / 3))) & 0xFFFFF;
                    }
                }
            }

        });

        canvas.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                switch (Character.toUpperCase(e.character)) {
                    case SWT.SPACE:
                    case SWT.CR:
                        if (repeatMode) {
                            stallBrk = STALL_CMD;
                            repeatMode = false;
                        }
                        else {
                            if (e.character == SWT.SPACE) {
                                stallBrk = breakValue;
                            }
                            else if (e.character == SWT.CR) {
                                oldTickCount = System.currentTimeMillis();
                                repeatMode = true;
                            }
                        }
                        break;
                    case 'B':
                        breakValue = breakValue & 0x00000100;
                        break;
                    case 'I':
                        breakValue = (breakValue & 0x00000100) | 0x00000100;
                        break;
                    case 'D':
                        breakValue = (breakValue & 0x00000100) | 0x00000010;
                        break;
                    case 'M':
                        breakValue = (breakValue & 0x00000100) | 0x00000001;
                        break;
                    default:
                        switch (e.keyCode) {
                            case SWT.ARROW_UP:
                                hubAddr = (hubAddr - 0x10) & 0xFFFFF;
                                break;
                            case SWT.ARROW_DOWN:
                                hubAddr = (hubAddr + 0x10) & 0xFFFFF;
                                break;
                            case SWT.PAGE_UP:
                                if ((e.stateMask & SWT.SHIFT) != 0) {
                                    hubAddr = (hubAddr - 0x10000) & 0xFFFFF;
                                }
                                else if ((e.stateMask & SWT.CTRL) != 0) {
                                    hubAddr = (hubAddr - 0x1000) & 0xFFFFF;
                                }
                                else {
                                    hubAddr = (hubAddr - HUB_SUB_BLOCK_SIZE) & 0xFFFFF;
                                }
                                break;
                            case SWT.PAGE_DOWN:
                                if ((e.stateMask & SWT.SHIFT) != 0) {
                                    hubAddr = (hubAddr + 0x10000) & 0xFFFFF;
                                }
                                else if ((e.stateMask & SWT.CTRL) != 0) {
                                    hubAddr = (hubAddr + 0x1000) & 0xFFFFF;
                                }
                                else {
                                    hubAddr = (hubAddr + HUB_SUB_BLOCK_SIZE) & 0xFFFFF;
                                }
                                break;
                        }
                        break;
                }
            }

        });

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = imageSize.x;
        gridData.heightHint = imageSize.y;
        canvas.setLayoutData(gridData);
    }

    void createBaseImage() {
        baseImage = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));

        GC gc = new GC(baseImage);
        try {
            gc.setAdvanced(true);
            gc.setAntialias(SWT.ON);
            gc.setTextAntialias(SWT.ON);
            gc.setInterpolation(SWT.NONE);
            gc.setFont(font);

            // Clear working bitmap
            gc.setBackground(cBackground);
            gc.fillRectangle(0, 0, imageSize.x, imageSize.y);

            // Draw REG map box
            drawBox(gc, REGMAPl, REGMAPt, REGMAPw, REGMAPh, cBox);
            drawText(gc, REGMAPl + 3, REGMAPt, cName, "REG");
            // Draw LUT map box
            drawBox(gc, LUTMAPl, LUTMAPt, LUTMAPw, LUTMAPh, cBox);
            drawText(gc, LUTMAPl + 3, LUTMAPt, cName, "LUT");
            // Draw C flag box
            drawBox(gc, CFl, CFt, CFw, CFh, cBox);
            drawText(gc, CFl, CFt, cName, "C");
            // Draw Z flag box
            drawBox(gc, ZFl, ZFt, ZFw, ZFh, cBox);
            drawText(gc, ZFl, ZFt, cName, "Z");
            // Draw PC box
            drawBox(gc, PCl, PCt, PCw, PCh, cBox);
            drawText(gc, PCl, PCt, cName, "PC");
            // Draw SKIP/SKIPF box
            drawBox(gc, SKIPl, SKIPt, SKIPw, SKIPh, cBox);
            drawText(gc, SKIPl, SKIPt, cName, "SKIP");
            // Draw XBYTE box
            drawBox(gc, XBYTEl, XBYTEt, XBYTEw, XBYTEh, cBox);
            drawText(gc, XBYTEl, XBYTEt, cName, "XBYTE");
            drawText(gc, XBYTEl + 10, XBYTEt, cDataDim, "\u2713");
            // Draw CT box
            drawBox(gc, CTl, CTt, CTw, CTh, cBox3);
            drawText(gc, CTl, CTt, cName, "CT");
            // Draw execution tab and disassembly box
            drawBox(gc, EXECl, EXECt, EXECw, EXECh, cBox2);
            drawBox(gc, DISl, DISt, DISw, DISh, cBox2);
            // Draw register watch box
            drawBox(gc, WATCHl, WATCHt, WATCHw, WATCHh, cBox);
            // Draw special function registers box
            drawBox(gc, SFRl, SFRt, SFRw, SFRh, cBox);
            for (int i = 0; i < RegName.length; i++) {
                drawText(gc, SFRl, SFRt + (i << 1), cData2, String.format("%03X", 0x1F0 + i, 3));
                drawText(gc, SFRl + 4, SFRt + (i << 1), cName, RegName[i]);
            }
            // Draw events box
            drawBox(gc, EVENTl, EVENTt, EVENTw, EVENTh, cBox);
            for (int i = 0; i < EventName.length; i++) {
                drawText(gc, EVENTl, EVENTt + (i << 1), cName, EventName[i]);
            }
            // Draw STACK box
            drawBox(gc, STACKl, STACKt, STACKw, STACKh, cBox);
            drawText(gc, STACKl, STACKt, cName, "STACK");
            // Draw interrupts box
            drawBox(gc, INTl, INTt, INTw, INTh, cBox);
            drawText(gc, INTl, INTt + (0 << 1), cName, "INT1");
            drawText(gc, INTl, INTt + (1 << 1), cName, "INT2");
            drawText(gc, INTl, INTt + (2 << 1), cName, "INT3");
            // Draw RFxx/WFxx, PTRA, PTRB box
            drawBox(gc, PTRl, PTRt, PTRw, PTRh, cBox);
            drawText(gc, PTRl, PTRt + (0 << 1), cName, " Fxx");
            drawText(gc, PTRl, PTRt + (1 << 1), cName, "PTRA");
            drawText(gc, PTRl, PTRt + (2 << 1), cName, "PTRB");
            // Draw status box
            drawBox(gc, STATUSl, STATUSt, STATUSw, STATUSh, cBox);
            drawText(gc, STATUSl + 1, STATUSt - 1 + q3, cDataDim, "INIT");
            drawText(gc, STATUSl, STATUSt + 1 + q1, cDataDim, "STALLI");
            drawText(gc, STATUSl - 1 + q3, STATUSt + 2 + q3, cDataDim, "STR");
            drawText(gc, STATUSl + 3 + q1, STATUSt + 2 + q3, cDataDim, "MOD");
            drawText(gc, STATUSl + 1, STATUSt + 4 + q1, cDataDim, "LUTS");
            // Draw pins box
            drawBox(gc, PINl, PINt, PINw, PINh, cBox);
            drawText(gc, PINl, PINt + (0 << 1), cName, "DIR");
            drawText(gc, PINl, PINt + (1 << 1), cName, "OUT");
            drawText(gc, PINl, PINt + (2 << 1), cName, " IN");
            // Draw smart pin watch box
            drawBox(gc, SMARTl, SMARTt, SMARTw, SMARTh, cBox);
            // Draw hub tab and hub box
            drawBox(gc, HUBl, HUBt + HUBh - 1, 3, 4, cBox);
            drawBox(gc, HUBl, HUBt, HUBw, HUBh, cBox);
            drawText(gc, HUBl, HUBt + HUBh + 1, cName, "HUB");
            // Draw hint box
            drawBox(gc, HINTl, HINTt, HINTw, HINTh, cBox2);
            // Draw button box
            drawBox(gc, Bl, Bt, Bw, Bh, cBox);
            // Draw mode buttons
            drawBox(gc, bBREAKl, bBREAKt, bBREAKw, bBREAKh, cModeButtonDim);
            drawText(gc, bBREAKl, bBREAKt, cModeTextDim, "BREAK");
            drawBox(gc, bADDRl, bADDRt, bADDRw, bADDRh, cModeButtonDim);
            drawBox(gc, bINT3El, bINT3Et, bINT3Ew, bINT3Eh, cModeButtonDim);
            drawText(gc, bINT3El, bINT3Et, cModeTextDim, "\u2794INT3");
            drawBox(gc, bINT2El, bINT2Et, bINT2Ew, bINT2Eh, cModeButtonDim);
            drawText(gc, bINT2El, bINT2Et, cModeTextDim, "\u2794INT2");
            drawBox(gc, bINT1El, bINT1Et, bINT1Ew, bINT1Eh, cModeButtonDim);
            drawText(gc, bINT1El, bINT1Et, cModeTextDim, "\u2794INT1");
            drawBox(gc, bDEBUGl, bDEBUGt, bDEBUGw, bDEBUGh, cModeButtonDim);
            drawText(gc, bDEBUGl, bDEBUGt, cModeTextDim, "DEBUG");
            drawBox(gc, bINITl, bINITt, bINITw, bINITh, cModeButtonDim);
            drawText(gc, bINITl, bINITt, cModeTextDim, "INIT");
            drawBox(gc, bEVENTl, bEVENTt, bEVENTw, bEVENTh, cModeButtonDim);
            drawText(gc, bEVENTl + 3, bEVENTt, cModeTextDim, "\u2191");
            drawBox(gc, bINT3l, bINT3t, bINT3w, bINT3h, cModeButtonDim);
            drawText(gc, bINT3l, bINT3t, cModeTextDim, "INT3");
            drawBox(gc, bINT2l, bINT2t, bINT2w, bINT2h, cModeButtonDim);
            drawText(gc, bINT2l, bINT2t, cModeTextDim, "INT2");
            drawBox(gc, bINT1l, bINT1t, bINT1w, bINT1h, cModeButtonDim);
            drawText(gc, bINT1l, bINT1t, cModeTextDim, "INT1");
            drawBox(gc, bMAINl, bMAINt, bMAINw, bMAINh, cModeButtonDim);
            drawText(gc, bMAINl, bMAINt, cModeTextDim, "MAIN");
            // Draw GO button
            drawBox(gc, bGOl, bGOt, bGOw, bGOh, cOrange1);
        } finally {
            gc.dispose();
        }

        // Compute REG and LUT box and bitmap boundaries
        RegBox = boxBoundary(REGMAPl, REGMAPt, REGMAPw, REGMAPh, 1);
        RegMap = boxBoundary(REGMAPl + 1, REGMAPt + 3, 7, REGMAPh - 4, 1);
        LutBox = boxBoundary(LUTMAPl, LUTMAPt, REGMAPw, LUTMAPh, 1);
        LutMap = boxBoundary(LUTMAPl + 1, LUTMAPt + 3, 7, LUTMAPh - 4, 1);
        // Compute C/Z/PC/SKIP/XBYTE/CT box boundaries
        CF = boxBoundary(CFl, CFt, CFw, CFh, 1);
        ZF = boxBoundary(ZFl, ZFt, ZFw, ZFh, 1);
        PC = boxBoundary(PCl, PCt, PCw, PCh, 1);
        Skip = boxBoundary(SKIPl, SKIPt, SKIPw, SKIPh, 1);
        XBYTE = boxBoundary(XBYTEl, XBYTEt, XBYTEw, XBYTEh, 1);
        CT = boxBoundary(CTl, CTt, CTw, CTh, 1);
        // Compute disassembly line and tab box boundaries
        Dis = boxBoundary(DISl, DISt, DISw, DISh, 0);
        Exec = boxBoundary(EXECl, EXECt, EXECw, EXECh, 1);
        // Compute register watch box boundaries
        RegWatch = boxBoundary(WATCHl, WATCHt, WATCHw, WATCHh, 1);
        // Compute SFR box and data boundaries
        SFRBox = boxBoundary(SFRl, SFRt, SFRw, SFRh, 1);
        SFRData = boxBoundary(SFRl + 10, SFRt, 8, 10 << 1, 0);
        // Compute event box and name boundaries
        EventsBox = boxBoundary(EVENTl, EVENTt, EVENTw, EVENTh, 1);
        Events = boxBoundary(EVENTl, EVENTt + (1 << 1), 3, 15 << 1, 0);
        // Compute stack box and data boundaries
        StackBox = boxBoundary(STACKl, STACKt, STACKw, STACKh, 1);
        StackData = boxBoundary(STACKl + 6, STACKt, 8 * 9 - 1, STACKh, 0);
        // Compute INT box boundaries
        IntBox = boxBoundary(INTl, INTt, INTw, INTh, 1);
        // Compute pointer box, address, and data boundaries
        PtrBox = boxBoundary(PTRl, PTRt, PTRw, PTRh, 1);
        PtrAddr = boxBoundary(PTRl + 5, PTRt, 5, PTRh, 0);
        PtrData = boxBoundary(PTRl + 11, PTRt, PTR_BYTES * 3 - 1, PTRh, 0);
        PtrChr = boxBoundary(PTRl + 11 + PTR_BYTES * 3 + 1, PTRt, PTR_BYTES, PTRh, 0);
        // Compute status box boundaries
        Status = boxBoundary(STATUSl, STATUSt, STATUSw, STATUSh, 1);
        // Compute pin box and data boundaries
        PinBox = boxBoundary(PINl, PINt, PINw, PINh, 1);
        PinData = boxBoundary(PINl + 4, PINt, 8 * 9 - 1, PINh, 0);
        // Compute smart pin watch box boundaries
        SmartWatch = boxBoundary(SMARTl, SMARTt, SMARTw, SMARTh, 1);
        // Compute hub tab, box, address, data, character, and bitmap boundaries
        HubTab = boxBoundary(HUBl, HUBt + HUBh - 1, 3, 4, 1);
        HubBox = boxBoundary(HUBl, HUBt, HUBw, HUBh, 1);
        HubAddr = boxBoundary(HUBl, HUBt, 5, HUBh, 0);
        HubData = boxBoundary(HUBl + 6, HUBt, 16 * 3 - 1, HUBh, 0);
        HubChr = boxBoundary(HUBl + 6 + 16 * 3 + 1, HUBt, 16, HUBh, 0);
        HubMap = boxBoundary(HUBl + 74, HUBt + 1, 22, HUBh - 2, 1);
        // Compute button boundaries
        ButtonBox = boxBoundary(Bl, Bt, Bw, Bh, 1);
        ButtonBreak = boxBoundary(bBREAKl, bBREAKt, bBREAKw, bBREAKh, 1);
        ButtonAddr = boxBoundary(bADDRl, bADDRt, bADDRw, bADDRh, 1);
        ButtonInt3E = boxBoundary(bINT3El, bINT3Et, bINT3Ew, bINT3Eh, 1);
        ButtonInt2E = boxBoundary(bINT2El, bINT2Et, bINT2Ew, bINT2Eh, 1);
        ButtonInt1E = boxBoundary(bINT1El, bINT1Et, bINT1Ew, bINT1Eh, 1);
        ButtonDebug = boxBoundary(bDEBUGl, bDEBUGt, bDEBUGw, bDEBUGh, 1);
        ButtonInit = boxBoundary(bINITl, bINITt, bINITw, bINITh, 1);
        ButtonEvent = boxBoundary(bEVENTl, bEVENTt, bEVENTw, bEVENTh, 1);
        ButtonInt3 = boxBoundary(bINT3l, bINT3t, bINT3w, bINT3h, 1);
        ButtonInt2 = boxBoundary(bINT2l, bINT2t, bINT2w, bINT2h, 1);
        ButtonInt1 = boxBoundary(bINT1l, bINT1t, bINT1w, bINT1h, 1);
        ButtonMain = boxBoundary(bMAINl, bMAINt, bMAINw, bMAINh, 1);
        ButtonGo = boxBoundary(bGOl, bGOt, bGOw, bGOh, 1);

        image = new Image(display, new ImageData(imageSize.x, imageSize.y, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
        imageGc = new GC(image);

        regMapImage = new Image(display, new ImageData(32, 512, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
        lutMapImage = new Image(display, new ImageData(32, 512, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
        hubMapImage = new Image(display, new ImageData(HUB_MAP_WIDTH, HUB_MAP_HEIGHT, 24, new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)));
    }

    Rectangle boxBoundary(int l, int t, int w, int h, int b) {
        return new Rectangle(frac(l, charWidth) - charWidth / 2, frac(t, charHeight) >> 1, frac(w, charWidth) + charWidth, frac(h, charHeight) >> 1);
    }

    static int frac(int x, int y) {
        return ((((x & 0x7F) << 2) + (x >> 7)) * y) >> 2;
    }

    public void addDisposeListener(DisposeListener l) {
        shell.addDisposeListener(l);
    }

    public void removeDisposeListener(DisposeListener l) {
        shell.removeDisposeListener(l);
    }

    public void dispose() {
        shell.dispose();
    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }

    int curPC, difPC, execMode, cSame, cDiff, index;
    int curCogAddr, curHubAddr, callDepth;
    boolean PCInCog, getHubCode, skipOn, disCog, hiddenPC;

    int[] buffDis = new int[DIS_LINES];

    byte[] buffFptr = new byte[PTR_BYTES];
    byte[] buffPtra = new byte[PTR_BYTES];
    byte[] buffPtrb = new byte[PTR_BYTES];

    byte[] buffHub = new byte[HUB_SUB_BLOCK_SIZE];

    boolean regMapUpdate;
    boolean lutMapUpdate;
    boolean hubMapUpdate;

    public void breakPoint(CircularBuffer receiver, CircularBuffer transmitter) {
        int i, n, j;

        try {
            //  ------------------------------
            //   Receive initial data from P2
            //  ------------------------------

            BRKCZ = receiver.readLong();
            BRKC = receiver.readLong();
            BRKZ = receiver.readLong();
            CTH2 = receiver.readLong();
            CTL2 = receiver.readLong();
            for (i = 0; i < STK.length; i++) {
                STK[i] = receiver.readLong();
            }
            IRET = receiver.readLong();
            FPTR = receiver.readLong();
            PTRA = receiver.readLong();
            PTRB = receiver.readLong();
            FREQ = receiver.readLong();
            COND = receiver.readLong();

            for (i = 0; i < cogBlock.length; i++) {
                cogBlockOld[i] = cogBlock[i];
                cogBlock[i] = receiver.readWord();
            }

            for (i = 0; i < hubBlock.length; i++) {
                hubBlockOld[i] = hubBlock[i];
                hubBlock[i] = receiver.readWord();
            }

            //  ----------------------
            //   Process initial data
            //  ----------------------

            if (firstBreak) {
                breakValue = COND;
                firstBreak = false;
            }

            // Set defaults
            curDisMode = disMode;
            curCogAddr = cogAddr;
            curHubAddr = hubAddr;

            curPC = IRET & 0xFFFFF;
            difPC = Math.abs(((curPC - oldPC) << 12) / 4096); // get absolute value of 20-bit difference
            oldPC = curPC;
            PCInCog = curPC < 0x400;

            i = PCInCog ? 0 : 2;
            switch (curDisMode) {
                case dmPC:
                    if (difPC > (8 << i)) {
                        disAddr = curPC - (DIS_LINE_IDEAL << i);
                    }
                    else if (curPC < disAddr) {
                        disAddr = curPC;
                    }
                    else if (curPC > disAddr + ((DIS_LINES - 1) << i)) {
                        disAddr = curPC - ((DIS_LINES - 1) << i);
                    }
                    else if (curPC != disAddr + (DIS_LINE_IDEAL << i)) {
                        if (disScrollTimer < DIS_SCROLL_THRESHOLD) {
                            disScrollTimer++;
                        }
                        else {
                            disAddr += within(curPC - (disAddr + (DIS_LINE_IDEAL << i)), -1, 1) << i;
                        }
                    }
                    if (PCInCog) {
                        disAddr = within(disAddr, 0x000, 0x400 - DIS_LINES);
                    }
                    else {
                        disAddr = within(disAddr, 0x00000, 0x100000 - (DIS_LINES << 2));
                    }
                    curDisAddr = disAddr;
                    break;
                case dmCOG:
                    curDisAddr = curCogAddr;
                    break;
                case dmHUB:
                    curDisAddr = curHubAddr;
                    break;
            }

            // Is hub read needed for instructions?
            getHubCode = (curDisMode == dmPC) && !PCInCog;

            //  -----------------------------
            //   Send requests/command to P2
            //  -----------------------------

            // Send reg/lut block requests
            j = 0;
            for (i = 0; i < cogBlock.length; i++) {
                j >>= 1;
                if (cogBlock[i] != cogBlockOld[i]) {
                    j |= 0x80;
                    if (i < cogBlock.length / 2) {
                        regMapUpdate = true;
                    }
                    else {
                        lutMapUpdate = true;
                    }
                }
                if ((i & 7) == 7) {
                    transmitter.write(j);
                }
            }

            j = 0;
            for (i = 0; i < ((hubBlock.length + 31) & ~31); i++) {
                j >>= 1;
                if (i < hubBlock.length && hubBlock[i] != hubBlockOld[i]) {
                    j |= 0x80;
                    hubMapUpdate = true;
                }
                if ((i & 7) == 7) {
                    transmitter.write(j);
                }
            }

            // Send hub read requests
            if (getHubCode) {
                transmitter.writeLong(((DIS_LINES << 2) << 20) | curDisAddr); // DisLines
            }
            else {
                transmitter.writeLong(0x00000000);
            }
            transmitter.writeLong((buffFptr.length << 20) | ((FPTR - PTR_CENTER) & 0xFFFFF)); // FPTR
            transmitter.writeLong((buffPtra.length << 20) | ((PTRA - PTR_CENTER) & 0xFFFFF)); // PTRA
            transmitter.writeLong((buffPtrb.length << 20) | ((PTRB - PTR_CENTER) & 0xFFFFF)); // PTRB
            transmitter.writeLong((buffHub.length << 20) | curHubAddr); // CurHubAddr

            // Send COGBRK requests
            transmitter.writeLong(requestCOGBRK);
            requestCOGBRK = 0;

            // Reset disassembly-scroll timer?
            if (repeatMode || (stallBrk != STALL_CMD)) {
                disScrollTimer = 0;
            }
            // Send STALL/BRK command
            if (repeatMode) {
                long t = System.currentTimeMillis();
                if ((t - oldTickCount) < 50) {
                    transmitter.writeLong(STALL_CMD);
                }
                else {
                    transmitter.writeLong(breakValue);
                    oldTickCount = t;
                }
            }
            else {
                transmitter.writeLong(stallBrk);
                stallBrk = STALL_CMD;
            }

            //  ----------------------------
            //   Receive final data from P2
            //  ----------------------------

            // Receive reg/lut blocks
            j = 0;
            for (i = 0; i < cogBlock.length; i++) {
                if (cogBlockOld[i] != cogBlock[i]) {
                    for (n = 0; n < COG_BLOCK_SIZE; n++) {
                        cogImage[j + n] = receiver.readLong();
                    }
                }
                j += COG_BLOCK_SIZE;
            }

            // Receive detailed hub checksum words
            j = 0;
            for (i = 0; i < hubBlock.length; i++) {
                if (hubBlock[i] != hubBlockOld[i]) {
                    for (n = 0; n < HUB_BLOCK_RATIO; n++) {
                        hubSubBlock[j + n] = receiver.readWord();
                    }
                }
                j += HUB_BLOCK_RATIO;
            }

            // Receive hub reads
            if (getHubCode) {
                for (i = 0; i < buffDis.length; i++) {
                    buffDis[i] = receiver.readLong();
                }
            }
            for (i = 0; i < buffFptr.length; i++) {
                buffFptr[i] = (byte) receiver.read();
            }
            for (i = 0; i < buffPtra.length; i++) {
                buffPtra[i] = (byte) receiver.read();
            }
            for (i = 0; i < buffPtrb.length; i++) {
                buffPtrb[i] = (byte) receiver.read();
            }
            for (i = 0; i < buffHub.length; i++) {
                buffHub[i] = (byte) receiver.read();
            }

            // Receive smart pin data
            i = j = 0;
            for (i = 0; i < SMART_PINS; i++) {
                smartBuffOld[i] = smartBuff[i];
                if ((i & 7) == 0) {
                    j = receiver.read();
                }
                if (((j >> (i & 7)) & 1) != 0) {
                    smartBuff[i] = receiver.readLong();
                }
                else {
                    smartBuff[i] = 0;
                }
                if (smartBuffOld[i] == -1) {
                    smartBuffOld[i] = smartBuff[i];
                }
            }

            //  --------------------
            //   Process final data
            //  --------------------

            // Patch disassembly buffer if needed
            if (!getHubCode) {
                for (i = 0; i < DIS_LINES; i++) {
                    switch (curDisMode) {
                        case dmPC:
                        case dmCOG:
                            buffDis[i] = cogImage[curDisAddr + i];
                            break;
                        case dmHUB:
                            buffDis[i] = (buffHub[(i << 2)] & 0xFF)
                                | ((buffHub[(i << 2) + 1] & 0xFF) << 8)
                                | ((buffHub[(i << 2) + 2] & 0xFF) << 16)
                                | ((buffHub[(i << 2) + 3] & 0xFF) << 24);
                            break;
                    }
                }
            }

            // Determine execution mode
            execMode = 0;
            if (((BRKCZ >> 2) & 3) == 3) {
                execMode = 1;
            }
            else if (((BRKCZ >> 4) & 3) == 3) {
                execMode = 2;
            }
            else if (((BRKCZ >> 6) & 3) == 3) {
                execMode = 3;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    int within(int value, int min, int max) {
        if (value < min) {
            value = min;
        }
        else if (value > max) {
            value = max;
        }
        return value;
    }

    public void update() {
        if (canvas.isDisposed()) {
            return;
        }

        updateBitmap();

        if (!pendingPaint.getAndSet(true)) {
            display.asyncExec(new Runnable() {

                @Override
                public void run() {
                    if (canvas.isDisposed()) {
                        return;
                    }
                    canvas.redraw();
                }

            });
        }
    }

    void updateBitmap() {
        //Color i;

        try {
            imageGc.setAdvanced(true);
            imageGc.setAntialias(SWT.ON);
            imageGc.setTextAntialias(SWT.ON);
            imageGc.setInterpolation(SWT.NONE);
            imageGc.setFont(font);

            imageGc.drawImage(baseImage, 0, 0);

            // Draw C flag
            drawText(imageGc, CFl + 2, CFt, cData, "" + (char) ((IRET >> 31 & 1) + '0'));
            // Draw Z flag
            drawText(imageGc, ZFl + 2, ZFt, cData, "" + (char) ((IRET >> 30 & 1) + '0'));
            // Draw PC
            drawText(imageGc, PCl + 3, PCt, cData, String.format("%05X", IRET & 0xFFFFF));
            //// Draw SKIP/SKIPF
            if (((BRKC >> 27) & 1) == 0) {
                drawText(imageGc, SKIPl + 4, SKIPt, cName, "F");
            }
            int CallDepth = (BRKC >> 28) & 0xF;
            boolean SkipOn = (execMode == 0) && (CallDepth == 0);
            /*if (SkipOn) {
            i = cData;
            }
            else {
            i = cDataDim;
            }*/
            drawRegBin(imageGc, SKIPl + 6, SKIPt, BRKZ, SkipOn ? cData : cDataDim);
            /*if (!SkipOn) {
            String s;
            if ((ExecMode == 0) && (CallDepth != 0)) {
                s = "CALL(" + i + ")";
            }
            else {
                s = ModeName[ExecMode];
            }
            drawText(gc, SKIPl + 15 - (s.length() + 1) >> 1, SKIPt, cData, "Suspended during " + s);
            }*/
            // Draw XBYTE
            drawText(imageGc, XBYTEl + 6, XBYTEt, cData, String.format("%03X", (BRKC >> 16) & 0x1FF));
            if (((BRKC >> 25) & 1) != 0) {
                drawText(imageGc, XBYTEl + 10, XBYTEt, cIndicator, "\u2713");
            }
            // Draw CT
            drawText(imageGc, CTl + 3, CTt, cData, String.format("%08X %08X", CTH2, CTL2));
            // Draw special function registers
            for (int i = 0; i < 16; i++) {
                drawText(imageGc, SFRl + 10, SFRt + (i << 1), cData, String.format("%08X", cogImage[0x1F0 + i]));
            }
            // Draw events
            for (int i = 0; i < 16; i++) {
                drawText(imageGc, EVENTl + 4, EVENTt + (i << 1), cData, "" + (char) ('0' + ((BRKC >> i) & 1)));
            }
            // Draw execution mode
            drawText(imageGc, EXECl, EXECt + 2, cData2, ModeName[execMode]);
            // Draw STACK
            for (int i = 0; i < STK.length; i++) {
                drawText(imageGc, STACKl + 6 + i * 9, STACKt, cData, String.format("%08X", STK[i]));
            }
            // Draw interrupts
            drawInt(imageGc, INTl, INTt + (0 << 1), 1);
            drawInt(imageGc, INTl, INTt + (1 << 1), 2);
            drawInt(imageGc, INTl, INTt + (2 << 1), 3);
            // Draw RFxx/WFxx, PTRA, PTRB
            drawText(imageGc, PTRl, PTRt, cName, ((BRKCZ >> 20) & 1) != 0 ? "W" : "R");
            drawPtrBytes(imageGc, PTRl, PTRt + (0 << 1), FPTR, buffFptr);
            drawPtrBytes(imageGc, PTRl, PTRt + (1 << 1), cogImage[0x1F8], buffPtra);
            drawPtrBytes(imageGc, PTRl, PTRt + (2 << 1), cogImage[0x1F9], buffPtrb);
            // Draw INIT, STALLI, STR, MOD, LUTS
            if (((BRKCZ >> 23) & 1) != 0) {
                drawText(imageGc, STATUSl + 1, STATUSt - 1 + q3, cIndicator, "INIT");
            }
            if (((BRKCZ >> 1) & 1) != 0) {
                drawText(imageGc, STATUSl, STATUSt + 1 + q1, cIndicator, "STALLI");
            }
            if (((BRKCZ >> 21) & 1) != 0) {
                drawText(imageGc, STATUSl - 1 + q3, STATUSt + 2 + q3, cIndicator, "STR");
            }
            if (((BRKCZ >> 22) & 1) != 0) {
                drawText(imageGc, STATUSl + 3 + q1, STATUSt + 2 + q3, cIndicator, "MOD");
            }
            if (((BRKC >> 26) & 1) != 0) {
                drawText(imageGc, STATUSl + 1, STATUSt + 4 + q1, cIndicator, "LUTS");
            }
            // Draw pins in binary
            drawRegBin(imageGc, PINl + 4, PINt + (0 << 1), cogImage[0x1FB], cData);
            drawRegBin(imageGc, PINl + 40, PINt + (0 << 1), cogImage[0x1FA], cData);
            drawRegBin(imageGc, PINl + 4, PINt + (1 << 1), cogImage[0x1FD], cData);
            drawRegBin(imageGc, PINl + 40, PINt + (1 << 1), cogImage[0x1FC], cData);
            drawRegBin(imageGc, PINl + 4, PINt + (2 << 1), cogImage[0x1FF], cData);
            drawRegBin(imageGc, PINl + 40, PINt + (2 << 1), cogImage[0x1FE], cData);
            // Draw hub data
            for (int j = 0; j < 8; j++) {
                int i = (curHubAddr + (j << 4)) & 0xFFFFF;
                drawText(imageGc, HUBl, HUBt + (j << 1), cData, String.format("%05X", i));
                for (int k = 0; k < 16; k++) {
                    i = buffHub[(j << 4) + k] & 0xFF;
                    drawText(imageGc, HUBl + 6 + k * 3, HUBt + (j << 1), cData, String.format("%02X", i));
                    if ((i < 0x20) || (i > 0x7E)) {
                        i = '.';
                    }
                    drawText(imageGc, HUBl + 55 + k, HUBt + (j << 1), cData2, "" + (char) i);
                }
            }

            //  --------------------
            //   Update disassembly
            //  --------------------

            boolean disCog = (curDisMode == dmPC) && PCInCog || (curDisMode == dmCOG);
            int x = (DISl + 15) * charWidth - (charWidth >> 1);
            int xs = charWidth * 42;
            int ys = charHeight;
            int r = charHeight >> 2;

            for (int i = 0; i < DIS_LINES; i++) {
                String s;
                int addr;

                // Draw address
                if (disCog) {
                    addr = curDisAddr + i;
                    s = (addr < 0x200) ? "R" : "L";
                    s += "-" + String.format("%03X", addr);
                }
                else {
                    addr = (curDisAddr + (i << 2)) & 0xFFFFF;
                    s = String.format("%05X", addr);
                }
                drawText(imageGc, DISl, DISt + (i << 1), cData2, s);

                // Draw instruction long
                int inst = buffDis[i];
                drawText(imageGc, DISl + 6, DISt + (i << 1), cData, String.format("%08X", inst));

                // Disassemble instruction long, may be register ROM
                if (disCog && (addr >= 0x1F8) && (addr <= 0x1FF)) {
                    s = "[ROM]        " + DebugROM[addr & 0x7];
                }
                else {
                    s = P2Disassembler.disassemble(addr, inst);
                }

                // Prepare to draw instruction
                boolean HiddenPC = (addr < 0x400) && (curDisMode == dmHUB);
                int y = ((DISt + 1 + (i << 1)) * charHeight) >> 1;

                // Inverse if instruction at PC
                if ((addr == curPC) && !HiddenPC) {
                    smoothShape(imageGc, x, y - (charHeight >> 1), xs, ys, r, r, 0, cData, 255);
                    drawText(imageGc, DISl + 15, DISt + (i << 1), cBox2, s);
                }
                else {
                    drawText(imageGc, DISl + 15, DISt + (i << 1), cData2, s);

                    // Strikethrough if instruction is to be skipped
                    int j = (addr < 0x400) ? (addr - curPC) : (addr - curPC) / 4;
                    if (SkipOn && (j >= 0) && (j <= 31) && ((BRKZ >> j) & 1) != 0 && !HiddenPC) {
                        smoothShape(imageGc, x, y - 1, xs, ys >> 1, r, r, 0, cData2, 160);
                    }

                    // Highlight if breakpoint instruction
                    if ((breakValue & 0x400) != 0 && (breakAddr == addr) && !HiddenPC) {
                        smoothShape(imageGc, x, y - (charHeight >> 1), xs, ys, r, r, 0, cName, 64);
                    }
                }
            }

            //  ----------------
            //   Update watches
            //  ----------------

            for (int i = 0; i < watchReg.length; i++) {
                if (watchReg[i] == 0xFFFF) {
                    watchReg[i] = 0;
                }
                else if (cogImage[i] != cogImageOld[i]) {
                    watchReg[i] = 1000;
                }
                else if (watchReg[i] > 0) {
                    watchReg[i]--;
                }
            }
            for (int i = 0; i < watchReg.length; i++) {
                if (watchReg[i] != 0) {
                    index = -1;
                    for (int j = 0; j < watchRegList.length; j++) {
                        if ((watchRegList[j] >> 16) == i) {
                            index = j;
                            break;
                        }
                    }
                    if (index < 0) {
                        int k = 0xFFFF;
                        for (int j = watchRegList.length - 1; j >= 0; j--) {
                            if ((watchRegList[j] & 0xFFFF) <= k) {
                                k = watchRegList[j] & 0xFFFF;
                                index = j;
                            }
                        }
                    }
                    watchRegList[index] = (i << 16) + watchReg[i];
                }
            }
            // Draw reg watch
            if ((watchRegList[0] & 0xFFFF) == 0) {
                drawText(imageGc, WATCHl + 3, WATCHt, cName, "REG \u0394");
            }
            else {
                for (int i = 0; i < REG_WATCH_LIST_SIZE; i++) {
                    if ((watchRegList[i] & 0xFFFF) > 0) {
                        int y = WATCHt + (i << 1);
                        drawText(imageGc, WATCHl, y, cData2, String.format("%03X", watchRegList[i] >> 16));
                        drawText(imageGc, WATCHl + 4, y, cData, String.format("%08X", cogImage[watchRegList[i] >> 16]));
                    }
                }
            }

            // Update smart pin watch
            for (int i = 0; i < watchSmart.length; i++) {
                if (watchSmart[i] == 0xFFFF) {
                    watchSmart[i] = 0;
                }
                else if (watchSmartAll || ((((cogImage[0x1FA + i >> 5] >> (i & 0x1F)) & 1) != 0) && (i < 62) && (smartBuff[i] != smartBuffOld[i]))) {
                    watchSmart[i] = 1000;
                }
                else if (watchSmart[i] > 0) {
                    watchSmart[i]--;
                }
            }
            for (int i = 0; i < watchSmart.length; i++) {
                if (watchSmart[i] != 0) {
                    index = -1;
                    for (int j = 0; j < watchSmartList.length; j++) {
                        if ((watchSmartList[j] >> 16) == i) {
                            index = j;
                            break;
                        }
                    }
                    if (index < 0) {
                        int k = 0xFFFF;
                        for (int j = watchSmartList.length - 1; j >= 0; j--) {
                            if ((watchSmartList[j] & 0xFFFF) <= k) {
                                k = watchSmartList[j] & 0xFFFF;
                                index = j;
                            }
                        }
                    }
                    watchSmartList[index] = i << 16 + watchSmart[i];
                }
            }
            // Draw smart pin watch
            if ((watchSmartList[0] & 0xFFFF) == 0) {
                drawText(imageGc, SMARTl, SMARTt, cName, "RQPIN \u0394");
            }
            else {
                for (int i = 0; i < watchSmartList.length; i++) {
                    if ((watchSmartList[i] & 0xFFFF) > 0) {
                        x = SMARTl + (i * 14);
                        int k = watchSmartList[i] >> 16;
                        drawText(imageGc, x, SMARTt, cData2, String.format("P%d", k));
                        drawText(imageGc, x + 4, SMARTt, cData, String.format("%08X", smartBuff[k] >> 16));
                    }
                }
            }

            //  ----------------
            //   Update buttons
            //  ----------------

            // Highlight MAIN button?
            if ((breakValue & 0x00000001) != 0) {
                drawBox(imageGc, bMAINl, bMAINt, bMAINw, bMAINh, cModeButton);
                drawText(imageGc, bMAINl, bMAINt, cModeText, "MAIN");
            }
            if ((breakValue & 0x00000002) != 0) {
                drawBox(imageGc, bINT1l, bINT1t, bINT1w, bINT1h, cModeButton);
                drawText(imageGc, bINT1l, bINT1t, cModeText, "INT1");
            }
            if ((breakValue & 0x00000004) != 0) {
                drawBox(imageGc, bINT2l, bINT2t, bINT2w, bINT2h, cModeButton);
                drawText(imageGc, bINT2l, bINT2t, cModeText, "INT2");
            }
            if ((breakValue & 0x00000008) != 0) {
                drawBox(imageGc, bINT3l, bINT3t, bINT3w, bINT3h, cModeButton);
                drawText(imageGc, bINT3l, bINT3t, cModeText, "INT3");
            }
            if ((breakValue & 0x00000010) != 0) {
                drawBox(imageGc, bDEBUGl, bDEBUGt, bDEBUGw, bDEBUGh, cModeButton);
                drawText(imageGc, bDEBUGl, bDEBUGt, cModeText, "DEBUG");
            }
            if ((breakValue & 0x00000020) != 0) {
                drawBox(imageGc, bINT1El, bINT1Et, bINT1Ew, bINT1Eh, cModeButton);
                drawText(imageGc, bINT1El, bINT1Et, cModeText, "\u2794INT1");
            }
            if ((breakValue & 0x00000040) != 0) {
                drawBox(imageGc, bINT2El, bINT2Et, bINT2Ew, bINT2Eh, cModeButton);
                drawText(imageGc, bINT2El, bINT2Et, cModeText, "\u2794INT2");
            }
            if ((breakValue & 0x00000080) != 0) {
                drawBox(imageGc, bINT3El, bINT3Et, bINT3Ew, bINT3Eh, cModeButton);
                drawText(imageGc, bINT3El, bINT3Et, cModeText, "\u2794INT3");
            }
            if ((breakValue & 0x00000100) != 0) {
                drawBox(imageGc, bINITl, bINITt, bINITw, bINITh, cModeButton);
                drawText(imageGc, bINITl, bINITt, cModeTextDim, "INIT");
            }
            if ((breakValue & 0x00000200) != 0) {
                drawBox(imageGc, bEVENTl, bEVENTt, bEVENTw, bEVENTh, cModeButton);
                drawText(imageGc, bEVENTl, bEVENTt, cModeText, EventName[breakEvent] + "\u2191");
            }
            else {
                drawText(imageGc, bEVENTl, bEVENTt, cModeTextDim, EventName[breakEvent]);
            }
            if ((breakValue & 0x00000400) != 0) {
                drawBox(imageGc, bADDRl, bADDRt, bADDRw, bADDRh, cModeButton);
                drawText(imageGc, bADDRl, bADDRt, cModeText, String.format("%05X", breakAddr & 0xFFFFF));
            }
            else {
                drawText(imageGc, bADDRl, bADDRt, cModeTextDim, String.format("%05X", breakAddr & 0xFFFFF));
            }
            if ((breakValue & 0x000006FF) == 0) {
                drawBox(imageGc, bBREAKl, bBREAKt, bBREAKw, bBREAKh, cModeButtonDim);
                drawText(imageGc, bBREAKl, bBREAKt, cModeTextDim, "BREAK");
            }

            drawBox(imageGc, bGOl, bGOt, bGOw, bGOh, cCmdButton);
            drawText(imageGc, bGOl + 3 + q2, bGOt, cCmdText, repeatMode ? "STOP" : " GO");

            //  ----------------------------
            //   Update reg/lut/hub bitmaps
            //  ----------------------------

            if (regMapUpdate) {
                GC gcMap = new GC(regMapImage);
                try {
                    int y = 0;
                    for (int h = 0; h < 512; h++, y++) {
                        int i = cogImageOld[h] = cogImage[h];
                        for (x = 31, r = 1; x >= 0; x--, r <<= 1) {
                            gcMap.setForeground((i & r) != 0 ? cHighSame : cBackground);
                            gcMap.drawPoint(x, y);
                        }
                    }
                } finally {
                    gcMap.dispose();
                }
                regMapUpdate = false;
            }
            imageGc.drawImage(regMapImage, 0, 0, 32, 512, RegMap.x, RegMap.y, RegMap.width, RegMap.height);

            if (lutMapUpdate) {
                GC gcMap = new GC(lutMapImage);
                try {
                    int y = 0;
                    for (int h = 512; h < 1024; h++, y++) {
                        int i = cogImageOld[h] = cogImage[h];
                        for (x = 31, r = 1; x >= 0; x--, r <<= 1) {
                            gcMap.setForeground((i & r) != 0 ? cHighSame : cBackground);
                            gcMap.drawPoint(x, y);
                        }
                    }
                } finally {
                    gcMap.dispose();
                }
                lutMapUpdate = false;
            }
            imageGc.drawImage(lutMapImage, 0, 0, 32, 512, LutMap.x, LutMap.y, LutMap.width, LutMap.height);

            if (hubMapUpdate) {
                GC gcMap = new GC(hubMapImage);
                try {
                    int h = 0;
                    for (int y = 0; y < HUB_MAP_HEIGHT; y++) {
                        for (x = 0; x < HUB_MAP_WIDTH; x++) {
                            int i = hubSubBlockOld[h] = hubSubBlock[h];
                            gcMap.setForeground(i != 0x0142 ? cHighSame : cBackground);
                            gcMap.drawPoint(x, y);
                            h++;
                        }
                    }
                } finally {
                    gcMap.dispose();
                }
                hubMapUpdate = false;
            }
            imageGc.drawImage(hubMapImage, 0, 0, HUB_MAP_WIDTH, HUB_MAP_HEIGHT, HubMap.x, HubMap.y, HubMap.width, HubMap.height);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void drawRegBin(GC gc, int left, int top, int value, Color color) {
        StringBuilder sb = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            sb.append(((value >> i) & 1) == 0 ? "0" : "1");
            if ((i % 8) == 0) {
                sb.append(" ");
            }
        }
        drawText(gc, left, top, color, sb.toString());
    }

    void drawInt(GC gc, int left, int top, int value) {
        int i = (BRKCZ >> ((value << 2) + 4)) & 0xF;
        if (i == 0) {
            drawText(gc, left + 5, top, cData2, "off");
        }
        else {
            drawText(gc, left + 5, top, cData, EventName[i]);
            i = (BRKCZ >> (value << 1)) & 3;
            String s = "idle";
            if (i == 3) {
                s = "busy";
            }
            else if (i == 2) {
                s = "wait";
            }
            drawText(gc, left + 9, top, cData2, s);
        }
    }

    void drawPtrBytes(GC gc, int left, int top, int addr, byte[] buff) {
        // Draw address
        drawText(gc, left + 5, top, cData, String.format("%05X", addr));
        // Draw bytes and characters
        for (int i = 0; i < buff.length; i++) {
            int ch = buff[i] & 0xFF;

            if (i == PTR_CENTER) {
                smoothShape(gc, (left + 11 + i * 3) * charWidth - charWidth / 2, (top * charHeight) >> 1,
                    charWidth * 3, charHeight,
                    charHeight, charHeight,
                    0, cData2, 255);

                smoothShape(gc, (left + 11 + buff.length * 3 + 1 + i) * charWidth, (top * charHeight) >> 1,
                    charWidth, charHeight,
                    charHeight, charHeight,
                    0, cData2, 255);
            }

            drawText(gc, left + 11 + i * 3, top, cData, String.format("%02X", ch));
            if (ch < 0x20 || ch >= 0x7E) {
                ch = '.';
            }
            drawText(gc, left + 11 + buff.length * 3 + 1 + i, top, cData, "" + (char) ch);
        }
    }

    void drawBox(GC gc, int left, int top, int width, int height, Color color) {
        int x = frac(left, charWidth) - charWidth / 2;
        int y = frac(top, charHeight) >> 1;
        int w = frac(width, charWidth) + charWidth;
        int h = frac(height, charHeight) >> 1;

        gc.setForeground(new Color(
            ((color.getRed() * 3) >> 1) & 0xFF,
            ((color.getGreen() * 3) >> 1) & 0xFF,
            ((color.getBlue() * 3) >> 1) & 0xFF));
        gc.setBackground(color);

        gc.fillRoundRectangle(x, y, w, h, 5, 5);
        gc.drawRoundRectangle(x, y, w, h, 5, 5);
    }

    void drawText(GC gc, int x, int y, Color color, String text) {
        gc.setForeground(color);
        gc.drawText(text, frac(x, charWidth), frac(y, charHeight) >> 1, true);
    }

    void smoothShape(GC gc, int xc, int yc, int xs, int ys, int xro, int yro, int thick, Color color, int opacity) {
        gc.setAlpha(opacity);
        gc.setLineWidth(thick);
        try {
            gc.setBackground(color);
            gc.fillRoundRectangle(xc, yc, xs, ys - 1, 5, 5);
        } finally {
            gc.setAlpha(255);
            gc.setLineWidth(0);
        }
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

            boolean run;

            @Override
            public void run() {
                try {
                    DebugPAsmWindow window = new DebugPAsmWindow(0);
                    window.create();
                    window.update();
                    window.open();

                    run = true;
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (run) {
                                display.asyncExec(new Runnable() {

                                    @Override
                                    public void run() {
                                        window.update();
                                    }

                                });
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    // Do nothing
                                }
                            }
                        }

                    });
                    thread.start();

                    while (display.getShells().length != 0) {
                        if (!display.readAndDispatch()) {
                            display.sleep();
                        }
                    }

                    run = false;
                    thread.join(1000);

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        display.dispose();
    }

}
