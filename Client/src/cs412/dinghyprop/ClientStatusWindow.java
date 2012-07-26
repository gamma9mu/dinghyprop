package cs412.dinghyprop;

import javax.swing.*;
import java.awt.*;

/**
 * GUI for RMI clientImpl.
 */
public class ClientStatusWindow extends JFrame {
    private static final long serialVersionUID = 4245107449408098871L;

    private JLabel addressLabel;
    private JLabel statusLabel;
    private JLabel processedLabel;

    private ClientImpl clientImpl;

    /**
     * Create a new {@code ClientStatusWindow}.
     * @param clientImpl    The {@code ClientImpl} to watch
     */
    public ClientStatusWindow(ClientImpl clientImpl) {
        this.clientImpl = clientImpl;

        addressLabel = new JLabel("Server: " + clientImpl.getServerAddress());
        processedLabel = new JLabel("Processed: 0");
        processedLabel.setPreferredSize(new Dimension(300, 16));
        statusLabel = new JLabel("Status: Initializing");
        statusLabel.setPreferredSize(new Dimension(300, 16));

        setLayout(new GridLayout(3, 1));
        getContentPane().add(addressLabel, 0);
        getContentPane().add(processedLabel, 1);
        getContentPane().add(statusLabel, 2);

        setTitle("DGP Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
    }

    /**
     * Show this and schedule updates.
     */
    public void run() {
        setVisible(true);
        final ClientStatusWindow sw = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException ignored) { }
                    sw.doUpdate();
                }
            }
        }).start();
    }

    /**
     * Invoke a GUI update on the GUI thread.
     */
    public void doUpdate() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateProcessed(clientImpl.getCount());
                updateStatus(clientImpl.getStatus());
                repaint();
            }
        });
    }

    /**
     * Update the processed GUI field.
     * @param processed    The new processed count
     */
    private void updateProcessed(int processed) {
        processedLabel.setText("Processed: " + Integer.toString(processed));
    }

    /**
     * Update the GUI status field.
     * @param status    The new status
     */
    private void updateStatus(String status) {
        statusLabel.setText("Status: " + status);
    }
}
