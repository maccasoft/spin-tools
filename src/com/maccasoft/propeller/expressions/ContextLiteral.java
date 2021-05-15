package com.maccasoft.propeller.expressions;

public class ContextLiteral extends Literal {

    private final Context context;

    public ContextLiteral(Context context) {
        this.context = context;
    }

    @Override
    public Number getNumber() {
        return new Integer(context.getAddress());
    }

    @Override
    public String toDebugString() {
        return "";
    }

}
