package com.maccasoft.propeller.expressions;

public class Float extends Function {

    public Float(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        return term.getNumber().doubleValue();
    }

    @Override
    public String getLexeme() {
        return "float";
    }

}
