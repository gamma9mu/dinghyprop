package cs412.dinghyprop;

import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.simulator.ISimulator;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface for the server.
 */
public interface IMaster extends Remote, Serializable {

    /**
     * Obtain a copy of the evaluation simulators.
     * @return  An array of the simulators to use to evaluate programs
     * @throws RemoteException
     */
    ISimulator[] getEvaluationSimulators() throws RemoteException;

    /**
     * Register an evaluation client to receive programs to evaluate.
     * @param client    The client instance
     * @throws RemoteException
     */
    void registerClient(IClient client) throws RemoteException;

    /**
     * Obtain a copy of the current best-fit program.
     * @return  A copy of the current leader
     * @throws RemoteException
     */
    Program getCurrentLeader() throws RemoteException;
}
