package cs412.dinghyprop;

import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.interpreter.ParsingException;
import cs412.dinghyprop.simulator.Simulator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Evaluation slave
 */
public class Slave extends UnicastRemoteObject implements ISlave {
    private static final long serialVersionUID = 7075703919341311722L;

    private Simulator[] simulators;

    /**
     * Create a new slave evaluator with a set of simulation environments.
     * @param simulators    The simulation environments
     * @throws RemoteException
     */
    public Slave(Simulator[] simulators) throws RemoteException {
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

        return fitness;
    }
}
