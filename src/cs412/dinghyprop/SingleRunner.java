package cs412.dinghyprop;

import cs412.dinghyprop.genetics.GeneticProgram;
import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.genetics.TournamentSelector;
import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.interpreter.ParsingException;
import cs412.dinghyprop.simulator.Simulator;
import cs412.dinghyprop.simulator.SimulatorRandom;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Non-distributed GP runner.
 */
public class SingleRunner {
    private static Logger log = Logger.getLogger("SingleRunner");
    private static final int popSize = 100;
    private static final int SIM_DIM = 20;
    private static final int GOAL = 300;
    private GeneticProgram gp;
    private Simulator simulator;
    private boolean success = false;
    private int best;

    /**
     * Create a new single-machine GP runner.
     * @param gp    The GP object to run
     */
    public SingleRunner(GeneticProgram gp) {
        this.gp = gp;
        simulator = new SimulatorRandom(SIM_DIM, SIM_DIM, 10).getSimulator();
    }

    /**
     * Evaluate all the individuals in a population.
     */
    private void runGeneration() {
        int fitnesses = 0;
        int maxFitness = 0;
        for (int i = 0; i < popSize; i++) {
            Program program = gp.getProgram(i);
            int fitness = evaluateProgram(program);
            fitnesses += fitness;
            maxFitness = (fitness > maxFitness) ? fitness : maxFitness;
        }
        System.out.println("Max: " + maxFitness
                + "\t Avg: " + (fitnesses / popSize));
        if (maxFitness >= GOAL) {
            success = true;
            best = maxFitness;
            return;
        }
        gp.createNextGeneration();
    }

    /**
     * Evaluate a single program in a randomly generated environment.
     * @param program    The program to evaluate
     * @return  The evaluated program's fitness
     */
    private int evaluateProgram(Program program) {
        Simulator sim;
        try {
            sim = simulator.clone();
        } catch (CloneNotSupportedException ignored) {
            return 0;
        }

        int fitness = 0;
        try {
            Interpreter interpreter = new Interpreter(sim, program.program);
            interpreter.run(100);
            fitness = interpreter.getFitness();
        } catch (ParsingException e) {
            log.log(Level.WARNING, "Program failed to compile or run.", e);
            log.log(Level.WARNING, program.toString());
        }

        program.fitness = fitness;
        return fitness;
    }

    @Override
    public String toString() {
        return "SingleRunner{gp=" + gp + '}';
    }

    /**
     * Print the text of the programs with the best fitness.
     */
    private void printBest() {
        System.out.println("Programs with best fitness [" + best + "]:");
        for (int i = 0; i < gp.getPopulationSize(); i++) {
            Program program = gp.getProgram(i);
            if (program.fitness == best) {
                System.out.println(program.program + '\n');
            }
        }
    }

    /**
     * Runs a GP population through 1000 generations.
     * @param args    One argument: the tournament size.
     */
    public static void main(String[] args) {
        GeneticProgram gp = new GeneticProgram(popSize,
                GeneticProgram.INIT_POP_METHOD.RHALF_AND_HALF, 10);
        if (args.length == 1) {
            try {
                int tournamentSize = Integer.parseInt(args[0]);
                gp.setSelector(new TournamentSelector(tournamentSize));
            } catch (NumberFormatException nfe) {
                System.err.println(nfe.getLocalizedMessage());
                System.err.println("Usage: SingleRunner [tournament_size]");
                System.exit(0);
            }
        } else {
            gp.setSelector(new TournamentSelector(4));
        }
        SingleRunner sr = new SingleRunner(gp);

        for (int iter = 0; iter < 1000; iter++) {
            System.out.println("Iteration: " + iter);
            sr.runGeneration();
            if (sr.success)
                break;
        }
        sr.printBest();
    }
}
