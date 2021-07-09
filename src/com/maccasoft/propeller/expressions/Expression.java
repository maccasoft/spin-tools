package com.maccasoft.propeller.expressions;

import java.util.HashMap;
import java.util.Map;

public abstract class Expression {

    protected Object data;
    protected Map<String, Object> keyedData = new HashMap<String, Object>();

    public Expression resolve() {
        return this;
    }

    public boolean isConstant() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public Number getNumber() {
        throw new RuntimeException("Not a number.");
    }

    public boolean isString() {
        return false;
    }

    public String getString() {
        throw new RuntimeException("Not a string.");
    }

    public boolean isGroup() {
        return false;
    }

    public Expression getElement() {
        return getElement(0);
    }

    public Expression getElement(int index) {
        return index == 0 ? this : null;
    }

    public Expression getNext() {
        return null;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData(String key) {
        return keyedData.get(key);
    }

    public void setData(String key, Object data) {
        this.keyedData.put(key, data);
    }

}
