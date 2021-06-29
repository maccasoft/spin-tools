package com.maccasoft.propeller.expressions;

public class ObjectContextLiteral extends Literal {

    private final Context context;

    public ObjectContextLiteral(Context context) {
        this.context = context;
    }

    @Override
    public Number getNumber() {
        return context.getObjectOffset();
    }

    @Override
    public String toString() {
        return Long.toString(getNumber().longValue());
    }

}
