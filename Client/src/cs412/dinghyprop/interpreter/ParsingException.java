/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.interpreter;

/**
 * Exception class for errors that occur while parsing generated programs
 */
public final class ParsingException extends Exception {
    private static final long serialVersionUID = -5447089429268408948L;

    /**
     * @param message    a description of the cause
     */
    public ParsingException(String message) {
        super(message);
    }

    /**
     * @param message    a description of the cause (context)
     * @param cause      the exception that cause parsing to fail
     */
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
