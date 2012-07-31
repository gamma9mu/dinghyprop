/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

/**
 * Base class for exceptions occurring in the simulator.
 */
public class ExecutionException extends Exception {
    private static final long serialVersionUID = -4445915370545158615L;

    /**
     * @param message    A description of the cause
     */
    public ExecutionException(String message) {
        super(message);
    }
}
