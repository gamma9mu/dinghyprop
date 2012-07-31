/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

/**
 * Exception class for errors caused by invoking nonexistent functions in the
 * simulator.
 */
public class UnknownFunctionException extends ExecutionException {
    private static final long serialVersionUID = -2305982772820403687L;

    /**
     * @param functionName    The name of the function whose invocation was
     *                        attempted
     */
    public UnknownFunctionException(String functionName) {
        super("Unknown function called: \"" + functionName + '"');
    }
}
