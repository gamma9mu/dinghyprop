package cs412.dinghyprop;

import cs412.dinghyprop.genetics.GeneticProgram;
import cs412.dinghyprop.genetics.IPopulationObserver;
import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.simulator.Simulator;
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
    private Simulator[] simulators;

    /**
     * The list of registered {@code ISlave}s
     */
    private final transient Queue<ISlave> slaves = new ConcurrentLinkedQueue<ISlave>();

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
    private String address = "//localhost/Master";

    /*
     * Status for program dispatcher.
     */
    private boolean running = true;

    /**
     * Create a new master object.
     * @param simulators    The simulation environments to supply to slaves
     * @throws RemoteException
     */
    public Master(GeneticProgram geneticProgram, Simulator[] simulators, int generations)
            throws RemoteException {
        super();
        this.geneticProgram = geneticProgram;
        this.simulators = simulators;
        this.generations = generations;
        programsRemaining = geneticProgram.getPopulationSize();
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

        new Thread(this).start();

        for (int i = 0; i < generations; i++) {
            while (programsRemaining > 0)
                try { wait(); } catch (InterruptedException ignored) { }
            System.out.println("Creating next generation.");
            programsRemaining = geneticProgram.getPopulationSize();
            geneticProgram.createNextGeneration();
        }
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
        final ISlave client = getNextSlave();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int fitness = client.evaluateProgram(program.program);
                    enqueueSlave(client);
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
        notifyAll();
    }

    @Override
    public Simulator[] getEvaluationSimulators() throws RemoteException {
        return simulators;
    }

    @Override
    public void registerSlave(ISlave slave) throws RemoteException {
        enqueueSlave(slave);
    }

    /**
     * Enqueue a slave for processing, handling notification.
     * @param slave    The {@code ISlave} to enqueue
     */
    private synchronized void enqueueSlave(ISlave slave) {
        slaves.add(slave);
        notifyAll();
    }

    /**
     * Obtain the next available {@code ISlave}.
     * @return  The {@code ISlave} that has been waiting the longest
     */
    private synchronized ISlave getNextSlave() {
        while (slaves.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ignored) { }
        }
        return slaves.poll();
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
        Simulator[] simulators = {new SimulatorRandom(10, 10, 6).getSimulator(),
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
