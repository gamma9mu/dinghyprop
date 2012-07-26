package cs412.dinghyprop;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface for the evaluation clients.
 */
public interface IClient extends Remote {

    /**
     * Evaluate a program's fitness.
     * @param program    The program text
     * @return  The fitness of the program
     * @throws RemoteException
     */
    int evaluateProgram(String program) throws RemoteException;

    /**
     * Inform the client that it is not longer needed.
     * @throws RemoteException
     */
    void release() throws RemoteException;
}
