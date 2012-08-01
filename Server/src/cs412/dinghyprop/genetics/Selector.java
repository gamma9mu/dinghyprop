/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.genetics;

/**
 * Specifies the interface of program selection methods.
 */
public interface Selector {

    /**
     * Selects a program from a population.
     * @param population    the population to select from
     * @return  one program from the population
     */
    Program select(Program[] population);
}
