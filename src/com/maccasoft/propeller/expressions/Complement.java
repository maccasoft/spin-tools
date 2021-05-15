package com.maccasoft.propeller.expressions;

public class Complement extends UnaryOperator {

    public Complement(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        return ~term.getNumber().intValue();
    }

    @Override
    public String getLexeme() {
        return "~";
    }

}
