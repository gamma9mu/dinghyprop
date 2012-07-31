/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.interpreter.ParsingException;
import cs412.dinghyprop.simulator.ISimulator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Evaluation client
 *
 * Objects of this class connect to the server, obtain a copy of the server's
 * simulation environments, and register themselves as a callback target for
 * processing the server's programs.
 */
public class ClientImpl extends UnicastRemoteObject implements IClient {
    private static final long serialVersionUID = 7075703919341311722L;

    /**
     * The complete RMI address of the server
     */
    private String masterAddress;

    /**
     * Server reference
     */
    private transient IMaster master = null;

    /**
     * Copies of the simulators obtained from the server
     */
    private transient ISimulator[] simulators = null;

    /**
     * The current status message of this client
     */
    private String status = "Not Connected";

    /**
     * Whether this client ist still "running," or more loosely, whether this
     * client can be expected to produce status updates.
     *
     * This field is set to true at object creation and should remain true at
     * least until this client has been disconnected from the server.
     */
    private boolean running = true;

    /**
     * A running total of all the programs evaluated by this client
     */
    private int count = 0;

    /**
     * Create a new client evaluator with a set of simulation environments.
     *
     * @param address    The address of the RMI server
     * @throws RemoteException inherited from superclass constructors
     */
    public ClientImpl(String address) throws RemoteException {
        super();
        masterAddress = "//" + address + "/Master";
    }

    /**
     * Attach to the server.
     *
     * @throws MalformedURLException if the server address is bad
     * @throws NotBoundException if no server exists at the given address
     * @throws RemoteException if this could not connect to the RMI registry or
     * if an RMI error occurs while either retrieving the simulators or
     * registering itself as a callback target
     */
    public void initialize() throws MalformedURLException, NotBoundException, RemoteException {
        status = "Looking up server...";
        master = (IMaster) Naming.lookup(masterAddress);
        status = "Obtaining environments...";
        simulators = master.getEvaluationSimulators();
        status = "Registering with server...";
        master.registerClient(this);
        status = "Awaiting program";
    }

    @Override
    public int evaluateProgram(String program) throws RemoteException {
        int fitness = 0;

        for (ISimulator simulator : simulators) {
            try {
                status = "Creating interpreter";
                Interpreter interpreter = new Interpreter(simulator.clone(), program);
                status = "Evaluating...";
                interpreter.run(100);
                fitness += interpreter.getFitness();
            } catch (ParsingException ignored) { }
            catch (CloneNotSupportedException ignored) { }
        }
        status = "Awaiting program";

        count++;

        return fitness;
    }

    @Override
    public void release() throws RemoteException {
        master = null;
        running = false;
        status = "Disconnected";
    }

    /**
     * @return  the cumulative total number programs processed by this client
     */
    public int getCount() {
        return count;
    }

    /**
     * @return  the current status of this client
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return  the full RMI address string of the server this client is (or
     * was) connected to
     */
    public String getServerAddress() {
        return masterAddress;
    }

    /**
     * Query whether there is a possibility of updates to the status of this
     * client.  Essentially, this method reports whether the client is
     * connected to the server, but this is not necessarily the case if true is
     * returned.
     *
     * @return whether this is still running.
     */
    public boolean isRunning() {
        return running;
    }

    @Override
    public String toString() {
        return "ClientImpl rmi://" + masterAddress +
                " (" + " processed: " + count + " -- " + status + ')';
    }
}
