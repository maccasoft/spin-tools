/*
 * Copyright (c) 2021-24 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package com.maccasoft.propeller.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class ImageRegistry {

    private static final Map<Object, Image> map = new HashMap<Object, Image>();

    ImageRegistry() {

    }

    public static Image getImage(Object key) {
        return map.get(key);
    }

    public static void setImage(Object key, Image image) {
        Image oldImage = map.get(key);
        map.put(key, image);
        if (oldImage != null) {
            oldImage.dispose();
        }
    }

    public static void removeImage(Object key) {
        Image oldImage = map.get(key);
        map.remove(key);
        if (oldImage != null) {
            oldImage.dispose();
        }
    }

    public static void dispose() {
        for (Image image : map.values()) {
            image.dispose();
        }
        map.clear();
    }

    public static Image getImageFromResources(String name) {
        if (map.get(name) == null) {
            InputStream is = ImageRegistry.class.getResourceAsStream(name);
            ImageLoader loader = new ImageLoader();
            ImageData[] data = loader.load(is);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            map.put(name, new Image(Display.getDefault(), data[0]));
        }
        return map.get(name);
    }

    private static final Map<String, String> fileMap = new HashMap<>();
    static {
        fileMap.put(".asm", "document-text.png");
        fileMap.put(".bat", "document-text.png");
        fileMap.put(".bin", "document-binary.png");
        fileMap.put(".binary", "document-binary.png");
        fileMap.put(".bmp", "document-image.png");
        fileMap.put(".c", "document-code.png");
        fileMap.put(".cpp", "document-text.png");
        fileMap.put(".dll", "document-binary.png");
        fileMap.put(".exe", "document-binary.png");
        fileMap.put(".gif", "document-image.png");
        fileMap.put(".gz", "document-zipper.png");
        fileMap.put(".h", "document-text.png");
        fileMap.put(".img", "document-binary.png");
        fileMap.put(".java", "document-text.png");
        fileMap.put(".jpg", "document-image.png");
        fileMap.put(".json", "document-text.png");
        fileMap.put(".lib", "document-binary.png");
        fileMap.put(".log", "document-text.png");
        fileMap.put(".lst", "document-text.png");
        fileMap.put(".o", "document-binary.png");
        fileMap.put(".p2asm", "document-text.png");
        fileMap.put(".pdf", "document-pdf.png");
        fileMap.put(".png", "document-image.png");
        fileMap.put(".rom", "document-binary.png");
        fileMap.put(".s", "document-text.png");
        fileMap.put(".sh", "document-text.png");
        fileMap.put(".spin", "document-code.png");
        fileMap.put(".spin2", "document-code.png");
        fileMap.put(".tga", "document-image.png");
        fileMap.put(".tar", "document-zipper.png");
        fileMap.put(".txt", "document-text.png");
        fileMap.put(".zip", "document-zipper.png");
    }

    public static Image getImageForFile(File file) {
        if (file.isDirectory()) {
            return getImageFromResources("folder.png");
        }
        int index = file.getName().lastIndexOf('.');
        if (index != -1) {
            String ext = file.getName().substring(index);
            String imageName = fileMap.get(ext.toLowerCase());
            if (imageName != null) {
                return getImageFromResources(imageName);
            }
        }
        return getImageFromResources("document.png");
    }

}
