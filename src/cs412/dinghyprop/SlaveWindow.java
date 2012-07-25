package cs412.dinghyprop;

import javax.swing.*;
import java.awt.*;

/**
 * GUI for RMI slave.
 */
public class SlaveWindow extends JFrame {
    private static final long serialVersionUID = 4245107449408098871L;

    private JLabel statusLabel;
    private JLabel processedLabel;

    private Slave slave;

    /**
     * Create a new {@code SlaveWindow}.
     * @param slave    The {@code Slave} to watch
     */
    public SlaveWindow(Slave slave) {
        this.slave = slave;

        processedLabel = new JLabel("Processed: 0");
        processedLabel.setPreferredSize(new Dimension(300, 16));
        statusLabel = new JLabel("Status: Initializing");
        statusLabel.setPreferredSize(new Dimension(300, 16));

        setLayout(new GridLayout(2, 1));
        getContentPane().add(processedLabel, 0);
        getContentPane().add(statusLabel, 1);

        setTitle("DGP Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
    }

    /**
     * Show this and schedule updates.
     */
    public void run() {
        setVisible(true);
        final SlaveWindow sw = this;
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
                updateProcessed(slave.getCount());
                updateStatus(slave.getStatus());
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
