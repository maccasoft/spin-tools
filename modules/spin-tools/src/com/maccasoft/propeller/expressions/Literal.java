package com.maccasoft.propeller.expressions;

public abstract class Literal extends Expression {

    @Override
    public boolean isConstant() {
        return true;
    }

}
