package com.maccasoft.propeller.expressions;

import java.util.ArrayList;
import java.util.List;

public abstract class Expression {

    public Expression resolve() {
        return this;
    }

    public boolean isRegister() {
        return false;
    }

    public int getRegister() {
        throw new EvaluationException("Not a register.");
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

    public int getHubAddress() {
        return getNumber().intValue();
    }

    public int getCogAddress() {
        return getNumber().intValue();
    }

    public List<Expression> getList() {
        List<Expression> list = new ArrayList<>();
        addToList(list);
        return list;
    }

    protected void addToList(List<Expression> list) {
        list.add(this);
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
