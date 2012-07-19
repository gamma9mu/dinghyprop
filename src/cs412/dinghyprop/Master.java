package cs412.dinghyprop;

import cs412.dinghyprop.genetics.GeneticProgram;
import cs412.dinghyprop.simulator.Simulator;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * GP master server
 */
public class Master extends UnicastRemoteObject implements IMaster {
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
    private List<ISlave> slaves = new ArrayList<ISlave>(10);

    /**
     * Create a new master object.
     * @param simulators    The simulation environments to supply to slaves
     * @throws RemoteException
     */
    public Master(GeneticProgram geneticProgram, Simulator[] simulators)
            throws RemoteException {
        super();
        this.geneticProgram = geneticProgram;
        this.simulators = simulators;
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

    /**
     * Register a new {@code Master} with RMI.
     * @param args    ignored
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Naming.rebind("//localhost/Master", new Master(null, null));
    }
}
