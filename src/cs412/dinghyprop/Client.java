package cs412.dinghyprop;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Client class to handle registering a {@code ClientImpl}.
 */
public class Client {

    /**
     * Create a client and register it with the master
     * @param address    The IP address of the master
     */
    public Client(String address) {
        try {
            String rmiAddress = "//" + address + "Master";
            IMaster master = (IMaster) Naming.lookup(rmiAddress);
            ClientImpl clientImpl = new ClientImpl(master.getEvaluationSimulators());
            master.registerClient(clientImpl);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Optionally read an IP address from the CLI.
     * @param args    An optional IP address
     */
    public static void main(String[] args) {
        if (args.length == 0)
            new Client("127.0.0.1");
        else
            new Client(args[0]);
    }
}
