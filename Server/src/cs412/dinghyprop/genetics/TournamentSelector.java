/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.genetics;

import java.security.SecureRandom;

/**
 * Performs n-size tournament selection.
 */
public final class TournamentSelector implements Selector {
    /**
     * The size of selection tournaments
     */
    private int tournamentSize;

    /**
     * A good RNG
     */
    private SecureRandom rand = new SecureRandom();

    // Register as a Selector
    static {
        try {
            CheckpointLoader.registerSelector("TournamentSelector",
                    TournamentSelector.class.getConstructor(new Class[]{Integer.TYPE}));
        } catch (NoSuchMethodException e) { e.printStackTrace(); } // should not happen
    }

    /**
     * Creates a new tournament selector.
     *
     * @param tournamentSize    the size of the tournaments (minimum: 2)
     */
    public TournamentSelector(int tournamentSize) {
        this.tournamentSize = tournamentSize;
        if (this.tournamentSize < 2) {
            this.tournamentSize = 2;
        }
    }

    @Override
    public Program select(Program[] population) {
        int popSize = population.length;
        int size = (tournamentSize > popSize) ? popSize : tournamentSize;
        Program[] programs = new Program[size];

        for (int i = 0; i < size; i++) {
            programs[i] = population[rand.nextInt(popSize)];
        }

        int max = programs[0].fitness;
        Program winner = programs[0];
        for (int i = 1; i < size; i++) {
            if (programs[i].fitness > max) {
                max = programs[i].fitness;
                winner = programs[i];
            }
        }

        return winner;
    }

    @Override
    public String toString() {
        return "TournamentSelector(" + tournamentSize + ')';
    }
}
