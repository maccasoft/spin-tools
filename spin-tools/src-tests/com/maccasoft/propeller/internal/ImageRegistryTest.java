/*
 * Copyright (c) 2021-25 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.maccasoft.propeller.internal;

import org.eclipse.swt.graphics.Image;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.maccasoft.propeller.internal.ImageRegistry.FilePair;

class ImageRegistryTest {

    @Test
    void testBrowserImageFiles() {
        for (FilePair pair : ImageRegistry.fileMap.values()) {
            Image image = ImageRegistry.getImageFromResources(pair.file);
            Assertions.assertNotNull(image, "Missing " + pair.file + " image file");
            image.dispose();

            image = ImageRegistry.getImageFromResources(pair.altFile);
            Assertions.assertNotNull(image, "Missing " + pair.altFile + " image file");
            image.dispose();
        }
    }

}
