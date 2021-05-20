package com.maccasoft.propeller.expressions;

public class HubContextLiteral extends Literal {

    private final Context context;

    public HubContextLiteral(Context context) {
        this.context = context;
    }

    @Override
    public Number getNumber() {
        return context.getHubAddress();
    }

    @Override
    public String toString() {
        return Long.toString(getNumber().longValue());
    }

}
