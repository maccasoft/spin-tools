package com.maccasoft.propeller.expressions;

public class Type extends Passthrough {

    private final String type;
    private final Expression term;

    public Type(String type, Expression term) {
        this.type = type;
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public Expression getTerm() {
        return term;
    }

    @Override
    public Expression resolve() {
        return term;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public String toString() {
        return type + " " + term;
    }

}
