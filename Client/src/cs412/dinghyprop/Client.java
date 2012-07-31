/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.*;
import java.net.InetAddress;

/**
 * DinghyProp client entry point
 *
 * This class' main() determines whether the client is being run from Java
 * WebStart or the command line and attempts to determine the address of the
 * server from a system property (in the case of JavaWS) or a command line
 * argument.  If neither method produces an address, the default (127.0.0.1) is
 * used.
 */
public class Client {

    /**
     * Create a client, associate it with a status window, and start them both.
     *
     * @param address    The address of the master
     */
    public Client(String address) {
        try {
            ClientImpl client = new ClientImpl(address);
            ClientStatusWindow sw = new ClientStatusWindow(client);
            sw.run();
            client.initialize();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cause: " + e.getLocalizedMessage(),
                    "Error Initializing", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Start a new client application.
     *
     * Optionally reads an IP address from the CLI, or (when running from Java
     * WebStart) read the address from a property in the JNLP file.
     *
     * @param args    An optional IP address
     */
    public static void main(String[] args) {
        String injws = System.getProperty("injws");
        String address = "127.0.0.1";

        if (injws != null && !injws.trim().isEmpty()) {
            try {
                BasicService service = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
                String host = service.getCodeBase().getHost();
                address = InetAddress.getByName(host).getHostAddress();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Cause: " + e.getLocalizedMessage(),
                        "Error Looking Up Server", JOptionPane.ERROR_MESSAGE);
            }
        } else if (args.length == 1) {
            address = args[0];
        } else {
            System.err.println("Usage: Client [master_address]");
            System.exit(-1);
        }

        new Client(address);
    }
}
