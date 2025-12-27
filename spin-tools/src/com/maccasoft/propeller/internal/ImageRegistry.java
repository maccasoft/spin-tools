/*
 * Copyright (c) 2021-2025 Marco Maccaferri and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
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

    static class FilePair {
        String file;
        String altFile;

        public FilePair(String file, String altFile) {
            this.file = file;
            this.altFile = altFile;
        }

    }

    static final Map<String, FilePair> fileMap = new HashMap<>();
    static {
        fileMap.put(".asm", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".bat", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".bin", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".binary", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".bmp", new FilePair("document-image.png", "blue-document-image.png"));
        fileMap.put(".c", new FilePair("document-code.png", "blue-document-code.png"));
        fileMap.put(".cpp", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".dll", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".exe", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".gif", new FilePair("document-image.png", "blue-document-image.png"));
        fileMap.put(".gz", new FilePair("document-zipper.png", "blue-document-zipper.png"));
        fileMap.put(".h", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".img", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".java", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".jpg", new FilePair("document-image.png", "blue-document-image.png"));
        fileMap.put(".json", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".lib", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".log", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".lst", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".o", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".obj", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".pasm", new FilePair("document-code.png", "blue-document-code.png"));
        fileMap.put(".p2asm", new FilePair("document-code.png", "blue-document-code.png"));
        fileMap.put(".pdf", new FilePair("document-pdf.png", "blue-document-pdf.png"));
        fileMap.put(".png", new FilePair("document-image.png", "blue-document-image.png"));
        fileMap.put(".rom", new FilePair("document-binary.png", "blue-document-binary.png"));
        fileMap.put(".s", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".sh", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".spin", new FilePair("document-code.png", "blue-document-code.png"));
        fileMap.put(".spin2", new FilePair("document-code.png", "blue-document-code.png"));
        fileMap.put(".tga", new FilePair("document-image.png", "blue-document-image.png"));
        fileMap.put(".tar", new FilePair("document-zipper.png", "blue-document-zipper.png"));
        fileMap.put(".txt", new FilePair("document-text.png", "blue-document-text.png"));
        fileMap.put(".zip", new FilePair("document-zipper.png", "blue-document-zipper.png"));

        fileMap.put(".spin-object", new FilePair("document-code-local.png", "document-code-library.png"));
        fileMap.put(".spin2-object", new FilePair("document-code-local.png", "document-code-library.png"));
    }

    public static Image getImageForFile(File file) {
        return getImageForFile(file, false);
    }

    public static Image getImageForFile(File file, boolean alt) {
        if (file.isDirectory()) {
            return getImageFromResources("folder.png");
        }
        int index = file.getName().lastIndexOf('.');
        if (index != -1) {
            String ext = file.getName().substring(index).toLowerCase();
            FilePair imageNamePair = fileMap.get(ext);
            if (imageNamePair != null) {
                return getImageFromResources(alt ? imageNamePair.altFile : imageNamePair.file);
            }
        }
        return getImageFromResources("document.png");
    }

    public static Image getImageForFile(String name, boolean alt) {
        int index = name.lastIndexOf('.');
        if (index != -1) {
            String ext = name.substring(index).toLowerCase();
            FilePair imageNamePair = fileMap.get(ext);
            if (imageNamePair != null) {
                return getImageFromResources(alt ? imageNamePair.altFile : imageNamePair.file);
            }
        }
        return getImageFromResources("document.png");
    }

}
