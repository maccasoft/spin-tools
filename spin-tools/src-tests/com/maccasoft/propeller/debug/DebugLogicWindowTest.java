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
class DebugLogicWindowTest {

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
        DebugLogicWindow subject = new DebugLogicWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("TEST SAMPLES 10"));

        Assertions.assertEquals(10, subject.sampleData.length);
        Assertions.assertEquals(0, subject.sampleIndex);
        Assertions.assertEquals(0, subject.sampleCount);

        subject.processSample(1);
        Assertions.assertEquals(1, subject.sampleData[0]);
        subject.processSample(2);
        Assertions.assertEquals(2, subject.sampleData[1]);
        subject.processSample(3);
        Assertions.assertEquals(3, subject.sampleData[2]);
        subject.processSample(4);
        Assertions.assertEquals(4, subject.sampleData[3]);

        Assertions.assertEquals(4, subject.sampleIndex);
        Assertions.assertEquals(4, subject.sampleCount);
    }

    @Test
    void testProcessSampleWrapAround() {
        DebugLogicWindow subject = new DebugLogicWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("TEST SAMPLES 10"));

        Assertions.assertEquals(10, subject.sampleData.length);
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

        Assertions.assertEquals(5, subject.sampleData[0]);
        Assertions.assertEquals(6, subject.sampleData[1]);
        Assertions.assertEquals(12, subject.sampleData[2]);
        Assertions.assertEquals(13, subject.sampleData[3]);
        Assertions.assertEquals(14, subject.sampleData[4]);
        Assertions.assertEquals(0, subject.sampleData[5]);
        Assertions.assertEquals(1, subject.sampleData[6]);
        Assertions.assertEquals(2, subject.sampleData[7]);
        Assertions.assertEquals(3, subject.sampleData[8]);
        Assertions.assertEquals(4, subject.sampleData[9]);

        Assertions.assertEquals(2, subject.sampleIndex);
        Assertions.assertEquals(subject.sampleData.length, subject.sampleCount);
    }

    @Test
    void testChannelUpdate() {
        DebugLogicWindow subject = new DebugLogicWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("SAMPLES 10 'TEST'"));

        subject.processSample(0);
        subject.processSample(1);
        subject.processSample(2);
        subject.processSample(3);

        subject.channelData[0].update(subject.imageSize.y);

        Assertions.assertEquals(subject.sampleCount * 4, subject.channelData[0].linePoints.length);

        int idx = 0;
        int expectedX = DebugLogicWindow.MARGIN_WIDTH * 2 + subject.textWidth + (subject.sampleData.length - subject.sampleCount) * subject.spacing;
        int expectedY0 = subject.imageSize.y + subject.charHeight;
        int expectedY1 = subject.imageSize.y;
        int expectedY = expectedY0;
        while (idx < subject.channelData[0].linePoints.length) {
            Assertions.assertEquals(expectedX, subject.channelData[0].linePoints[idx++]);
            Assertions.assertEquals(expectedY, subject.channelData[0].linePoints[idx++]);
            expectedX += subject.spacing;
            Assertions.assertEquals(expectedX, subject.channelData[0].linePoints[idx++]);
            Assertions.assertEquals(expectedY, subject.channelData[0].linePoints[idx++]);
            expectedY = expectedY == expectedY0 ? expectedY1 : expectedY0;
        }
    }

    @Test
    void testChannelUpdateWrapAround() {
        DebugLogicWindow subject = new DebugLogicWindow(new CircularBuffer(16));
        subject.create();
        subject.setup(new KeywordIterator("SAMPLES 10 'TEST'"));

        subject.processSample(9);
        subject.processSample(9);
        subject.processSample(9);
        subject.processSample(9);
        for (int i = 0; i < subject.sampleData.length; i++) {
            subject.processSample(i);
        }

        subject.channelData[0].update(subject.imageSize.y);

        Assertions.assertEquals(subject.sampleCount * 4, subject.channelData[0].linePoints.length);

        int idx = 0;
        int expectedX = DebugLogicWindow.MARGIN_WIDTH * 2 + subject.textWidth + (subject.sampleData.length - subject.sampleCount) * subject.spacing;
        int expectedY0 = subject.imageSize.y + subject.charHeight;
        int expectedY1 = subject.imageSize.y;
        int expectedY = expectedY0;
        while (idx < subject.channelData[0].linePoints.length) {
            Assertions.assertEquals(expectedX, subject.channelData[0].linePoints[idx++]);
            Assertions.assertEquals(expectedY, subject.channelData[0].linePoints[idx++]);
            expectedX += subject.spacing;
            Assertions.assertEquals(expectedX, subject.channelData[0].linePoints[idx++]);
            Assertions.assertEquals(expectedY, subject.channelData[0].linePoints[idx++]);
            expectedY = expectedY == expectedY0 ? expectedY1 : expectedY0;
        }
    }

}
