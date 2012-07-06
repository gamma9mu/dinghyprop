package cs412.dinghyprop.genetics;

import java.security.SecureRandom;

/**
 * Performs n-size tournament selection.
 */
public class TournamentSelector implements Selector {
    private int tournamentSize;
    private SecureRandom rand = new SecureRandom();

    /**
     * Create a new tournament selector for a given tournament size.
     * @param tournamentSize    The size of the tournaments (minimum: 2)
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
            Program selected = population[rand.nextInt(popSize)];
            programs[i] = selected;
        }

        int max = -1;
        Program winner = null;
        for (int i = 0; i < size; i++) {
            if (programs[i].fitness > max) {
                max = programs[i].fitness;
                winner = programs[i];
            }
        }

        return winner;
    }

    @Override
    public String toString() {
        return "TournamentSelector{tournamentSize=" + tournamentSize + '}';
    }
}
