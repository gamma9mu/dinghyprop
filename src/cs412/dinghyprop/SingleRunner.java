/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import cs412.dinghyprop.genetics.GeneticProgram;
import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.genetics.TournamentSelector;
import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.interpreter.ParsingException;
import cs412.dinghyprop.simulator.ISimulator;
import cs412.dinghyprop.simulator.SimulatorRandom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Non-distributed version of the DinghyProp genetic programming.
 */
public class SingleRunner {
    private static Logger log = Logger.getLogger("SingleRunner");

    /**
     * The population size of the GeneticProgram
     */
    private static final int popSize = 100;

    /**
     * The dimensions of the simulation environments to create
     */
    private static final int SIM_DIM = 20;

    /**
     * The goal fitness
     */
    private int goal;

    /**
     * The running GeneticProgram
     */
    private GeneticProgram gp;

    /**
     * The simulator to use when evaluating programs
     */
    private ISimulator simulator;

    /**
     * Whether an individual with the goal fitness has been found
     */
    private boolean success = false;

    /**
     * The best seen fitness score
     */
    private int best = Integer.MIN_VALUE;

    /**
     * The best program seen
     */
    private Program bestProgram = new Program("(+ 0 0)");

    /**
     * The directory to write checkpoints into
     */
    private File checkpointDir;

    /**
     * Creates a new single-machine GP runner.
     *
     * @param gp    the GP object to run
     */
    public SingleRunner(GeneticProgram gp) {
        this.gp = gp;
        gp.initialize();
        simulator = new SimulatorRandom(SIM_DIM, SIM_DIM, 10).getSimulator();
        goal = simulator.getTerminationFitness();
        String checkpointDirName = "gp_" + new Date().toString().replace(' ', '_');
        checkpointDir = new File(checkpointDirName);
        if (!checkpointDir.mkdir()) {
            log.warning("Could not create checkpoint directory: "
                    + checkpointDirName + "\nCheckpointing disabled.");
        }
    }

    /**
     * Runs the genetic program through 1000 generations or until success.
     */
    private void run() {
        for (int iter = 0; iter < 1000; iter++) {
            System.out.print("Generation: " + iter + '\t');
            runGeneration();
            if (iter % 5 == 0)
                dump(iter);
            if (success)
                break;
            gp.createNextGeneration();
        }
        if (success)
            dump(-1);
    }

    /**
     * Writes the current generation to a file.
     *
     * @param generationIndex    the generation number or -1 for final
     *                           generation.
     */
    private void dump(int generationIndex) {
        File file;
        if (generationIndex == -1)
            file = new File(checkpointDir, "final_generation");
        else
            file = new File(checkpointDir, "gen_"
                + String.format("%04d", generationIndex));

        try {
            gp.savePopulation(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, "Exception in dump", e);
        }
    }

    /**
     * Evaluates all the individuals in a population.
     */
    private void runGeneration() {
        int fitnesses = 0;
        int maxFitness = 0;
        for (int i = 0; i < popSize; i++) {
            Program program = gp.getProgram(i);
            int fitness = evaluateProgram(program);
            gp.setProgramFitness(i, fitness);

            if (fitness > maxFitness) {
                best = fitness;
                bestProgram = program;
            }

            fitnesses += fitness;
        }
        System.out.println("Max: " + maxFitness
                + "\tAvg: " + (fitnesses / popSize));
        if (best >= goal) {
            success = true;
        }
    }

    /**
     * Evaluates a single program in a randomly generated environment.
     *
     * @param program    the program to evaluate
     * @return  the evaluated program's fitness
     */
    private int evaluateProgram(Program program) {
        ISimulator sim;
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

        return fitness;
    }

    /**
     * Prints the text of the programs with the best fitness.
     */
    private void printBest() {
        System.out.println("Programs with best fitness [" + best + "]:");
        for (int i = 0; i < gp.getPopulationSize(); i++) {
            Program program = gp.getProgram(i);
            if (program.getFitness() >= best) {
                System.out.println(program.toString() + '\n');
            }
        }
    }

    @Override
    public String toString() {
        return "SingleRunner{gp=" + gp + '}';
    }

    /**
     * Runs a GeneticProgram through 1000 generations.
     *
     * @param args    one optional argument: the tournament size.
     */
    public static void main(String[] args) {
        GeneticProgram gp = new GeneticProgram(popSize,
                GeneticProgram.INIT_POP_METHOD.RHALF_AND_HALF, 5);

        int tournamentSize = 4;
        if (args.length == 1) {
            try {
                tournamentSize = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.err.println(nfe.getLocalizedMessage());
                System.err.println("Usage: SingleRunner [tournament_size]");
                System.exit(0);
            }
        }

        gp.setSelector(new TournamentSelector(tournamentSize));

        run(gp);
    }

    /**
     * Creates and runs a SingleRunner instance.
     *
     * @param gp    the GeneticProgram to use
     */
    public static void run(GeneticProgram gp) {
        SingleRunner sr = new SingleRunner(gp);
        sr.run();
        sr.printBest();
    }
}
