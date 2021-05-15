package com.maccasoft.propeller.expressions;

public class CharacterLiteral extends Literal {

    private final char character;

    public CharacterLiteral(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number getNumber() {
        return new Integer(character);
    }

    @Override
    public String toString() {
        String escaped = Character.toString(character);
        escaped = escaped.replace("\\", "\\\\");
        escaped = escaped.replace("\'", "\\\'");
        escaped = escaped.replace("\0", "\\0");
        escaped = escaped.replace("\7", "\\a");
        escaped = escaped.replace("\t", "\\t");
        escaped = escaped.replace("\n", "\\n");
        escaped = escaped.replace("\f", "\\f");
        escaped = escaped.replace("\r", "\\r");
        escaped = escaped.replace("\33", "\\e");
        return "'" + escaped + "'";
    }

}
