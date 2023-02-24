package com.maccasoft.propeller.expressions;

public class MemoryContextLiteral extends Literal {

    private final Context context;
    private final String type;

    public MemoryContextLiteral(Context context) {
        this.context = context;
        this.type = "BYTE";
    }

    public MemoryContextLiteral(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public Number getNumber() {
        return context.getMemoryAddress();
    }

    @Override
    public String toString() {
        return Long.toString(getNumber().longValue());
    }

}
