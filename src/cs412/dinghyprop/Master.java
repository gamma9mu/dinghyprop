package cs412.dinghyprop;

import cs412.dinghyprop.genetics.GeneticProgram;
import cs412.dinghyprop.genetics.IPopulationObserver;
import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.simulator.ISimulator;
import cs412.dinghyprop.simulator.SimulatorRandom;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GP master server
 */
public class Master extends UnicastRemoteObject implements IMaster, IPopulationObserver, Runnable {
    private static final long serialVersionUID = 7213091562277551698L;

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

    /*
     * Status for program dispatcher.
     */
    private boolean running = true;

    /*
     * Statistics
     */
    int best;
    int worst;
    Program leader = null;

    /**
     * Create a new master object.
     * @param simulators    The simulation environments to supply to clients
     * @throws RemoteException
     */
    public Master(GeneticProgram geneticProgram, ISimulator[] simulators, int generations)
            throws RemoteException {
        this.geneticProgram = geneticProgram;
        this.simulators = simulators;
        this.generations = generations;
        programsRemaining = geneticProgram.getPopulationSize();
        resetStatistics();
    }

    /**
     * Register with RMI and run the GP.
     * @throws MalformedURLException
     * @throws RemoteException
     */
    public synchronized void runGP() throws MalformedURLException, RemoteException {
        geneticProgram.addPopulationObserver(this);
        Naming.rebind(address, this);
        geneticProgram.initialize();
        leader = geneticProgram.getProgram(0); // A safe default

        new Thread(this).start();

        int targetFitness = 300 * simulators.length;
        for (int i = 1; i <= generations; i++) {
            while (programsRemaining > 0)
                try { wait(); } catch (InterruptedException ignored) { }

            if (best >= targetFitness)
                break;

            System.out.println("Creating generation #" + Integer.toString(i));
            programsRemaining = geneticProgram.getPopulationSize();
            resetStatistics();
            geneticProgram.createNextGeneration();
        }
        running = false;
    }

    /**
     * Evaluate all the individuals in a population.
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
     * Create a thread to handle I/O with a client and evaluate a program.
     * @param index      The program's index
     * @param program    The program itself
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
     * "Callback" for evaluation threads that manages fitness update
     * bookkeeping.
     * @param index      The index of the program to update
     * @param fitness    The fitness to assign the program
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
        return leader;
    }

    @Override
    public void registerClient(IClient client) throws RemoteException {
        enqueueClient(client);
    }

    /**
     * Enqueue a client for processing, handling notification.
     * @param client    The {@code IClient} to enqueue
     */
    private synchronized void enqueueClient(IClient client) {
        clients.add(client);
        notifyAll();
    }

    /**
     * Obtain the next available {@code IClient}.
     * @return  The {@code IClient} that has been waiting the longest
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
     * Enqueue a program, handling notification.
     * @param index         The index of the program
     * @param individual    The program itself
     */
    private synchronized void enqueueProgram(int index, Program individual) {
        pendingPrograms.add(new IndexedProgram(index, individual));
        notifyAll();
    }

    /**
     * Obtain the next program due for processing.
     * @return  A {@code Map.Entry&lt;Integer,Program&gt;} where the key is the
     * index and the value is the {@code Program} to be processed
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
     * Update the statistics fields with new fitness information.
     * @param index      The index of the program the fitness refers to
     * @param fitness    The latest returned fitness
     */
    private void updateStatistic(int index, int fitness) {
        if (fitness < worst)
            worst = fitness;
        if (fitness > best) {
            best = fitness;
            leader = geneticProgram.getProgram(index);
        }
    }

    /**
     * Reset the statistics fields.
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
     * Register a new {@code Master} with RMI.
     * @param args    ignored
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        GeneticProgram gp = new GeneticProgram(100,
                GeneticProgram.INIT_POP_METHOD.RHALF_AND_HALF, 3);
        ISimulator[] simulators = {new SimulatorRandom(10, 10, 6).getSimulator(),
                new SimulatorRandom(10, 10, 6).getSimulator(),
                new SimulatorRandom(10, 10, 6).getSimulator()
        };
        new Master(gp, simulators, 1000).runGP();
    }

    /**
     * Manages a program and its index.
     */
    class IndexedProgram {
        public int index;

        public Program program;
        /**
         * Create a new {@code IndexedProgram}.
         * @param index      The program's index
         * @param program    The program itself
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
