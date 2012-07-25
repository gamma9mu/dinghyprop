package cs412.dinghyprop;

import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.interpreter.ParsingException;
import cs412.dinghyprop.simulator.Simulator;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Evaluation slave
 */
public class Slave extends UnicastRemoteObject implements IClient {
    private static final long serialVersionUID = 7075703919341311722L;

    private Simulator[] simulators;

    /*
     * Processed program count
     */
    int count = 0;

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

        count++;

        return fitness;
    }

    /**
     * Get the count of processed programs.
     * @return  The number programs processed by this slave
     */
    public int getCount() {
        return count;
    }

    public String getStatus() {
        return "Processing";
    }

    /**
     * Testing main
     * @param args    CLI args: [master_ip_address]
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String ip = "127.0.0.1";
        if (args.length == 1) {
            ip = args[0];
        } else if (args.length > 1) {
            System.err.println("Usage: slave [master_address]");
            System.exit(-1);
        }

        String address = "//" + ip + "/Master";
        IMaster master = (IMaster) Naming.lookup(address);
        Simulator[] sims = master.getEvaluationSimulators();
        Slave me = new Slave(sims);
        SlaveWindow sw = new SlaveWindow(me);
        master.registerSlave(me);
        sw.run();
    }
}
