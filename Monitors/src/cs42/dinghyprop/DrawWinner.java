package cs42.dinghyprop;

import cs412.dinghyprop.IMaster;
import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.interpreter.ParsingException;
import cs412.dinghyprop.simulator.ISimulator;
import cs412.dinghyprop.simulator.Obstacle;
import cs412.dinghyprop.simulator.Simulator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.rmi.Naming;
import java.util.Observable;
import java.util.Observer;

/**
 * This class creates the animation of the current winning program.
 *
 */
public class DrawWinner extends JPanel implements Observer{
    private static final long serialVersionUID = -5236126589222504417L;
    private static final int DRAW_DELAY = 300;
    private int sizeX = 100, sizeY = 100;
    private Simulator currentSimulator = null;
    private int[] goal = null;
    private Obstacle[] obstacles = null;
    private IMaster server;
    private JComboBox dropDown;
    private ISimulator[] sims = null;
    private String currentProgram = "";
    private int[] position = {0, 0};
    protected transient volatile Thread interpreterThread = null;
    private int scalingFactor = 2;
    private int halfStep = 1;
    private int imageScaleRate = scalingFactor / 10;
    private transient Image dinghy;

    /**
     * Constructor that sets initial size of animation window
     */
    public DrawWinner(IMaster master) throws IOException {
        server = master;
        dinghy = ImageIO.read(getClass().getClassLoader().getResource("dinghy.png"));

        try {
            sims = server.getEvaluationSimulators();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Could not retrieve simulators\nCause: " + e.getLocalizedMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        dropDown = new JComboBox();
        for (int i = 0; i < sims.length; i++) {
            dropDown.addItem("Simulator " + i);
        }
        dropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSimulator();
            }
        });

        JButton update = new JButton("Get Current Winner");
        /**
         * Sets up an action listener on the button to grab the current winning program each time the button
         * is pressed. It then starts the animation.
         */
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startAnimation();
            }
        });

        JFrame frame = new JFrame("animation");
        frame.add(dropDown, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);
        frame.add(update, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    /**
     * This method sends the simulation to the interpreter and paints the simulation on the screen
     * @param current  The current simulation being passed to the interpreter
     */
	public void setSimulation(ISimulator current) {
        if (interpreterThread != null)
            interpreterThread = null;
        currentSimulator = (Simulator) current;
        currentSimulator.addObserver(this);
		int[] size = currentSimulator.getSize();
		sizeX = size[0];
		sizeY = size[1];

        position = currentSimulator.getDinghy();
		goal = currentSimulator.getGoal();
		obstacles = currentSimulator.getObstacles();

        final String prog = currentProgram;
        interpreterThread = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                Interpreter interpreter = new Interpreter(currentSimulator, prog);
                Thread me = Thread.currentThread();
                for (int i = 0; i < 100; i++) {
                    if (interpreterThread != me)
                        break;
                    interpreter.execute();
                }
            } catch (ParsingException pe) {
                pe.printStackTrace();
            }
            }
        });
        interpreterThread.start();

        repaint();
	}

    /**
     * This method paints the simulation environment on the screen.
     * @param g The current graphics object.
     */
    @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D graph = (Graphics2D) g;

        // Blank the background
        graph.setColor(Color.white);
        graph.fill(new Rectangle(getWidth(), getHeight()));

        if(currentSimulator != null) {
            computeScalers();

            // Draw the grid
            graph.setColor(Color.black);
            drawGrid(graph);

            // Draw the goal
            graph.setColor(Color.GREEN);
            drawCenteredScaledCircle(graph, goal[0], goal[1]);

            // Draw the obstacles
            graph.setColor(Color.RED);
			drawObstacles(graph);

            // Draw the dinghy
            drawDinghy(graph, position[0], position[1]);
        }
	}

    /**
     * Draw a grid to represent possible object locations.
     * @param graph    The graphics to draw with
     */
    private void drawGrid(Graphics2D graph) {
        int width = currentSimulator.getSize()[0] * scalingFactor;
        int height = currentSimulator.getSize()[1] * scalingFactor;

        for (int i = 0; i <= height; i += scalingFactor)
            graph.drawLine(5, i+5, width+5, i+5);

        for (int i = 0; i <= width; i += scalingFactor)
            graph.drawLine(i+5, 5, i+5, height+5);
    }

    /**
     * Compute the scaling factors for the current simulator and window size
     * combination.
     */
    private void computeScalers() {
        Dimension wsize = getSize();
        int[] ssize = currentSimulator.getSize();

        // Compute overall scaling factor
        scalingFactor = (int) (Math.sqrt(
                (wsize.width - 10) * (wsize.width - 10)
                        + (wsize.height - 10) * (wsize.height - 10))
                / Math.sqrt(ssize[0] * ssize[0] + ssize[1] * ssize[1]));
        // Get a half step scaling factor
        halfStep = scalingFactor / 2;

        // Compute a scaling factor for the dinghy image (which is not to scale
        // with the rest of the graphics)
        imageScaleRate = scalingFactor / 10;
        if (imageScaleRate < 1)
            imageScaleRate = 1;
    }

    /**
     * This method gets the dimensions for the animation from the simulation environment
     * @return The size of the animation screen.
     */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sizeX * scalingFactor, sizeY * scalingFactor);
	}

    /**
     * This method gets called whenever the simulation updates the dinghy. It then sets the new position of the
     * dinghy and calls repaint.
     * @param o The observable object that was updated.
     * @param arg The object passed by notifyObservers
     */
	@Override
	public void update(Observable o, Object arg) {
        position = currentSimulator.getDinghy();
        repaint();
        try { Thread.sleep(DRAW_DELAY); } catch (InterruptedException ignored) { }
    }

    /**
     * This method calculates the position of the dinghy based on heading. It then adds the dinghy polygon to the
     * animation screen.
     * @param g  The current Graphics object.
     * @param x  The X Position of the dinghy.
     * @param y  The Y Position of the dinghy.
     */
	public void drawDinghy(Graphics2D g, int x, int y) {
		this.repaint();

        int tempX = x * scalingFactor;
        int tempY = y * scalingFactor;
        int w = (dinghy.getWidth(null) / 2) * imageScaleRate;
        int h = (dinghy.getHeight(null) / 2) * imageScaleRate;
        int quadrants = 0;
		try{
            int heading = currentSimulator.reference("heading");
            if (heading == 0) {
                    tempX -= w;
                    tempY -= h;
            } else if (heading == 90) {
                    quadrants = 3;
                    tempX -= h;
                    tempY += w;
            } else if (heading == 180) {
                    quadrants = 2;
                    tempX += w;
                    tempY += h;
            } else /* if (heading == 270) */ {
                    quadrants = 1;
                    tempX += h;
                    tempY -= w;
			}
		} catch(cs412.dinghyprop.simulator.VariableReferenceException ignored) { }

        AffineTransform at = AffineTransform.getTranslateInstance(tempX + 5, tempY + 5);
        at.concatenate(AffineTransform.getQuadrantRotateInstance(quadrants));
        at.concatenate(AffineTransform.getScaleInstance(imageScaleRate, imageScaleRate));
        g.drawImage(dinghy, at, null);
    }

    /**
     * This method draws all of the obstacles in the simulation environment on the animation screen.
     * @param g  The current Graphics object.
     */
    private void drawObstacles(Graphics2D g) {
		for(Obstacle obstacle : obstacles) {
			int[] pos = obstacle.getPosition();
            drawCenteredScaledCircle(g, pos[0], pos[1]);
		}
	}

    /**
     * Draws a scaled circle centered on a point.
     * @param g    The {@code Graphics2D} to draw with (with color set)
     * @param x    The x coordinate
     * @param y    The y coordinate
     */
    private void drawCenteredScaledCircle(Graphics2D g, int x, int y) {
        int x0 = (x * scalingFactor) - halfStep + 5;
        int y0 = (y * scalingFactor) - halfStep + 5;
        g.fillOval(x0, y0, scalingFactor, scalingFactor);
    }

    /**
     * This method starts the animation process every time a user presses the button to request the current winner. It
     * does this by retrieving the current selected simulation and the calling the getCurrentLeader method in the master
     * program. It then sends the simulation and the program to the setSimulation method.
     */
    private void startAnimation() {
        try {
            currentProgram = server.getCurrentLeader().program;
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Could not retrieve program\nCause: " + e.getLocalizedMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateSimulator();
    }

    /**
     * Update the simulation to the selected simulator.
     */
    private void updateSimulator() {
        try {
            ISimulator sim = sims[dropDown.getSelectedIndex()].clone();
            setSimulation(sim);
        } catch (CloneNotSupportedException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not create simulator\nCause: " + e.getLocalizedMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The main method that sets up the RMI connection based on CLI arguments.
     * It then sets up the DrawWinner.
     * @param args Command Line arguments that specify the IP address to connect to.
     */
    public static void main(String[] args) {
		String masterName;
		if(args.length == 0)
			masterName = "//127.0.0.1/Master";
		else
			masterName = "//" + args[0] + "/Master";

		try {
			IMaster master = (IMaster)Naming.lookup(masterName);
            new DrawWinner(master);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Cause: " + e.getLocalizedMessage(),
                    "Error Initializing", JOptionPane.ERROR_MESSAGE);
		} catch(Exception e){
            JOptionPane.showMessageDialog(null,
                    "Cause: " + e.getLocalizedMessage(),
                    "Could Not Connect", JOptionPane.ERROR_MESSAGE);
		}
	}
}
