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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class PngImageTransfer extends ByteArrayTransfer {

    private static final String IMAGE_PNG = "image/png";
    private static final int ID = registerType(IMAGE_PNG);

    private static PngImageTransfer _instance = new PngImageTransfer();

    private PngImageTransfer() {
    }

    public static PngImageTransfer getInstance() {
        return _instance;
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] {
            IMAGE_PNG
        };
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] {
            ID
        };
    }

    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        if (object == null || !(object instanceof ImageData)) {
            return;
        }

        if (isSupportedType(transferData)) {
            ImageData image = (ImageData) object;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
                // write data to a byte array and then ask super to convert to pMedium

                ImageLoader imgLoader = new ImageLoader();
                imgLoader.data = new ImageData[] {
                    image
                };
                imgLoader.save(out, SWT.IMAGE_PNG);

                byte[] buffer = out.toByteArray();
                out.close();

                super.javaToNative(buffer, transferData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Object nativeToJava(TransferData transferData) {
        if (isSupportedType(transferData)) {

            byte[] buffer = (byte[]) super.nativeToJava(transferData);
            if (buffer == null) {
                return null;
            }

            try (ByteArrayInputStream in = new ByteArrayInputStream(buffer)) {
                return new ImageData(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
