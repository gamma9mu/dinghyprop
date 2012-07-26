package cs412.dinghyprop;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import java.net.InetAddress;

/**
 * Client class to handle registering a {@code ClientImpl}.
 */
public class Client {

    /**
     * Create a client and register it with the master
     * @param address    The IP address of the master
     */
    public Client(String address) throws Exception {
        ClientImpl client = new ClientImpl(address);
        ClientStatusWindow sw = new ClientStatusWindow(client);
        sw.run();
        client.initialize();
    }

    /**
     * Optionally read an IP address from the CLI.
     * @param args    An optional IP address
     */
    public static void main(String[] args) throws Exception {
        String injws = System.getProperty("injws");
        String address = "127.0.0.1";

        if (injws != null && !injws.trim().isEmpty()) {
            BasicService service =
                    (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
            String host = service.getCodeBase().getHost();
            address = InetAddress.getByName(host).getHostAddress();
        } else if (args.length == 1) {
            address = args[0];
        } else {
            System.err.println("Usage: Client [master_address]");
            System.exit(-1);
        }

        new Client(address);
    }
}
