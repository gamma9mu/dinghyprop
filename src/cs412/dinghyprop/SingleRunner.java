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
 * Non-distributed GP runner.
 */
public class SingleRunner {
    private static Logger log = Logger.getLogger("SingleRunner");
    private static final int popSize = 100;
    private static final int SIM_DIM = 20;
    private static final int GOAL = 300;
    private GeneticProgram gp;
    private ISimulator simulator;
    private boolean success = false;
    private int best = Integer.MIN_VALUE;
    private File checkpointDir;

    /**
     * Create a new single-machine GP runner.
     * @param gp    The GP object to run
     */
    public SingleRunner(GeneticProgram gp) {
        this.gp = gp;
        gp.initialize();
        simulator = new SimulatorRandom(SIM_DIM, SIM_DIM, 10).getSimulator();
        String checkpointDirName = "gp_" + new Date().toString().replace(' ', '_');
        checkpointDir = new File(checkpointDirName);
        if (!checkpointDir.mkdir()) {
            log.warning("Could not create checkpoint directory: "
                    + checkpointDirName + "\nCheckpointing disabled.");
        }
    }

    /**
     * Run the genetic program through 1000 generations or until success.
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
     * Write the current generation to a file.
     * @param generationIndex    The generation number or -1 for final
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
     * Evaluate all the individuals in a population.
     */
    private void runGeneration() {
        int fitnesses = 0;
        int maxFitness = 0;
        for (int i = 0; i < popSize; i++) {
            Program program = gp.getProgram(i);
            int fitness = evaluateProgram(program);
            gp.setProgramFitness(i, fitness);

            fitnesses += fitness;
            maxFitness = (fitness > maxFitness) ? fitness : maxFitness;
        }
        System.out.println("Max: " + maxFitness
                + "\tAvg: " + (fitnesses / popSize));
        if (maxFitness >= GOAL) {
            success = true;
            best = maxFitness;
        }
    }

    /**
     * Evaluate a single program in a randomly generated environment.
     * @param program    The program to evaluate
     * @return  The evaluated program's fitness
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
     * Print the text of the programs with the best fitness.
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
     * Runs a GP population through 1000 generations.
     * @param args    One argument: the tournament size.
     */
    public static void main(String[] args) {
        GeneticProgram gp = new GeneticProgram(popSize,
                GeneticProgram.INIT_POP_METHOD.RHALF_AND_HALF, 10);

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
     * Run a GP instance.
     * @param gp    The GP to run
     */
    public static void run(GeneticProgram gp) {
        SingleRunner sr = new SingleRunner(gp);
        sr.run();
        sr.printBest();
    }
}
