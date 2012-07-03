package cs412.dinghyprop.interpreter;

/**
 * Tracks a token through parsing.
 */
public final class Token {
    public static enum TYPE { PAREN_OPEN, PAREN_CLOSE, SYMBOL, NUMBER, EOF }

    public TYPE type;
    public String text;

    public Token(TYPE type, String text) {
        this.type = type;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Token{type=" + type + ", text='" + text + "\'}";
    }
}
