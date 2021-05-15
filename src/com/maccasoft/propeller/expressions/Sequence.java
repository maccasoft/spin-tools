package com.maccasoft.propeller.expressions;

import java.util.List;

public class Sequence extends BinaryOperator {

    public Sequence(Expression value, Expression tail) {
        super(value, tail);
    }

    public Expression getValue() {
        return term1;
    }

    public Expression getTail() {
        return term2;
    }

    @Override
    protected void addToList(List<Expression> list) {
        term1.addToList(list);
        Expression tail = term2;
        while (tail != null) {
            tail.getElement().addToList(list);
            tail = tail.getNext();
        }
    }

    @Override
    public Expression getElement(int index) {
        return index == 0 ? term1 : term2.getElement(index - 1);
    }

    @Override
    public Expression getNext() {
        return term2;
    }

    @Override
    public String getLexeme() {
        return ",";
    }

    @Override
    public String toString() {
        return "" + term1 + ", " + term2;
    }

}
