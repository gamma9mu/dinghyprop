package cs412.dinghyprop;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Interface for the evaluation clients.
 */
public interface ISlave extends Remote {

    /**
     * Evaluate a program's fitness.
     * @param program    The program text
     * @return  The fitness of the program
     */
    int evaluateProgram(String program) throws RemoteException;
}
