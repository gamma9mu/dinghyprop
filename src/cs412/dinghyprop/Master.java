package cs412.dinghyprop;

import cs412.dinghyprop.genetics.GeneticProgram;
import cs412.dinghyprop.genetics.IPopulationObserver;
import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.simulator.Simulator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GP master server
 */
public class Master extends UnicastRemoteObject implements IMaster, IPopulationObserver {
    private static final long serialVersionUID = 7213091562277551698L;

    /**
     * The GP for this run
     */
    private GeneticProgram geneticProgram;

    /**
     * The simulation environments of the GP run
     */
    private Simulator[] simulators;

    /**
     * The list of registered {@code ISlave}s
     */
    private final Queue<ISlave> slaves = new ConcurrentLinkedQueue<ISlave>();

    /**
     * Programs that are pending evaluation
     */
    private final Map<Integer, Program> pendingPrograms =
            new HashMap<Integer, Program>(10);

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
    public void run() throws MalformedURLException, RemoteException {
        geneticProgram.addPopulationObserver(this);
        Naming.rebind("//localhost/Master", this);
        for (int i = 0; i < generations; i++) {
            runGeneration();
            geneticProgram.createNextGeneration();
        }
    }

    /**
     * Evaluate all the individuals in a population.
     */
    private void runGeneration() {
        programsRemaining = geneticProgram.getPopulationSize();
        while (programsRemaining > 0) {
            synchronized (pendingPrograms) {
                while (pendingPrograms.isEmpty()) {
                    try {
                        pendingPrograms.wait();
                    } catch (InterruptedException ignored) { }
                }
                Map.Entry<Integer, Program> entry =
                        pendingPrograms.entrySet().iterator().next();
                Program program = geneticProgram.getProgram(entry.getKey());
                sendForEvaluation(entry.getKey(), program);
            }
        }
    }

    /**
     * Create a thread to handle I/O with a client and evaluate a program.
     * @param index      The program's index
     * @param program    The program itself
     */
    private void sendForEvaluation(final int index, final Program program) {
        final ISlave client;
        synchronized (slaves) {
            client = slaves.poll();
            while (client == null) {
                try {
                    slaves.wait();
                } catch (InterruptedException ignored) { }
                slaves.poll();
            }
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int fitness = client.evaluateProgram(program.program);
                    slaves.add(client);
                    updateFitness(index, fitness);
                } catch (RemoteException ignored) {
                    // put the program back in the pending list on a failure
                    pendingPrograms.put(index, program);
                    pendingPrograms.notifyAll();
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
    private void updateFitness(int index, int fitness) {
        geneticProgram.setProgramFitness(index, fitness);
        programsRemaining--;
        pendingPrograms.notifyAll();
    }

    @Override
    public Simulator[] getEvaluationSimulators() throws RemoteException {
        return simulators;
    }

    @Override
    public void registerSlave(ISlave slave) throws RemoteException {
        if (! slaves.contains(slave))
            slaves.add(slave);
    }

    @Override
    public void individualCreated(int index, Program individual) {
        pendingPrograms.put(index, individual);
    }

    /**
     * Register a new {@code Master} with RMI.
     * @param args    ignored
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new Master(null, null, 1000).run();
    }
}
