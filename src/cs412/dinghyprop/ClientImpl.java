package cs412.dinghyprop;

import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.interpreter.ParsingException;
import cs412.dinghyprop.simulator.Simulator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Evaluation client
 */
public class ClientImpl extends UnicastRemoteObject implements IClient {
    private static final long serialVersionUID = 7075703919341311722L;

    private Simulator[] simulators;

    /*
     * Processed program count
     */
    int count = 0;

    /**
     * Create a new client evaluator with a set of simulation environments.
     * @param simulators    The simulation environments
     * @throws RemoteException
     */
    public ClientImpl(Simulator[] simulators) throws RemoteException {
        this.simulators = simulators;
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

        for (Simulator simulator : simulators) {
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
        return "Processing";
    }
}
