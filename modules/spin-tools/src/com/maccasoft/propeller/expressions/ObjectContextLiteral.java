package com.maccasoft.propeller.expressions;

public class ObjectContextLiteral extends Literal {

    private final Context context;
    private final String type;

    public ObjectContextLiteral(Context context) {
        this.context = context;
        this.type = "BYTE";
    }

    public ObjectContextLiteral(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    public String getType() {
        return type;
    }

    @Override
    public Number getNumber() {
        return Long.valueOf(context.getObjectAddress());
    }

    public Number getMemoryAddress() {
        return context.getMemoryAddress();
    }

    @Override
    public String toString() {
        return Long.toString(getNumber().longValue()) + "@" + Long.toString(context.getMemoryAddress());
    }

}
