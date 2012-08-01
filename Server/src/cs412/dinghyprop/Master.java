/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import cs412.dinghyprop.genetics.CheckpointLoader;
import cs412.dinghyprop.genetics.GeneticProgram;
import cs412.dinghyprop.genetics.IPopulationObserver;
import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.simulator.ISimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GeneticProgram RMI server
 */
public class Master extends UnicastRemoteObject implements IMaster, IPopulationObserver, Runnable {
    private static final long serialVersionUID = 7213091562277551698L;
    private static Logger log = Logger.getLogger("Master");

    /**
     * The population size to use for fresh GP runs
     */
    private static final int POPULATION_SIZE = 10000;

    /**
     * The number of generations to evaluate before termination
     */
    private static final int GENERATIONS = 100000;

    /**
     * The GP for this run
     */
    private transient GeneticProgram geneticProgram;

    /**
     * The simulation environments of the GP run
     */
    private ISimulator[] simulators;

    /**
     * The list of registered {@code IClient}s
     */
    private final transient Queue<IClient> clients = new ConcurrentLinkedQueue<IClient>();

    /**
     * Programs that are pending evaluation
     */
    private final transient Queue<IndexedProgram> pendingPrograms =
            new ConcurrentLinkedQueue<IndexedProgram>();

    /**
     * The number of programs that still require evaluation (GP's generation
     * size less the evaluated program count)
     */
    private int programsRemaining;

    /**
     * The number of generations to run
     */
    private int generations;

    /**
     * The RMI address on which to listen
     */
    private static final String address = "Master";

    /**
     * Status for program dispatcher.
     */
    private volatile boolean running = true;

    /**
     * The best fitness seen from the current generation
     */
    private int best;

    /**
     * The worst fitness seen from the current generation
     */
    private int worst;

    /**
     * The current generations best-yet program
     */
    private Program frontRunner = null;

    /**
     * Previous generation's leader
     */
    private Program leader = null;

    /**
     * Checkpointing directory
     */
    private File checkpointDir;

    /**
     * File numbering for checkpoints
     */
    private int checkpointFileIndex = 0;

    /**
     * Creates a new server object.
     *
     * @param simulators    the simulation environments to supply to clients
     * @throws RemoteException inherited from superclass constructors
     */
    public Master(GeneticProgram geneticProgram, ISimulator[] simulators, int generations)
            throws RemoteException {
        super(54614);
        this.geneticProgram = geneticProgram;
        this.simulators = simulators;
        this.generations = generations;
        programsRemaining = geneticProgram.getPopulationSize();
        resetStatistics();

        // setup checkpointing
        String checkpointDirName = "gp_" + new Date().toString().replace(' ', '_');
        checkpointDir = new File(checkpointDirName);
        if (!checkpointDir.mkdir()) {
            log.warning("Could not create checkpoint directory: "
                    + checkpointDirName + "\nCheckpointing disabled.");
        }
    }

    /**
     * Register with RMI and run the GP.
     *
     * @throws RemoteException if a problem occurs in RMI initialization
     */
    public synchronized void runGP() throws RemoteException {
        initRMI();
        initGP();

        log.info("Starting program dispatcher");
        new Thread(this).start();

        int targetFitness = 0;
        for (ISimulator simulator : simulators)
            targetFitness += simulator.getTerminationFitness();

        for (int i = 1; i <= generations; i++) {
            while (programsRemaining > 0)
                try { wait(); } catch (InterruptedException ignored) { }

            if (best >= targetFitness) {
                log.info("Successful individual found.");
                break;
            }

            resetStatistics();
            leader = frontRunner;

            if (i % 5 == 0) {
                log.info("Writing checkpoint");
                writeCheckpoint(String.format("gen_%08d", checkpointFileIndex));
                checkpointFileIndex++;
            }

            log.info("Creating generation #" + Integer.toString(i));
            programsRemaining = geneticProgram.getPopulationSize();
            geneticProgram.createNextGeneration();
            log.info("Created  generation #" + Integer.toString(i));
        }

        cleanup();
    }

    /**
     * Registers this server with RMI.
     *
     * @throws RemoteException if the RMI registry could not be located or the
     * server could not be bound to its address
     */
    private void initRMI() throws RemoteException {
        log.info("Registering with RMI");
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind(address, this);
    }

    /**
     * Setup GP related state.
     */
    private void initGP() {
        log.info("Initializing GP");
        geneticProgram.addPopulationObserver(this);
        geneticProgram.initialize();
        frontRunner = geneticProgram.getProgram(0); // A safe default
    }

    /**
     * Releases clients, stops the program dispatch thread and writes a final
     * checkpoint.
     *
     * @throws RemoteException for RMI errors while releasing clients
     */
    private void cleanup() throws RemoteException {
        log.info("GP run complete.");
        running = false;

        log.info("Releasing clients.");
        for (IClient client : clients)
            client.release();
        clients.clear();

        writeCheckpoint("final_generation");
    }

    /**
     * Dispatches programs for evaluation.
     */
    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                while (pendingPrograms.isEmpty())
                    try { wait(); } catch (InterruptedException ignored) { }
            }
            IndexedProgram entry = getNextProgram();
                sendForEvaluation(entry.index, entry.program);
        }
    }

    /**
     * Writes a checkpoint file.
     *
     * @param filename    the file path to write to
     */
    private void writeCheckpoint(String filename) {
        File file = new File(checkpointDir, filename);
        try {
            geneticProgram.savePopulation(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            log.log(Level.WARNING, "Exception in dump", e);
        }
    }

    /**
     * Creates a thread to handle I/O with a client.
     *
     * @param index      the index of the program to send to the client
     * @param program    the program itself
     */
    private void sendForEvaluation(final int index, final Program program) {
        final IClient client = getNextClient();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int fitness = client.evaluateProgram(program.program);
                    enqueueClient(client);
                    updateFitness(index, fitness);
                } catch (RemoteException ignored) {
                    enqueueProgram(index, program);
                }
            }
        });
        t.start();
    }

    /**
     * Updates the fitness of a program with the GeneticProgram.
     *
     * @param index      the index of the program to update
     * @param fitness    the fitness to assign the program
     */
    private synchronized void updateFitness(int index, int fitness) {
        geneticProgram.setProgramFitness(index, fitness);
        programsRemaining--;
        updateStatistic(index, fitness);
        notifyAll();
    }

    @Override
    public ISimulator[] getEvaluationSimulators() throws RemoteException {
        return simulators;
    }

    @Override
    public Program getCurrentLeader() throws RemoteException {
        if (frontRunner == null)
            return new Program("");
        if (leader == null)
            return frontRunner;
        return leader;
    }

    @Override
    public void registerClient(IClient client) throws RemoteException {
        enqueueClient(client);
    }

    /**
     * Enqueues a client for processing, handling notification.
     *
     * @param client    the IClient to enqueue
     */
    private synchronized void enqueueClient(IClient client) {
        clients.add(client);
        notifyAll();
    }

    /**
     * @return the next available IClient (that has been waiting the longest)
     */
    private synchronized IClient getNextClient() {
        while (clients.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ignored) { }
        }
        return clients.poll();
    }

    @Override
    public void individualCreated(int index, Program individual) {
        enqueueProgram(index, individual);
    }

    /**
     * Enqueues a program, handling notification.
     *
     * @param index         the index of the program
     * @param individual    the program itself
     */
    private synchronized void enqueueProgram(int index, Program individual) {
        pendingPrograms.add(new IndexedProgram(index, individual));
        notifyAll();
    }

    /**
     * Obtains the next program due for processing.
     *
     * @return  A {@code Map.Entry&lt;Integer,Program&gt;} where the key is the
     * index and the value is the program to be processed
     */
    private synchronized IndexedProgram getNextProgram() {
        while (pendingPrograms.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ignored) { }
        }
        return pendingPrograms.poll();
    }

    /**
     * Updates the statistics fields with new fitness information.
     *
     * @param index      the index of a program
     * @param fitness    the program's fitness
     */
    private void updateStatistic(int index, int fitness) {
        if (fitness < worst)
            worst = fitness;
        if (fitness > best) {
            best = fitness;
            frontRunner = geneticProgram.getProgram(index);
        }
    }

    /**
     * Resets the statistics fields.
     */
    private void resetStatistics() {
        worst = Integer.MAX_VALUE;
        best = Integer.MIN_VALUE;
    }

    @Override
    public String toString() {
        return "Master @ " + address;
    }

    /**
     * Registers a new server with RMI.
     * <p>
     * This entry point accepts one required and one option argument.  The
     * required argument is the directory path to the simulator files and it
     * appears first.  It is optionally followed by the path to a checkpoint
     * directory, which (if specified) will load the last version of that GP
     * and continue it.
     *
     * @param args    the directory path where simulator files are stored, and,
     *                optionally, a checkpoint directory
     * @throws Exception if RMI problems occur when creating the server or
     * registering it with the RMI registry
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: Master <simulation_directory> "
                    + "[checkpoint_directory]");
            System.exit(-1);
        }

        SimulationDirLoader sdl = new SimulationDirLoader(args[0]);
        ISimulator[] simulators = sdl.load();

        GeneticProgram gp;
        if (args.length == 2) {
            gp = loadCheckpoint(args[1]);
        } else {
            gp = new GeneticProgram(POPULATION_SIZE,
                    GeneticProgram.INIT_POP_METHOD.RHALF_AND_HALF, 3);
        }

        new Master(gp, simulators, GENERATIONS).runGP();
    }

    /**
     * Loads a checkpoint of a genetic program.
     *
     * @param arg    the path to the checkpoint directory
     * @return  the loaded genetic program, or null on error
     */
    private static GeneticProgram loadCheckpoint(String arg) {
        CheckpointLoader cpl = new CheckpointLoader(arg);
        GeneticProgram gp = cpl.instantiate();
        if (gp == null) {
            System.err.println("Could not load checkpoint.");
            System.exit(-1);
        }
        return gp;
    }

    /**
     * Manages a program and its index.
     */
    class IndexedProgram {
        /**
         * The GeneticProgram's index for the program
         */
        public int index;

        /**
         * The program itself
         */
        public Program program;

        /**
         * Creates a new IndexedProgram.
         *
         * @param index      the program's index
         * @param program    the program itself
         */
        public IndexedProgram(int index, Program program) {
            this.index = index;
            this.program = program;
        }

        @Override
        public String toString() {
            return "IndexedProgram{index=" + index + ", program=" + program + '}';
        }
    }
}
