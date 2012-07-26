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
 */
public class ClientImpl extends UnicastRemoteObject implements IClient {
    private static final long serialVersionUID = 7075703919341311722L;

    private String masterAddress;
    private transient IMaster master = null;
    private transient ISimulator[] simulators = null;
    private String status = "Not Connected";

    /*
     * Processed program count
     */
    int count = 0;

    /**
     * Create a new client evaluator with a set of simulation environments.
     * @param address    The address of the RMI server
     * @throws RemoteException
     */
    public ClientImpl(String address) throws RemoteException {
        super();
        masterAddress = "//" + address + "/Master";
    }

    /**
     * Attach to the server.
     * @throws MalformedURLException
     * @throws NotBoundException
     * @throws RemoteException
     */
    public void initialize() throws MalformedURLException, NotBoundException, RemoteException {
        status = "Looking up server...";
        master = (IMaster) Naming.lookup(masterAddress);
        status = "Obtaining environments...";
        simulators = master.getEvaluationSimulators();
        status = "Registering with server...";
        master.registerClient(this);
        status = "Processing...";
    }

    /**
     * Calculates the total summed fitness over every test case.
     * @param program    The program text
     * @return  The fitness of the program
     * @throws RemoteException
     */
    @Override
    public int evaluateProgram(String program) throws RemoteException {
        int fitness = 0;

        for (ISimulator simulator : simulators) {
            try {
                Interpreter interpreter = new Interpreter(simulator.clone(), program);
                interpreter.run(100);
                fitness += interpreter.getFitness();
            } catch (ParsingException ignored) { }
            catch (CloneNotSupportedException ignored) { }
        }

        count++;

        return fitness;
    }

    @Override
    public void release() throws RemoteException {
        master = null;
        status = "Disconnected";
    }

    /**
     * Get the count of processed programs.
     * @return  The number programs processed by this client
     */
    public int getCount() {
        return count;
    }

    /**
     * Get the status of the client.
     * @return  The current status
     */
    public String getStatus() {
        return status;
    }
}
