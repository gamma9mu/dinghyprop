/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.genetics;

/**
 * Interface for objects which want to be notified of the creation of
 * individuals by a {@link GeneticProgram}.
 */
public interface IPopulationObserver {

    /**
     * Notifies the observer that a new individual was created.
     *
     * @param index         the GeneticProgram's index of the individual
     * @param individual    the individual itself
     */
    void individualCreated(int index, Program individual);
}
