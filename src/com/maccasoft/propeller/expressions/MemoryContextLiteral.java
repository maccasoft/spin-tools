package com.maccasoft.propeller.expressions;

public class MemoryContextLiteral extends Literal {

    private final Context context;

    public MemoryContextLiteral(Context context) {
        this.context = context;
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
