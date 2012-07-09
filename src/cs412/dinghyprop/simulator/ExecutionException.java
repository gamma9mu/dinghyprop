package cs412.dinghyprop.simulator;

/**
 * Base class for errors in the simulator.
 */
public class ExecutionException extends Exception {
    private static final long serialVersionUID = -4445915370545158615L;

    public ExecutionException(String message) {
        super(message);
    }
}
