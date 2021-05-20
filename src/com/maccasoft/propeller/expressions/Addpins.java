package com.maccasoft.propeller.expressions;

public class Addpins extends BinaryOperator {

    public Addpins(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        return (term1.getNumber().intValue() & 0x3F) | ((term2.getNumber().intValue() & 0x1F) << 6);
    }

    @Override
    public String getLexeme() {
        return "addpins";
    }

}
