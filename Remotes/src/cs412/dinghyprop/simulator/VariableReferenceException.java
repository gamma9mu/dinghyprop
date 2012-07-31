/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

/**
 * Exception class for errors caused during simulator variables referencing
 */
public class VariableReferenceException extends ExecutionException {
    private static final long serialVersionUID = 6152561853669535551L;

    /**
     * @param variableName    The name of the variable being referenced when
     *                        the error occurred
     */
    public VariableReferenceException(String variableName) {
        super("Error referencing variable: \"" + variableName + '"');
    }
}
