package com.maccasoft.propeller.expressions;

public class NumberLiteral extends Literal {

    public static final NumberLiteral ZERO = new NumberLiteral(0);
    public static final NumberLiteral ONE = new NumberLiteral(1);

    private final Number value;
    private final int base;

    public NumberLiteral(Number value) {
        this.value = value;
        this.base = 10;
    }

    public NumberLiteral(Number value, int base) {
        this.value = value;
        this.base = base;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number getNumber() {
        return value;
    }

    public int getBase() {
        return base;
    }

    @Override
    public String toString() {
        switch (base) {
            case 2:
                return "%" + Integer.toBinaryString(value.intValue());
            case 4:
                return "%%" + Integer.toString(value.intValue(), 4);
            case 16:
                return "$" + Integer.toHexString(value.intValue());
        }
        return value.toString();
    }

}
