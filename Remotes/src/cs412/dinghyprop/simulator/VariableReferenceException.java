package cs412.dinghyprop.simulator;

/**
 * Exception for errors reference simulator variables.
 */
public class VariableReferenceException extends ExecutionException {
    private static final long serialVersionUID = 6152561853669535551L;

    public VariableReferenceException(String variableName) {
        super("Error referencing variable: \"" + variableName + '"');
    }
}
