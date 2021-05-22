package com.maccasoft.propeller.expressions;

public class Sqrt extends Function {

    public Sqrt(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        if (term.getNumber() instanceof Double) {
            return Math.sqrt(term.getNumber().doubleValue());
        }
        return Math.sqrt(term.getNumber().doubleValue());
    }

    @Override
    public String getLexeme() {
        return "sqrt";
    }

}
