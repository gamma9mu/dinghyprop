/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface for the evaluation clients.
 *
 * This interface specifies the methods available for use by the server on
 * clients that have registered as evaluation callback targets.
 */
public interface IClient extends Remote {

    /**
     * Calculates the total summed fitness over any test case(s) the IClient
     * has access to.
     *
     * @param program    The program text
     * @return The fitness of the program
     * @throws RemoteException if an RMI error occurs
     */
    int evaluateProgram(String program) throws RemoteException;

    /**
     * Inform the client that it is not longer needed and may exit.
     *
     * @throws RemoteException if an RMI error occurs
     */
    void release() throws RemoteException;
}
