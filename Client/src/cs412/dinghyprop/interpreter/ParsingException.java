package cs412.dinghyprop.interpreter;

/**
 * For parsing errors.
 */
public final class ParsingException extends Exception {
    private static final long serialVersionUID = -5447089429268408948L;

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
