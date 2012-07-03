package cs412.dinghyprop.interpreter;

/**
 * For parsing errors.
 */
public class ParsingException extends Exception {
    private static final long serialVersionUID = -5447089429268408948L;

    public ParsingException(String s) {
        super(s);
    }
}
