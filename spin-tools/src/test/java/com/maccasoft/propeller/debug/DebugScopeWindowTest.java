/*
 * Copyright (c) 26 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.debug;

import org.eclipse.swt.widgets.Display;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.maccasoft.propeller.internal.CircularBuffer;

@TestInstance(Lifecycle.PER_CLASS)
class DebugScopeWindowTest {

    Display display;

    @BeforeAll
    void initialize() {
        display = Display.getDefault();
    }

    @AfterAll
    void terminate() {
        display.dispose();
    }

    @Test
    void testProcessSample() {
        DebugScopeWindow subject = new DebugScopeWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("SAMPLES 10"));
        subject.update(new KeywordIterator("'TEST' 0 100"));

        Assertions.assertEquals(10, subject.samples);
        Assertions.assertEquals(0, subject.sampleIndex);
        Assertions.assertEquals(0, subject.sampleCount);

        subject.processSample(1);
        Assertions.assertEquals(1, subject.channelData[0].sampleData[0]);
        subject.processSample(2);
        Assertions.assertEquals(2, subject.channelData[0].sampleData[1]);
        subject.processSample(3);
        Assertions.assertEquals(3, subject.channelData[0].sampleData[2]);
        subject.processSample(4);
        Assertions.assertEquals(4, subject.channelData[0].sampleData[3]);

        Assertions.assertEquals(4, subject.sampleIndex);
        Assertions.assertEquals(4, subject.sampleCount);
    }

    @Test
    void testProcessSampleWrapAround() {
        DebugScopeWindow subject = new DebugScopeWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("SAMPLES 10"));
        subject.update(new KeywordIterator("'TEST' 0 100"));

        Assertions.assertEquals(10, subject.samples);
        Assertions.assertEquals(0, subject.sampleIndex);
        Assertions.assertEquals(0, subject.sampleCount);

        subject.processSample(10);
        subject.processSample(11);
        subject.processSample(12);
        subject.processSample(13);
        subject.processSample(14);

        subject.processSample(0);
        subject.processSample(1);
        subject.processSample(2);
        subject.processSample(3);
        subject.processSample(4);
        subject.processSample(5);
        subject.processSample(6);

        Assertions.assertEquals(5, subject.channelData[0].sampleData[0]);
        Assertions.assertEquals(6, subject.channelData[0].sampleData[1]);
        Assertions.assertEquals(12, subject.channelData[0].sampleData[2]);
        Assertions.assertEquals(13, subject.channelData[0].sampleData[3]);
        Assertions.assertEquals(14, subject.channelData[0].sampleData[4]);
        Assertions.assertEquals(0, subject.channelData[0].sampleData[5]);
        Assertions.assertEquals(1, subject.channelData[0].sampleData[6]);
        Assertions.assertEquals(2, subject.channelData[0].sampleData[7]);
        Assertions.assertEquals(3, subject.channelData[0].sampleData[8]);
        Assertions.assertEquals(4, subject.channelData[0].sampleData[9]);

        Assertions.assertEquals(2, subject.sampleIndex);
        Assertions.assertEquals(subject.samples, subject.sampleCount);
    }

    @Test
    void testChannelUpdate() {
        DebugScopeWindow subject = new DebugScopeWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("SAMPLES 10 SIZE 40 20"));
        subject.update(new KeywordIterator("'TEST' 0 10"));

        subject.processSample(0);
        subject.processSample(1);
        subject.processSample(2);
        subject.processSample(3);

        subject.channelData[0].update();

        Assertions.assertEquals(subject.sampleCount * 2, subject.channelData[0].linePoints.length);

        int idx = 0;
        int expectedX = DebugScopeWindow.MARGIN_WIDTH + (subject.samples - subject.sampleCount) * 4;
        int expectedY = DebugScopeWindow.MARGIN_HEIGHT + subject.charHeight + (subject.imageSize.y - subject.channelData[0].y_base);
        while (idx < subject.channelData[0].linePoints.length) {
            Assertions.assertEquals(expectedX, subject.channelData[0].linePoints[idx++]);
            Assertions.assertEquals(expectedY, subject.channelData[0].linePoints[idx++]);
            expectedX += 4;
            expectedY -= 2;
        }
    }

    @Test
    void testFullChannelUpdate() {
        DebugScopeWindow subject = new DebugScopeWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("SAMPLES 10 SIZE 40 20"));
        subject.update(new KeywordIterator("'TEST' 0 10"));

        for (int i = 0; i < subject.samples; i++) {
            subject.processSample(i);
        }

        subject.channelData[0].update();

        Assertions.assertEquals(subject.sampleCount * 2, subject.channelData[0].linePoints.length);

        int idx = 0;
        int expectedX = DebugScopeWindow.MARGIN_WIDTH + (subject.samples - subject.sampleCount) * 4;
        int expectedY = DebugScopeWindow.MARGIN_HEIGHT + subject.charHeight + (subject.imageSize.y - subject.channelData[0].y_base);
        while (idx < subject.channelData[0].linePoints.length) {
            Assertions.assertEquals(expectedX, subject.channelData[0].linePoints[idx++]);
            Assertions.assertEquals(expectedY, subject.channelData[0].linePoints[idx++]);
            expectedX += 4;
            expectedY -= 2;
        }
    }

    @Test
    void testChannelUpdateWrapAround() {
        DebugScopeWindow subject = new DebugScopeWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("SAMPLES 10 SIZE 40 20"));
        subject.update(new KeywordIterator("'TEST' 0 10"));

        subject.processSample(9);
        subject.processSample(9);
        subject.processSample(9);
        subject.processSample(9);
        for (int i = 0; i < subject.samples; i++) {
            subject.processSample(i);
        }

        subject.channelData[0].update();

        Assertions.assertEquals(subject.sampleCount * 2, subject.channelData[0].linePoints.length);

        int idx = 0;
        int expectedX = DebugScopeWindow.MARGIN_WIDTH + (subject.samples - subject.sampleCount) * 4;
        int expectedY = DebugScopeWindow.MARGIN_HEIGHT + subject.charHeight + (subject.imageSize.y - subject.channelData[0].y_base);
        while (idx < subject.channelData[0].linePoints.length) {
            Assertions.assertEquals(expectedX, subject.channelData[0].linePoints[idx++]);
            Assertions.assertEquals(expectedY, subject.channelData[0].linePoints[idx++]);
            expectedX += 4;
            expectedY -= 2;
        }
    }

}
