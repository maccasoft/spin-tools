/*
 * Copyright (c) 2021-26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import java.util.function.Consumer;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;

import com.maccasoft.propeller.Preferences;
import com.maccasoft.propeller.internal.CircularBuffer;

public class DebugFFTWindow extends DebugWindow {

    static final int FFT_MAX = 1 << 11;

    static double log2(int value) {
        return Math.log(value) / Math.log(2.0);
    }

    int FFTexp;
    int FFTfirst;
    int FFTlast;

    Color backColor;
    Color gridColor;

    ColorMode colorMode;
    int colorTune;
    Color[] lutColors;

    int dotSize;
    int lineSize;
    int textSize;
    boolean logScale;

    PackMode packMode;

    int samples;
    int channelIndex;
    Channel[] channelData;

    int sampleCount;

    int rate;
    int rateCount;

    Font font;
    int charHeight;

    FFT fft;

    class Channel {
        String name;

        int mag;
        int high;
        int tall;
        int base;
        int legend;
        Color color;

        int[] FFTsamp;
        int[] FFTpower;

        String legendMax;
        int legendMaxY;
        String legendMin;
        int legendMinY;

        int[] array;

        Channel(String name, int mag, int high, int tall, int base, int legend, Color color) {
            this.name = name;

            this.mag = mag;
            this.high = high;
            this.tall = tall;
            this.base = base;
            this.legend = legend;
            this.color = color;

            this.FFTsamp = new int[FFT_MAX];
            this.FFTpower = new int[FFT_MAX / 2];
            this.array = new int[(FFTlast - FFTfirst + 1) * 2];

            legendMin = String.format("+%d", 0);
            legendMinY = MARGIN_HEIGHT + charHeight + imageSize.y - base - 1;

            legendMax = String.format("+%d", high);
            legendMaxY = MARGIN_HEIGHT + charHeight + imageSize.y - base - tall - 1;
        }

        void update() {
            double sx = (double) imageSize.x / (double) (FFTlast - FFTfirst);
            double sy = (double) (tall - 1) / (double) high;

            double x = 0;
            for (int i = FFTfirst, idx = 0; i <= FFTlast; i++) {
                int v = FFTpower[i];

                if (logScale) {
                    v = (int) Math.round(log2(v + 1) / log2((high + 1)) * high);
                }

                array[idx++] = MARGIN_WIDTH + (int) Math.round(x);
                array[idx++] = MARGIN_HEIGHT + charHeight + (imageSize.y - base - 1) - (int) Math.round(v * sy);

                x += sx;
            }
        }

        void plot(GC gc) {
            if (legend != 0) {
                gc.setLineWidth(0);
                gc.setLineStyle(SWT.LINE_DOT);
                gc.setForeground(gridColor);
                gc.setBackground(backColor);

                if ((legend & 0b0001) != 0) {
                    gc.drawLine(MARGIN_WIDTH, legendMinY, imageSize.x, legendMinY);
                }
                if ((legend & 0b0010) != 0) {
                    gc.drawLine(MARGIN_WIDTH, legendMaxY, imageSize.x, legendMaxY);
                }

                if (legendMin != null && (legend & 0b0100) != 0) {
                    Point extent = gc.stringExtent(legendMin);
                    gc.fillRectangle(MARGIN_WIDTH + 1, legendMinY - extent.y / 2, 2 + extent.x + 2, extent.y);
                    gc.drawText(legendMin, MARGIN_WIDTH + 2, legendMinY - extent.y / 2, true);
                }
                if (legendMax != null && (legend & 0b1000) != 0) {
                    Point extent = gc.stringExtent(legendMax);
                    gc.fillRectangle(MARGIN_WIDTH + 1, legendMaxY - extent.y / 2, 2 + extent.x + 2, extent.y);
                    gc.drawText(legendMax, MARGIN_WIDTH + 2, legendMaxY - extent.y / 2, true);
                }
            }
            gc.setLineWidth(lineSize);
            gc.setLineStyle(SWT.LINE_SOLID);
            gc.setForeground(color);
            gc.drawPolyline(array);
        }

    }

    public DebugFFTWindow(CircularBuffer transmitBuffer) {
        super(transmitBuffer);

        backColor = new Color(0, 0, 0);
        gridColor = new Color(64, 64, 64);

        colorMode = ColorMode.RGB24;
        colorTune = 0;
        lutColors = new Color[256];

        dotSize = 0;
        lineSize = 1;
        textSize = 0;

        packMode = PackMode.NONE();

        samples = 512;

        FFTexp = (int) log2(samples);
        FFTfirst = 0;
        FFTlast = (samples / 2) - 1;

        channelIndex = 0;
        channelData = new Channel[0];

        sampleCount = 0;

        rate = 1;
        rateCount = 0;
    }

    @Override
    public void setup(KeywordIterator iter) {
        String cmd;

        while (iter.hasNext()) {
            cmd = iter.next();

            switch (cmd) {
                case "TITLE":
                    title(iter);
                    break;

                case "POS":
                    pos(iter);
                    break;

                case "SIZE":
                    size(iter);
                    break;

                case "SAMPLES":
                    if (iter.hasNextNumber()) {
                        int val = iter.nextNumber();
                        if (val < 4) {
                            val = 4;
                        }
                        if (val > FFT_MAX) {
                            val = FFT_MAX;
                        }
                        FFTexp = (int) log2(val);
                        samples = 1 << FFTexp;
                        FFTfirst = 0;
                        FFTlast = (samples / 2) - 1;
                        if (iter.hasNextNumber()) {
                            val = iter.nextNumber();
                            if (val >= 0 && val <= FFTlast - 1) {
                                FFTfirst = val;
                            }
                            if (iter.hasNextNumber()) {
                                val = iter.nextNumber();
                                if (val >= FFTfirst + 1 && val <= FFTlast) {
                                    FFTlast = val;
                                }
                            }
                        }
                    }
                    break;

                case "RATE":
                    rate(iter);
                    break;

                case "DOTSIZE":
                    if (iter.hasNextNumber()) {
                        dotSize = iter.nextNumber();
                    }
                    break;

                case "LINESIZE":
                    if (iter.hasNextNumber()) {
                        lineSize = iter.nextNumber() / 2;
                    }
                    break;

                case "TEXTSIZE":
                    if (iter.hasNextNumber()) {
                        textSize = iter.nextNumber();
                    }
                    break;

                case "COLOR":
                    if (iter.hasNextNumber()) {
                        iter.nextNumber();
                        if (iter.hasNextNumber()) {
                            iter.nextNumber();
                        }
                    }
                    break;

                case "LOGSCALE":
                    logScale = true;
                    break;

                case "LONGS_1BIT":
                case "LONGS_2BIT":
                case "LONGS_4BIT":
                case "LONGS_8BIT":
                case "LONGS_16BIT":
                case "WORDS_1BIT":
                case "WORDS_2BIT":
                case "WORDS_4BIT":
                case "WORDS_8BIT":
                case "BYTES_1BIT":
                case "BYTES_2BIT":
                case "BYTES_4BIT":
                    packMode = packedMode(cmd, iter);
                    break;

                case "HIDEXY":
                    break;
            }
        }

        fft = new FFT(FFTexp);

        Font textFont = JFaceResources.getTextFont();
        FontData fontData = textFont.getFontData()[0];
        if (Preferences.getInstance().getEditorFont() != null) {
            fontData = StringConverter.asFontData(Preferences.getInstance().getEditorFont());
        }

        font = new Font(display, fontData.getName(), textSize != 0 ? textSize : fontData.getHeight(), SWT.BOLD);

        GC gc = new GC(canvas);
        try {
            gc.setFont(font);
            charHeight = gc.stringExtent("X").y;
            for (Channel ch : channelData) {
                Point extent = gc.stringExtent(ch.name);
                if (extent.y > charHeight) {
                    charHeight = extent.y;
                }
            }
        } finally {
            gc.dispose();
        }

        canvas.addPaintListener(new PaintListener() {

            @Override
            public void paintControl(PaintEvent e) {
                paint(e.gc);
            }

        });

        GridData gridData = (GridData) canvas.getLayoutData();
        gridData.widthHint = MARGIN_WIDTH + imageSize.x + MARGIN_WIDTH;
        gridData.heightHint = MARGIN_HEIGHT + charHeight + imageSize.y + MARGIN_WIDTH;

        shell.pack();
        shell.redraw();
    }

    void rate(KeywordIterator iter) {
        rate = -1;
        rateCount = 0;
        if (iter.hasNextNumber()) {
            rate = iter.nextNumber();
        }
    }

    @Override
    protected void paint(GC gc) {
        gc.setAdvanced(true);
        gc.setAntialias(SWT.OFF);
        gc.setTextAntialias(SWT.ON);
        gc.setInterpolation(SWT.NONE);
        gc.setLineCap(SWT.CAP_SQUARE);
        gc.setFont(font);

        gc.setLineWidth(0);

        gc.setBackground(backColor);
        gc.fillRectangle(canvas.getClientArea());

        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setForeground(gridColor);

        gc.drawRectangle(MARGIN_WIDTH, MARGIN_HEIGHT + charHeight, imageSize.x, imageSize.y);
        if (logScale) {
            String text = "logscale";
            Point extent = gc.stringExtent(text);
            gc.drawString("logscale", MARGIN_WIDTH + imageSize.x - extent.x, MARGIN_HEIGHT + charHeight - extent.y - 1, true);
        }

        int x = MARGIN_WIDTH;
        int spacing = gc.stringExtent("A").x;
        for (int i = 0; i < channelData.length; i++) {
            Point extent = gc.stringExtent(channelData[i].name);

            gc.setForeground(channelData[i].color);
            gc.drawString(channelData[i].name, x, MARGIN_HEIGHT + charHeight - extent.y - 1, true);

            gc.setLineWidth(0);
            channelData[i].plot(gc);

            x += extent.x + spacing;
        }
    }

    @Override
    public void update(KeywordIterator iter) {
        String cmd;

        if (iter.hasNext()) {
            cmd = iter.next();

            if (isNumber(cmd)) {
                try {
                    packMode.newPack(stringToNumber(cmd));
                    for (int i = 0; i < packMode.size; i++) {
                        processSample(packMode.unpack());
                    }
                } catch (Exception e) {
                    // Do nothing
                }
            }
            else if (isString(cmd)) {
                channel(stringStrip(cmd), iter);
            }
            else {
                switch (cmd.toUpperCase()) {
                    case "CLEAR":
                        break;

                    case "SAVE":
                        save(iter);
                        break;

                    case "CLOSE":
                        shell.dispose();
                        break;

                    case "PC_KEY":
                        sendKeyPress();
                        break;

                    case "PC_MOUSE":
                        sendMouse();
                        break;
                }
            }
        }
    }

    void channel(String name, KeywordIterator iter) {
        Color color = new Color(0, 250, 0);

        int mag = iter.hasNextNumber() ? iter.nextNumber() : 0;
        int high = iter.hasNextNumber() ? iter.nextNumber() : 0x7FFFFFFF;
        int tall = iter.hasNextNumber() ? iter.nextNumber() : imageSize.y;
        int base = iter.hasNextNumber() ? iter.nextNumber() : 0;
        int grid = iter.hasNextNumber() ? iter.nextNumber() : 0;

        if (iter.hasNext()) {
            int h = 0;
            int p = 8;

            String s = iter.next().toUpperCase();
            switch (s) {
                case "BLACK":
                    color = new Color(0, 0, 0);
                    break;
                case "WHITE":
                    color = new Color(255, 255, 255);
                    break;
                case "ORANGE":
                case "BLUE":
                case "GREEN":
                case "CYAN":
                case "RED":
                case "MAGENTA":
                case "YELLOW":
                case "GRAY":
                    h = RGBColor.valueOf(s).ordinal();
                    if (iter.hasNextNumber()) {
                        p = iter.nextNumber();
                    }
                    color = translateColor((h << 5) | (p << 1), ColorMode.RGBI8X);
                    break;
                default:
                    iter.back();
                    break;
            }
        }

        int i = 0;
        Channel[] newArray = new Channel[channelData.length + 1];
        while (i < channelData.length) {
            newArray[i] = channelData[i++];
        }
        newArray[i++] = new Channel(name, mag, high, tall, base, grid, color);
        channelData = newArray;
    }

    Color translateColor(int p, ColorMode mode) {
        int color;

        switch (mode) {
            case LUT1:
                return lutColors[p & 0b1];
            case LUT2:
                return lutColors[p & 0b11];
            case LUT4:
                return lutColors[p & 0b1111];
            case LUT8:
                return lutColors[p & 0b11111111];
            default:
                color = mode.translateColor(p, colorTune);
                return new Color(display, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
        }
    }

    void processSample(int sample) {
        System.arraycopy(channelData[channelIndex].FFTsamp, 1, channelData[channelIndex].FFTsamp, 0, samples - 1);
        channelData[channelIndex].FFTsamp[samples - 1] = sample;

        channelIndex++;
        if (channelIndex >= channelData.length) {
            if (sampleCount < samples) {
                sampleCount++;
            }
            if (rateCount < rate) {
                rateCount++;
            }

            if (sampleCount >= samples && rateCount >= rate) {
                for (int i = 0; i < channelData.length; i++) {
                    fft.performFFT(channelData[i].mag, channelData[i].FFTsamp, channelData[i].FFTpower);
                }
                update();
                rateCount = 0;
            }

            channelIndex = 0;
        }
    }

    void update() {
        for (int i = 0; i < channelData.length; i++) {
            channelData[i].update();
        }
        canvas.redraw();
    }

    static String[] data = new String[] {
        "SIZE 250 200 SAMPLES 2048 0 127 RATE 256 LOGSCALE COLOR YELLOW 4 YELLOW 5",
        "'FFT' 0 1000 180 10 15 YELLOW 12",
        "194",
        "380",
        "552",
        "703",
        "827",
        "920",
        "979",
        "1_000",
        "983",
        "929",
        "840",
        "719",
        "570",
        "400",
        "215",
        "21",
        "-173",
        "-361",
        "-535",
        "-689",
        "-817",
        "-913",
        "-975",
        "-1_000",
        "-986",
        "-935",
        "-848",
        "-729",
        "-582",
        "-413",
        "-228",
        "-34",
        "161",
        "350",
        "526",
        "681",
        "811",
        "909",
        "973",
        "999",
        "987",
        "938",
        "852",
        "733",
        "587",
        "417",
        "232",
        "38",
        "-158",
        "-347",
        "-524",
        "-680",
        "-810",
        "-909",
        "-973",
        "-999",
        "-987",
        "-937",
        "-851",
        "-732",
        "-585",
        "-415",
        "-229",
        "-34",
        "162",
        "352",
        "529",
        "685",
        "814",
        "912",
        "975",
        "1_000",
        "986",
        "934",
        "846",
        "725",
        "576",
        "404",
        "217",
        "21",
        "-175",
        "-365",
        "-541",
        "-695",
        "-823",
        "-918",
        "-978",
        "-1_000",
        "-983",
        "-927",
        "-836",
        "-711",
        "-559",
        "-386",
        "-197",
        "0",
        "197",
        "386",
        "560",
        "712",
        "836",
        "928",
        "983",
        "1_000",
        "978",
        "917",
        "820",
        "692",
        "536",
        "359",
        "168",
        "-30",
        "-226",
        "-414",
        "-585",
        "-734",
        "-853",
        "-940",
        "-989",
        "-999",
        "-970",
        "-902",
        "-800",
        "-665",
        "-505",
        "-324",
        "-131",
        "68",
        "264",
        "449",
        "617",
        "761",
        "874",
        "953",
        "994",
        "996",
        "958",
        "883",
        "772",
        "631",
        "465",
        "281",
        "85",
        "-114",
        "-309",
        "-491",
        "-654",
        "-791",
        "-897",
        "-967",
        "-998",
        "-990",
        "-942",
        "-857",
        "-738",
        "-589",
        "-417",
        "-228",
        "-30",
        "169",
        "361",
        "540",
        "696",
        "825",
        "921",
        "980",
        "1_000",
        "980",
        "921",
        "825",
        "696",
        "539",
        "360",
        "167",
        "-33",
        "-231",
        "-420",
        "-593",
        "-741",
        "-860",
        "-945",
        "-991",
        "-998",
        "-964",
        "-892",
        "-784",
        "-644",
        "-478",
        "-293",
        "-97",
        "104",
        "301",
        "485",
        "650",
        "789",
        "896",
        "967",
        "998",
        "990",
        "941",
        "855",
        "733",
        "583",
        "408",
        "217",
        "17",
        "-183",
        "-376",
        "-554",
        "-710",
        "-837",
        "-929",
        "-985",
        "-1_000",
        "-974",
        "-909",
        "-807",
        "-672",
        "-510",
        "-327",
        "-131",
        "71",
        "269",
        "457",
        "626",
        "770",
        "883",
        "959",
        "996",
        "993",
        "949",
        "867",
        "748",
        "600",
        "427",
        "236",
        "36",
        "-166",
        "-361",
        "-542",
        "-700",
        "-829",
        "-925",
        "-982",
        "-1_000",
        "-976",
        "-913",
        "-811",
        "-677",
        "-515",
        "-331",
        "-134",
        "69",
        "269",
        "457",
        "627",
        "772",
        "884",
        "960",
        "997",
        "992",
        "947",
        "862",
        "742",
        "592",
        "416",
        "224",
        "22",
        "-180",
        "-376",
        "-555",
        "-712",
        "-840",
        "-932",
        "-986",
        "-999",
        "-971",
        "-903",
        "-797",
        "-658",
        "-492",
        "-305",
        "-105",
        "98",
        "298",
        "486",
        "653",
        "793",
        "900",
        "970",
        "999",
        "987",
        "934",
        "841",
        "714",
        "557",
        "377",
        "181",
        "-23",
        "-226",
        "-419",
        "-595",
        "-746",
        "-866",
        "-949",
        "-993",
        "-996",
        "-957",
        "-877",
        "-761",
        "-613",
        "-440",
        "-247",
        "-45",
        "160",
        "357",
        "540",
        "700",
        "831",
        "927",
        "984",
        "1_000",
        "973",
        "906",
        "800",
        "661",
        "494",
        "306",
        "105",
        "-100",
        "-301",
        "-490",
        "-657",
        "-797",
        "-904",
        "-972",
        "-1_000",
        "-985",
        "-928",
        "-832",
        "-701",
        "-540",
        "-357",
        "-158",
        "47",
        "251",
        "444",
        "618",
        "766",
        "881",
        "959",
        "997",
        "992",
        "945",
        "858",
        "734",
        "579",
        "400",
        "203",
        "-2",
        "-207",
        "-404",
        "-583",
        "-737",
        "-860",
        "-947",
        "-993",
        "-996",
        "-957",
        "-878",
        "-760",
        "-611",
        "-435",
        "-240",
        "-36",
        "171",
        "370",
        "553",
        "713",
        "842",
        "935",
        "988",
        "999",
        "967",
        "893",
        "781",
        "636",
        "463",
        "270",
        "66",
        "-141",
        "-342",
        "-529",
        "-693",
        "-826",
        "-925",
        "-983",
        "-1_000",
        "-973",
        "-904",
        "-796",
        "-654",
        "-484",
        "-293",
        "-89",
        "119",
        "322",
        "510",
        "677",
        "815",
        "917",
        "980",
        "1_000",
        "977",
        "912",
        "807",
        "667",
        "498",
        "308",
        "104",
        "-104",
        "-308",
        "-498",
        "-667",
        "-807",
        "-912",
        "-977",
        "-1_000",
        "-979",
        "-916",
        "-813",
        "-674",
        "-506",
        "-316",
        "-112",
        "96",
        "301",
        "493",
        "663",
        "804",
        "910",
        "976",
        "1_000",
        "980",
        "917",
        "814",
        "676",
        "508",
        "317",
        "113",
        "-96",
        "-301",
        "-493",
        "-664",
        "-805",
        "-911",
        "-977",
        "-1_000",
        "-979",
        "-915",
        "-812",
        "-672",
        "-503",
        "-311",
        "-106",
        "103",
        "309",
        "500",
        "670",
        "810",
        "915",
        "979",
        "1_000",
        "977",
        "911",
        "804",
        "662",
        "491",
        "298",
        "92",
        "-118",
        "-323",
        "-514",
        "-682",
        "-820",
        "-922",
        "-982",
        "-1_000",
        "-973",
        "-903",
        "-793",
        "-647",
        "-473",
        "-278",
        "-71",
        "140",
        "344",
        "533",
        "699",
        "833",
        "931",
        "987",
        "999",
        "966",
        "891",
        "776",
        "626",
        "448",
        "251",
        "42",
        "-169",
        "-372",
        "-559",
        "-721",
        "-850",
        "-942",
        "-991",
        "-997",
        "-957",
        "-875",
        "-754",
        "-599",
        "-417",
        "-216",
        "-5",
        "205",
        "407",
        "590",
        "747",
        "870",
        "954",
        "996",
        "993",
        "945",
        "854",
        "726",
        "564",
        "377",
        "173",
        "-38",
        "-248",
        "-447",
        "-626",
        "-776",
        "-892",
        "-967",
        "-999",
        "-986",
        "-928",
        "-828",
        "-691",
        "-522",
        "-330",
        "-123",
        "89",
        "298",
        "493",
        "666",
        "809",
        "915",
        "980",
        "1_000",
        "975",
        "906",
        "795",
        "649",
        "473",
        "275",
        "66",
        "-148",
        "-354",
        "-544",
        "-710",
        "-843",
        "-938",
        "-990",
        "-997",
        "-959",
        "-877",
        "-755",
        "-599",
        "-415",
        "-212",
        "0",
        "212",
        "415",
        "599",
        "755",
        "878",
        "959",
        "997",
        "990",
        "937",
        "841",
        "707",
        "540",
        "349",
        "141",
        "-73",
        "-283",
        "-481",
        "-657",
        "-802",
        "-911",
        "-978",
        "-1_000",
        "-976",
        "-907",
        "-797",
        "-649",
        "-472",
        "-273",
        "-62",
        "153",
        "360",
        "551",
        "716",
        "849",
        "942",
        "992",
        "996",
        "954",
        "868",
        "742",
        "582",
        "395",
        "189",
        "-25",
        "-239",
        "-441",
        "-623",
        "-776",
        "-893",
        "-968",
        "-999",
        "-984",
        "-923",
        "-819",
        "-677",
        "-504",
        "-307",
        "-96",
        "120",
        "330",
        "524",
        "695",
        "833",
        "932",
        "988",
        "998",
        "962",
        "880",
        "758",
        "600",
        "414",
        "209",
        "-5",
        "-220",
        "-425",
        "-609",
        "-765",
        "-886",
        "-965",
        "-999",
        "-986",
        "-927",
        "-825",
        "-684",
        "-511",
        "-314",
        "-102",
        "114",
        "325",
        "521",
        "693",
        "832",
        "932",
        "988",
        "998",
        "961",
        "879",
        "755",
        "596",
        "409",
        "203",
        "-13",
        "-229",
        "-433",
        "-618",
        "-773",
        "-891",
        "-968",
        "-999",
        "-983",
        "-921",
        "-815",
        "-671",
        "-494",
        "-295",
        "-81",
        "136",
        "347",
        "541",
        "711",
        "846",
        "941",
        "992",
        "996",
        "952",
        "864",
        "734",
        "570",
        "378",
        "169",
        "-48",
        "-264",
        "-466",
        "-647",
        "-797",
        "-909",
        "-978",
        "-1_000",
        "-975",
        "-903",
        "-788",
        "-636",
        "-453",
        "-249",
        "-33",
        "185",
        "394",
        "584",
        "746",
        "873",
        "958",
        "997",
        "989",
        "933",
        "833",
        "693",
        "520",
        "321",
        "108",
        "-111",
        "-325",
        "-523",
        "-696",
        "-835",
        "-935",
        "-990",
        "-997",
        "-956",
        "-870",
        "-742",
        "-578",
        "-386",
        "-176",
        "43",
        "260",
        "464",
        "646",
        "797",
        "910",
        "978",
        "1_000",
        "973",
        "900",
        "783",
        "628",
        "443",
        "236",
        "18",
        "-200",
        "-410",
        "-599",
        "-760",
        "-883",
        "-964",
        "-999",
        "-985",
        "-923",
        "-817",
        "-671",
        "-493",
        "-290",
        "-74",
        "146",
        "359",
        "555",
        "724",
        "857",
        "949",
        "995",
        "993",
        "942",
        "845",
        "707",
        "535",
        "337",
        "122",
        "-98",
        "-314",
        "-515",
        "-690",
        "-832",
        "-934",
        "-990",
        "-997",
        "-956",
        "-868",
        "-738",
        "-572",
        "-377",
        "-165",
        "56",
        "274",
        "479",
        "660",
        "809",
        "919",
        "983",
        "999",
        "966",
        "886",
        "763",
        "601",
        "411",
        "200",
        "-21",
        "-240",
        "-448",
        "-634",
        "-789",
        "-905",
        "-977",
        "-1_000",
        "-974",
        "-900",
        "-782",
        "-626",
        "-438",
        "-229",
        "-8",
        "212",
        "423",
        "613",
        "772",
        "893",
        "971",
        "1_000",
        "980",
        "911",
        "797",
        "644",
        "459",
        "252",
        "31",
        "-190",
        "-403",
        "-595",
        "-758",
        "-884",
        "-965",
        "-999",
        "-983",
        "-919",
        "-808",
        "-658",
        "-475",
        "-268",
        "-48",
        "175",
        "389",
        "583",
        "749",
        "877",
        "962",
        "999",
        "986",
        "923",
        "815",
        "666",
        "484",
        "278",
        "58",
        "-165",
        "-380",
        "-576",
        "-743",
        "-873",
        "-960",
        "-998",
        "-987",
        "-926",
        "-819",
        "-670",
        "-488",
        "-282",
        "-62",
        "162",
        "378",
        "574",
        "742",
        "873",
        "959",
        "998",
        "987",
        "926",
        "818",
        "669",
        "487",
        "280",
        "59",
        "-165",
        "-381",
        "-577",
        "-745",
        "-875",
        "-961",
        "-998",
        "-986",
        "-923",
        "-814",
        "-664",
        "-480",
        "-272",
        "-50",
        "174",
        "390",
        "586",
        "752",
        "880",
        "964",
        "999",
        "983",
        "918",
        "806",
        "653",
        "467",
        "257",
        "34",
        "-190",
        "-405",
        "-599",
        "-763",
        "-889",
        "-969",
        "-1_000",
        "-980",
        "-910",
        "-794",
        "-637",
        "-448",
        "-237",
        "-13",
        "212",
        "426",
        "618",
        "779",
        "900",
        "975",
        "1_000",
        "974",
        "899",
        "778",
        "616",
        "424",
        "209",
        "-16",
        "-240",
        "-452",
        "-641",
        "-797",
        "-912",
        "-981",
        "-999",
        "-967",
        "-884",
        "-757",
        "-590",
        "-393",
        "-176",
        "50",
        "274",
        "483",
        "668",
        "818",
        "927",
        "988",
        "998",
        "956",
        "866",
        "730",
        "558",
        "356",
        "136",
        "-91",
        "-313",
        "-519",
        "-699",
        "-842",
        "-942",
        "-994",
        "-994",
        "-942",
        "-842",
        "-699",
        "-519",
        "-312",
        "-90",
        "138",
        "358",
        "560",
        "733",
        "868",
        "958",
        "998",
        "987",
        "924",
        "813",
        "661",
        "474",
        "262",
        "37",
        "-191",
        "-408",
        "-604",
        "-769",
        "-894",
        "-972",
        "-1_000",
        "-976",
        "-901",
        "-779",
        "-616",
        "-421",
        "-205",
        "23",
        "249",
        "462",
        "652",
        "807",
        "920",
        "985",
        "998",
        "960",
        "871",
        "737",
        "564",
        "361",
        "140",
        "-89",
        "-313",
        "-521",
        "-701",
        "-845",
        "-944",
        "-994",
        "-992",
        "-938",
        "-835",
        "-687",
        "-504",
        "-294",
        "-69",
        "160",
        "381",
        "582",
        "752",
        "882",
        "966",
        "1_000",
        "980",
        "909",
        "790",
        "630",
        "436",
        "219",
        "-9",
        "-237",
        "-453",
        "-644",
        "-802",
        "-917",
        "-984",
        "-999",
        "-961",
        "-872",
        "-737",
        "-563",
        "-359",
        "-136",
        "94",
        "319",
        "527",
        "707",
        "850",
        "948",
        "996",
        "990",
        "933",
        "825",
        "674",
        "487",
        "274",
        "47",
        "-183",
        "-404",
        "-602",
        "-769",
        "-895",
        "-974",
        "-1_000",
        "-973",
        "-894",
        "-768",
        "-601",
        "-401",
        "-181",
        "50",
        "278",
        "491",
        "678",
        "828",
        "935",
        "991",
        "995",
        "945",
        "845",
        "699",
        "517",
        "306",
        "79",
        "-152",
        "-376",
        "-579",
        "-751",
        "-882",
        "-967",
        "-1_000",
        "-979",
        "-905",
        "-783",
        "-619",
        "-421",
        "-201",
        "30",
        "259",
        "475",
        "665",
        "819",
        "929",
        "989",
        "996",
        "949",
        "852",
        "708",
        "526",
        "315",
        "88",
        "-145",
        "-369",
        "-573",
        "-747",
        "-880",
        "-966",
        "-1_000",
        "-979",
        "-906",
        "-783",
        "-618",
        "-420",
        "-199",
        "33",
        "263",
        "479",
        "669",
        "823",
        "932",
        "991",
        "995",
        "946",
        "846",
        "699",
        "515",
        "302",
        "73",
        "-160",
        "-384",
        "-587",
        "-759",
        "-889",
        "-971",
        "-1_000",
        "-974",
        "-896",
        "-768",
        "-599",
        "-397",
        "-173",
        "60",
        "290",
        "504",
        "691",
        "840",
        "943",
        "994",
        "992",
        "935",
        "827",
        "673",
        "483",
        "266",
        "35",
        "-198",
        "-420",
        "-620",
        "-785",
        "-908",
        "-980",
        "-999",
        "-963",
        "-875",
        "-738",
        "-560",
        "-352",
        "-125",
        "110",
        "338",
        "548",
        "728",
        "868",
        "960",
        "999",
        "983",
        "913",
        "793",
        "629",
        "430",
        "208",
        "-26",
        "-259",
        "-477",
        "-669",
        "-824",
        "-933",
        "-991",
        "-995",
        "-943",
        "-839",
        "-689",
        "-500",
        "-284",
        "-52",
        "183",
        "407",
        "610",
        "778",
        "903",
        "979",
        "1_000",
        "965",
        "877",
        "740",
        "563",
        "354",
        "125",
        "-111",
        "-340",
        "-551",
        "-731",
        "-871",
        "-962",
        "-999",
        "-981",
        "-908",
        "-785",
        "-618",
        "-416",
        "-192",
        "44",
        "277",
        "494",
        "685",
        "837",
        "942",
        "994",
        "991",
        "933",
        "823",
        "666",
        "472",
        "252",
        "18",
        "-218",
        "-441",
        "-639",
        "-802",
        "-920",
        "-986",
        "-997",
        "-953",
        "-854",
        "-708",
        "-522",
        "-307",
        "-74",
        "163",
        "390",
        "596",
        "768",
        "897",
        "976",
        "1_000",
        "967",
        "880",
        "744",
        "565",
        "355",
        "125",
        "-113",
        "-344",
        "-555",
        "-736",
        "-875",
        "-964",
        "-1_000",
        "-978",
        "-902",
        "-774",
        "-603",
        "-398",
        "-170",
        "68",
        "301",
        "518",
        "705",
        "853",
        "952",
        "997",
        "986",
        "919",
        "800",
        "635",
        "435",
        "209",
        "-28",
        "-263",
        "-484",
        "-678",
        "-832",
        "-940",
        "-994",
        "-992",
        "-933",
        "-821",
        "-662",
        "-466",
        "-243",
        "-6",
        "231",
        "455",
        "653",
        "814",
        "928",
        "990",
        "995",
        "944",
        "838",
        "685",
        "492",
        "272",
        "35",
        "-203",
        "-429",
        "-632",
        "-798",
        "-918",
        "-986",
        "-997",
        "-952",
        "-852",
        "-703",
        "-513",
        "-295",
        "-59",
        "180",
        "409",
        "614",
        "784",
        "909",
        "982",
        "999",
        "958",
        "862",
        "716",
        "530",
        "312",
        "77",
        "-162",
        "-393",
        "-601",
        "-774",
        "-902",
        "-979",
        "-999",
        "-962",
        "-869",
        "-726",
        "-541",
        "-325",
        "-90",
        "150",
        "382",
        "591",
        "767",
        "898",
        "977",
        "1_000",
        "965",
        "874",
        "732",
        "548",
        "333",
        "98",
        "-143",
        "-375",
        "-586",
        "-763",
        "-895",
        "-976",
        "-1_000",
        "-966",
        "-875",
        "-734",
        "-551",
        "-335",
        "-100",
        "142",
        "374",
        "586",
        "763",
        "895",
        "976",
        "1_000",
        "965",
        "875",
        "733",
        "549",
        "332",
        "96",
        "-145",
        "-378",
        "-589",
        "-766",
        "-898",
        "-977",
        "-1_000",
        "-964",
        "-871",
        "-728",
        "-542",
        "-324",
        "-88",
        "154",
        "387",
        "597",
        "772",
        "902",
        "979",
        "999",
        "960",
        "865",
        "719",
        "531",
        "311",
        "74",
        "-168",
        "-401",
        "-609",
        "-782",
        "-909",
        "-983",
        "-998",
        "-955",
        "-856",
        "-706",
        "-515",
        "-293",
        "-55",
        "188",
        "419",
        "625",
        "795",
        "918",
        "986",
        "997",
        "948",
        "844",
        "690",
        "495",
        "270",
        "30",
        "-212",
        "-442",
        "-645",
        "-811",
        "-928",
        "-991",
        "-994",
        "-939",
        "-829",
        "-669",
        "-469",
        "-242",
        "0",
        "242",
        "469",
        "669",
        "829",
        "939",
        "994",
        "990",
        "927",
        "809",
        "643",
        "439",
        "208",
        "-35",
        "-276",
        "-501",
        "-695",
        "-849",
        "-952",
        "-998",
        "-984",
        "-912",
        "-786",
        "-612",
        "-403",
        "-169",
        "75",
        "315",
        "536",
        "725",
        "870",
        "964",
        "1_000",
        "976",
        "894",
        "758",
        "577",
        "361",
        "124",
        "-121",
        "-359",
        "-575",
        "-756",
        "-893",
        "-975",
        "-1_000",
        "-964",
        "-870",
        "-725",
        "-535",
        "-314",
        "-73",
        "172",
        "406",
        "616",
        "790",
        "915",
        "986",
        "997",
        "948",
        "842",
        "686",
        "488",
        "260",
        "17",
        "-227",
        "-457",
        "-660",
        "-824",
        "-937",
        "-994",
        "-991",
        "-928",
        "-809",
        "-641",
        "-434",
        "-201",
        "44",
        "287",
        "512",
        "706",
        "858",
        "957",
        "999",
        "980",
        "902",
        "769",
        "589",
        "374",
        "136",
        "-110",
        "-350",
        "-569",
        "-752",
        "-891",
        "-975",
        "-1_000",
        "-964",
        "-869",
        "-722",
        "-531",
        "-307",
        "-65",
        "182",
        "417",
        "627",
        "799",
        "922",
        "989",
        "995",
        "941",
        "830",
        "668",
        "465",
        "234",
        "-12",
        "-257",
        "-486",
        "-686",
        "-843",
        "-949",
        "-997",
        "-984",
        "-911",
        "-782",
        "-605",
        "-391",
        "-153",
        "94",
        "336",
        "557",
        "744",
        "885",
        "972",
        "1_000",
        "966",
        "873",
        "726",
        "535",
        "310",
        "67",
        "-181",
        "-417",
        "-628",
        "-800",
        "-923",
        "-989",
        "-995",
        "-939",
        "-825",
        "-660",
        "-455",
        "-222",
        "26",
        "271",
        "500",
        "698",
        "853",
        "955",
        "999",
        "980",
        "902",
        "767",
        "585",
        "367",
        "126",
        "-123",
        "-364",
        "-583",
        "-765",
        "-901",
        "-980",
        "-999",
        "-956",
        "-853",
        "-698",
        "-499",
        "-270",
        "-24",
        "224",
        "458",
        "664",
        "828",
        "941",
        "995",
        "988",
        "919",
        "793",
        "618",
        "404",
        "165",
        "-84",
        "-328",
        "-552",
        "-741",
        "-884",
        "-972",
        "-1_000",
        "-965",
        "-870",
        "-721",
        "-526",
        "-299",
        "-53",
        "196",
        "433",
        "643",
        "813",
        "932",
        "993",
        "991",
        "928",
        "807",
        "635",
        "423",
        "185",
        "-65",
        "-311",
        "-537",
        "-730",
        "-877",
        "-969",
        "-1_000",
        "-969",
        "-876",
        "-729",
        "-536",
        "-309",
        "-63",
        "187",
        "426",
        "637",
        "809",
        "930",
        "992",
        "992",
        "929",
        "808",
        "636",
        "424",
        "185",
        "-65",
        "-312",
        "-538",
        "-731",
        "-878",
        "-970",
        "-1_000",
        "-967",
        "-873",
        "-724",
        "-529",
        "-301",
        "-54",
        "197",
        "435",
        "646",
        "816",
        "935",
        "994",
        "990",
        "924",
        "799",
        "623",
        "408",
        "167",
        "-84",
        "-330",
        "-556",
        "-746",
        "-889",
        "-975",
        "-1_000",
        "-961",
        "-861",
        "-706",
        "-506",
        "-274",
        "-25",
        "226",
        "462",
        "670",
        "834",
        "946",
        "997",
        "985",
        "910",
        "777",
        "594",
        "374",
        "130",
        "-123",
        "-368",
        "-589",
        "-773",
        "-907",
        "-984",
        "-997",
        "-947",
        "-837",
        "-673",
        "-465",
        "-228",
        "23",
        "273",
        "506",
        "706",
        "861",
        "961",
        "1_000",
        "974",
        "886",
        "741",
        "549",
        "321",
        "73",
        "-180",
        "-422",
        "-636",
        "-810",
        "-931",
        "-993",
        "-991",
        "-925",
        "-800",
        "-623",
        "-406",
        "-163",
        "90",
        "338",
        "564",
        "754",
        "895",
        "978",
        "999",
        "955",
        "849",
        "689",
        "484",
        "248",
        "-4",
        "-256",
        "-491",
        "-695",
        "-854",
        "-957",
        "-999",
        "-976",
        "-891",
        "-747",
        "-555",
        "-327",
        "-78",
        "176",
        "419",
        "635",
        "809",
        "931",
        "993",
        "991",
        "924",
        "797",
        "618",
        "400",
        "155",
        "-100",
        "-348",
        "-574",
        "-762",
        "-901",
        "-981",
        "-998",
        "-950",
        "-840",
        "-675",
        "-466",
        "-227",
        "27",
        "279",
        "513",
        "714",
        "868",
        "965",
        "1_000",
        "969",
        "876",
        "724",
        "526",
        "293",
        "42",
        "-213",
        "-454",
        "-665",
        "-833",
        "-946",
        "-997",
        "-983",
        "-905",
        "-768",
        "-580",
        "-354",
        "-105",
        "151",
        "397",
        "617",
        "796",
        "924",
        "991",
        "993",
        "929",
        "805",
        "628",
        "410",
        "164",
        "-92",
        "-342",
        "-570",
        "-760",
        "-900",
        "-981",
        "-998",
        "-949",
        "-837",
        "-671",
        "-460",
        "-219",
        "37",
        "290",
        "524",
        "724",
        "876",
        "970",
        "1_000",
        "964",
        "865",
        "708",
        "504",
        "268",
        "13",
        "-242",
        "-482",
        "-689",
        "-851",
        "-957",
        "-999",
        "-976",
        "-887",
        "-740",
        "-544",
        "-312",
        "-59",
        "198",
        "441",
        "656",
        "827",
        "943",
        "997",
        "984",
        "906",
        "768",
        "579",
        "352",
        "101",
        "-157",
        "-404",
        "-625",
        "-804",
        "-929",
        "-993",
        "-990",
        "-922",
        "-792",
        "-610",
        "-386",
        "-137",
        "121",
        "371",
        "596",
        "782",
        "916",
        "988",
        "995",
        "935",
        "813",
        "636",
        "417",
        "170",
        "-88",
        "-341",
        "-570",
        "-762",
        "-903",
        "-983",
        "-997",
        "-945",
        "-829",
        "-658",
        "-443",
        "-198",
        "60",
        "315",
        "548",
        "744",
        "891",
        "978",
        "999",
        "953",
        "843",
        "677",
        "465",
        "222",
        "-37",
        "-292",
        "-528",
        "-729",
        "-880",
        "-973",
        "-1_000",
        "-959",
        "-854",
        "-692",
        "-483",
        "-241",
        "17",
        "274",
        "512",
        "716",
        "872",
        "969",
        "1_000",
        "964",
        "863",
        "703",
    };

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
                /*int FFTExp = (int) (Math.log(2048) / Math.log(2));
                int FFTfirst = 0;
                int FFTlast = 127;

                FFT fft = new FFT(FFTExp, FFTfirst, FFTlast);

                int idx = 0;
                for (int i = 2; i < data.length; i++) {
                    KeywordIterator iter = new KeywordIterator(data[i]);
                    fft.FFTsamp[idx++] = iter.nextNumber();
                }
                fft.perform();

                System.out.println();*/

                try {
                    DebugWindow window = new DebugFFTWindow(new CircularBuffer(128));
                    window.create();
                    window.setup(new KeywordIterator(data[0]));
                    window.open();

                    display.timerExec(500, new Runnable() {

                        @Override
                        public void run() {
                            for (int i = 1; i < data.length; i++) {
                                window.update(new KeywordIterator(data[i]));
                            }
                        }

                    });

                    while (display.getShells().length != 0) {
                        if (!display.readAndDispatch()) {
                            display.sleep();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        display.dispose();
    }

}
