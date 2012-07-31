/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.simulator.ISimulator;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface for the server.
 *
 * This interface provides necessary functionality for the setup and use of
 * clients.
 */
public interface IMaster extends Remote, Serializable {

    /**
     * Obtain a copy of the simulators that should <b>ALL</b> be used in the
     * evaluation of programs.
     *
     * @return  An array of the simulators to use to evaluate programs
     * @throws RemoteException  if an RMI error occurs
     */
    ISimulator[] getEvaluationSimulators() throws RemoteException;

    /**
     * Register a client with the server as willing to evaluate program
     * fitness. <b>Important:</b> there is no requirement that a client
     * explicitly unregister itself from the server.
     *
     * @param client    The client instance
     * @throws RemoteException if an RMI error occurs
     */
    void registerClient(IClient client) throws RemoteException;

    /**
     * Obtain a copy of the previous generations program with the highest
     * fitness.
     * <p>
     * This method is intended for use by clients who wish to monitor the
     * progress of the generations.
     *
     * @return  A copy of the current leader
     * @throws RemoteException if an RMI error occurs
     */
    Program getCurrentLeader() throws RemoteException;
}
