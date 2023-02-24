package com.maccasoft.propeller;

import java.io.File;

public class SourceLocation {

    public int offset;
    public int topPixel;
    public File file;

    public SourceLocation(File file, int offset, int topPixel) {
        this.file = file;
        this.offset = offset;
        this.topPixel = topPixel;
    }

}
