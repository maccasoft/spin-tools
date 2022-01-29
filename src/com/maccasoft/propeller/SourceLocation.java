package com.maccasoft.propeller;

import java.io.File;

public class SourceLocation {

    public String name;
    public int offset;
    public int topPixel;
    public File file;

    public SourceLocation(String name, File file, int offset, int topPixel) {
        this.name = name;
        this.file = file;
        this.offset = offset;
        this.topPixel = topPixel;
    }

}
