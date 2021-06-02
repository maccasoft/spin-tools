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

    public NumberLiteral(String s) {
        if (s.startsWith("%%")) {
            s = s.substring(2);
            this.base = 4;
        }
        else if (s.startsWith("%")) {
            s = s.substring(1);
            this.base = 2;
        }
        else if (s.startsWith("$")) {
            s = s.substring(1);
            this.base = 16;
        }
        else {
            this.base = 10;
        }
        if (s.contains(".")) {
            this.value = Double.parseDouble(s.replace("_", ""));
        }
        else {
            this.value = Long.parseLong(s.replace("_", ""), this.base);
        }
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
                return "%" + Long.toBinaryString(value.longValue());
            case 4:
                return "%%" + Long.toString(value.longValue(), 4);
            case 16:
                return "$" + Long.toHexString(value.longValue());
        }
        return value.toString();
    }

}
