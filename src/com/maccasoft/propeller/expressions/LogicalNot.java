package com.maccasoft.propeller.expressions;

public class LogicalNot extends UnaryOperator {

    public LogicalNot(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        return term.getNumber().intValue() == 0 ? -1 : 0;
    }

    @Override
    public String getLexeme() {
        return "!!";
    }

}
