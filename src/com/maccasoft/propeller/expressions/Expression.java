package com.maccasoft.propeller.expressions;

public abstract class Expression {

    public Expression resolve() {
        return this;
    }

    public boolean isConstant() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public Number getNumber() {
        throw new EvaluationException("Not a number.");
    }

    public boolean isString() {
        return false;
    }

    public String getString() {
        throw new EvaluationException("Not a string.");
    }

    public boolean isGroup() {
        return false;
    }

    public Expression getElement() {
        return getElement(0);
    }

    public Expression getElement(int index) {
        return index == 0 ? this : null;
    }

    public Expression getNext() {
        return null;
    }

}
