package com.maccasoft.propeller;

import java.io.File;

public class SourceLocation {

    public String name;
    public int offset;
    public int topIndex;
    public File file;

    public SourceLocation(String name, File file, int offset, int topIndex) {
        this.name = name;
        this.file = file;
        this.offset = offset;
        this.topIndex = topIndex;
    }

}
