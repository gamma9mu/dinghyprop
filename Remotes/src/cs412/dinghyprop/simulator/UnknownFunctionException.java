/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

/**
 * Exception for errors invoking nonexistent functions in the simulator.
 */
public class UnknownFunctionException extends ExecutionException {
    private static final long serialVersionUID = -2305982772820403687L;

    public UnknownFunctionException(String functionName) {
        super("Unknown function called: \"" + functionName + '"');
    }
}
