package com.maccasoft.propeller.expressions;

public class NumberLiteral extends Literal {

    public static final NumberLiteral ZERO = new NumberLiteral(0);
    public static final NumberLiteral ONE = new NumberLiteral(1);

    private final Number value;
    private final int base;
    private String text;

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
        this.text = s;
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
        if (text == null) {
            switch (base) {
                case 2:
                    text = "%" + Long.toBinaryString(value.longValue());
                    break;
                case 4:
                    text = "%%" + Long.toString(value.longValue(), 4);
                    break;
                case 16:
                    text = "$" + Long.toHexString(value.longValue());
                    break;
                default:
                    text = value.toString();
                    break;
            }
        }
        return text;
    }

}
