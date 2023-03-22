package com.maccasoft.propeller.expressions;

public class Defined extends Literal {

    final Context context;
    final String identifier;

    public Defined(String identifier, Context context) {
        this.context = context;
        this.identifier = identifier;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Number getNumber() {
        return Long.valueOf(context.hasSymbol(identifier) ? 1 : 0);
    }

    @Override
    public String toString() {
        return Long.toString(getNumber().longValue());
    }

}
