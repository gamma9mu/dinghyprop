package cs412.dinghyprop.genetics;

/**
 * Specifies the interface of program selection methods.
 */
public interface Selector {
    Program select(Program[] population);
}
