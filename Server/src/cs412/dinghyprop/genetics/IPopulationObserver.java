package cs412.dinghyprop.genetics;

/**
 * Interface for objects which want to be notified of the creation of
 * individuals by a {@code GeneticProgram}.
 */
public interface IPopulationObserver {
    /**
     * Notifies the observer that a new individual was created.
     * @param index         The GP's index of the individual
     * @param individual    The individual itself
     */
    void individualCreated(int index, Program individual);
}
