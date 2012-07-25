package cs412.dinghyprop;

import java.rmi.Naming;

/**
 * Client class to handle registering a {@code ClientImpl}.
 */
public class Client {

    /**
     * Create a client and register it with the master
     * @param address    The IP address of the master
     */
    public Client(String address) throws Exception {
        String rmiAddress = "//" + address + "/Master";
        IMaster master = (IMaster) Naming.lookup(rmiAddress);
        ClientImpl client = new ClientImpl(master.getEvaluationSimulators());
        SlaveWindow sw = new SlaveWindow(client);
        sw.run();
        master.registerClient(client);
    }

    /**
     * Optionally read an IP address from the CLI.
     * @param args    An optional IP address
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0)
            new Client("127.0.0.1");
        else if (args.length == 1)
            new Client(args[0]);
        else {
            System.err.println("Usage: Client [master_address]");
            System.exit(-1);
        }
    }
}
