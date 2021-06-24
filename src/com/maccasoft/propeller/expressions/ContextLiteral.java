package com.maccasoft.propeller.expressions;

public class ContextLiteral extends Literal {

    private final Context context;

    public ContextLiteral(Context context) {
        this.context = context;
    }

    @Override
    public Number getNumber() {
        return Long.valueOf(context.getAddress());
    }

    @Override
    public String toString() {
        return Long.toString(getNumber().longValue());
    }

}
