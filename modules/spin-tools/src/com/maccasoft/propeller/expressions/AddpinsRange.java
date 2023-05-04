package com.maccasoft.propeller.expressions;

public class AddpinsRange extends BinaryOperator {

    public AddpinsRange(Expression term1, Expression term2) {
        super(term1, term2);
    }

    @Override
    public Number getNumber() {
        long t1 = term1.getNumber().longValue() & 0x3F;
        long t2 = term2.getNumber().longValue() & 0x3F;
        return t2 | (((t1 - t2) & 0x1F) << 6);
    }

    @Override
    public String getLexeme() {
        return "..";
    }

}
