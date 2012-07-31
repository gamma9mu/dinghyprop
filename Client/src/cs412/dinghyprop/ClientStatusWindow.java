/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import javax.swing.*;
import java.awt.*;

/**
 * GUI for RMI clientImpl.
 *
 * Creates and displays a small JFrame for displaying the server address,
 * client status, and the number of programs the client has processed for the
 * server.
 */
public class ClientStatusWindow extends JFrame {
    private static final long serialVersionUID = 4245107449408098871L;

    private JLabel statusLabel;
    private JLabel processedLabel;

    private ClientImpl clientImpl;

    /**
     * Create a new {@code ClientStatusWindow}.
     * @param clientImpl    The {@code ClientImpl} to watch
     */
    public ClientStatusWindow(ClientImpl clientImpl) {
        this.clientImpl = clientImpl;

        processedLabel = new JLabel("0");
        statusLabel = new JLabel("Initializing");

        setLayout(new GridLayout(3, 2));
        getContentPane().add(new JLabel("Server: ", SwingConstants.RIGHT));
        getContentPane().add(new JLabel(clientImpl.getServerAddress() + "    "));
        getContentPane().add(new JLabel("Processed: ", SwingConstants.RIGHT));
        getContentPane().add(processedLabel);
        getContentPane().add(new JLabel("Status: ", SwingConstants.RIGHT));
        getContentPane().add(statusLabel);

        setTitle("DGP Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
    }

    /**
     * Show the frame and start a thread to update it at regular intervals.
     */
    public void run() {
        setVisible(true);
        final ClientStatusWindow sw = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (sw.isClientRunning()) {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException ignored) { }
                    sw.doUpdate();
                }
            }
        }).start();
    }

    /**
     * Send a GUI update Runnable to the AWT thread.
     */
    public void doUpdate() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                processedLabel.setText(Integer.toString(clientImpl.getCount()));
                statusLabel.setText(clientImpl.getStatus());
                repaint();
            }
        });
    }

    /**
     * Query whether the client reports itself as still running.
     * @return  true for yes, etc.
     */
    private boolean isClientRunning() {
        return clientImpl.isRunning();
    }
}
