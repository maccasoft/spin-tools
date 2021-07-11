package com.maccasoft.propeller.expressions;

public class ContextLiteral extends Literal {

    private final Context context;

    public ContextLiteral(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public boolean isConstant() {
        return false;
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
