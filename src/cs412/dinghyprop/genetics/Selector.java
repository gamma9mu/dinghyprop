package cs412.dinghyprop.genetics;

/**
 * Specifies the interface of program selection methods.
 */
public interface Selector {
    /**
     * Select a program from a population.
     * @param population    The population to select from
     * @return  One program from the population
     */
    Program select(Program[] population);
}
