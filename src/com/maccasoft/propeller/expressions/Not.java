package com.maccasoft.propeller.expressions;

public class Not extends UnaryOperator {

    public Not(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        return ~term.getNumber().intValue();
    }

    @Override
    public String getLexeme() {
        return "!";
    }

}
