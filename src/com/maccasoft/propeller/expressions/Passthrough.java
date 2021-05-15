package com.maccasoft.propeller.expressions;

public abstract class Passthrough extends Expression {

    @Override
    public boolean isNumber() {
        return resolve().isNumber();
    }

    @Override
    public Number getNumber() {
        return resolve().getNumber();
    }

    @Override
    public boolean isString() {
        return resolve().isString();
    }

    @Override
    public String getString() {
        return resolve().getString();
    }

    @Override
    public boolean isGroup() {
        return resolve().isGroup();
    }

}
