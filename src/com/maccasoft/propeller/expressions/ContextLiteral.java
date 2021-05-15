package com.maccasoft.propeller.expressions;

public class ContextLiteral extends Literal {

    private final Context context;

    public ContextLiteral(Context context) {
        this.context = context;
    }

    @Override
    public Number getNumber() {
        return context.getAddress();
    }

    @Override
    public String toString() {
        return Integer.toString(getNumber().intValue());
    }

}
