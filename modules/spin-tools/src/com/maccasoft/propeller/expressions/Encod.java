package com.maccasoft.propeller.expressions;

public class Encod extends UnaryOperator {

    public Encod(Expression term) {
        super(term);
    }

    @Override
    public Number getNumber() {
        long v = term.getNumber().longValue();
        for (long l = 31, b = 1L << 31; l >= 0; l--, b >>= 1) {
            if ((v & b) != 0) {
                return l;
            }
        }
        return 0;
    }

    @Override
    public String getLexeme() {
        return "encod";
    }

}
