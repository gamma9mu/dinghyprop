package cs412.dinghyprop;

import cs412.dinghyprop.simulator.Simulator;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface for the server.
 */
public interface IMaster extends Remote {

    /**
     * Obtain a copy of the evaluation simulators.
     * @return  An array of the simulators to use to evaluate programs
     * @throws RemoteException
     */
    Simulator[] getEvaluationSimulators() throws RemoteException;

    /**
     * Register an evaluation client to receive programs to evaluate.
     * @param client    The client instance
     * @throws RemoteException
     */
    void registerClient(IClient client) throws RemoteException;
}
