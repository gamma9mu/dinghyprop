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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Non-distributed version of the DinghyProp genetic programming.
 */
public class SingleRunner extends UnicastRemoteObject implements IMaster {
    private static final long serialVersionUID = 8558983861071255805L;
    private static Logger log = Logger.getLogger("SingleRunner");

    /**
     * The population size of the GeneticProgram
     */
    private static final int popSize = 100;

    /**
     * The goal fitness
     */
    private int goal;

    /**
     * The running GeneticProgram
     */
    private transient GeneticProgram gp;

    /**
     * The simulators to use when evaluating programs
     */
    private ISimulator[] simulators;

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
     * @throws RemoteException inherited
     */
    public SingleRunner(GeneticProgram gp, ISimulator[] simulators) throws RemoteException {
        super(54614);

        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("Master", this);

        this.gp = gp;
        gp.initialize();
        this.simulators = simulators;

        goal = 0;
        for (ISimulator simulator : simulators)
            goal += simulator.getTerminationFitness();

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
            maxFitness = (fitness > maxFitness) ? fitness : maxFitness;
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
        int fitness = 0;
        for (ISimulator simulator : simulators) {
            ISimulator sim;
            try {
                sim = simulator.clone();
            } catch (CloneNotSupportedException ignored) {
                return 0;
            }

            try {
                Interpreter interpreter = new Interpreter(sim, program.program);
                interpreter.run(100);
                fitness += interpreter.getFitness();
            } catch (ParsingException e) {
                log.log(Level.WARNING, "Program failed to compile or run.", e);
                log.log(Level.WARNING, program.toString());
            }
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
    public ISimulator[] getEvaluationSimulators() throws RemoteException {
        return simulators;
    }

    @Override
    public void registerClient(IClient client) throws RemoteException {
    }

    @Override
    public Program getCurrentLeader() throws RemoteException {
        return bestProgram;
    }

    @Override
    public String toString() {
        return "SingleRunner{gp=" + gp + '}';
    }

    /**
     * Runs a GeneticProgram through 1000 generations.
     *
     * @param args    one optional argument: -t followed by the tournament size
     *                one required argument: the simulation spec directory path
     */
    public static void main(String[] args) {
        GeneticProgram gp = new GeneticProgram(popSize,
                GeneticProgram.INIT_POP_METHOD.RHALF_AND_HALF, 5);

        SimulationDirLoader sdl = null;
        if (args.length == 3 && args[0].compareTo("-t") == 0) {
            try {
                int tournamentSize = Integer.parseInt(args[1]);
                gp.setSelector(new TournamentSelector(tournamentSize));
            } catch (NumberFormatException nfe) {
                System.err.println(nfe.getLocalizedMessage());
                usage();
            }
            sdl = new SimulationDirLoader(args[2]);
        } else if (args.length == 1) {
            sdl = new SimulationDirLoader(args[0]);
        } else {
            usage();
        }

        run(gp, sdl.load());
    }

    /**
     * Creates and runs a SingleRunner instance.
     *
     * @param gp    the GeneticProgram to use
     */
    public static void run(GeneticProgram gp, ISimulator[] simulators) {
        try {
            SingleRunner sr = new SingleRunner(gp, simulators);
            sr.run();
            sr.printBest();
        } catch (RemoteException e) {
            log.throwing("SingleRunner", "run", e);
        }
    }

    /**
     * Prints usage message and exits with an error return.
     */
    private static void usage() {
        System.err.println("Usage: SingleRunner [-t <tournament_size>] <simulation_dir>");
        System.exit(-1);
    }
}
