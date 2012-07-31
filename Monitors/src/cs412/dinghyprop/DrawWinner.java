/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.interpreter.Parser;
import cs412.dinghyprop.interpreter.ParsingException;
import cs412.dinghyprop.interpreter.TreeViewer;
import cs412.dinghyprop.simulator.ISimulator;
import cs412.dinghyprop.simulator.Obstacle;
import cs412.dinghyprop.simulator.Simulator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.rmi.Naming;
import java.util.Observable;
import java.util.Observer;

/**
 * This class provides a client for monitoring the progress of DinghyProp's
 * genetic programming by animating the current best-fit program.  By double-
 * clicking the animation, a display of the programs AST (in tree form) is
 * created and displayed.
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
     * Create and configure a new DrawWinner from a server reference.
     *
     * @param master    the server to monitor
     */
    public DrawWinner(IMaster master) throws IOException {
        server = master;
        dinghy = ImageIO.read(getClass().getClassLoader().getResource("dinghy.png"));

        try {
            sims = server.getEvaluationSimulators();
        } catch (Exception e) {
            reportError("Could not retrieve simulators.", e);
        }

        // Open a TreeViewer for the current program on double-click
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    try {
                        TreeViewer.createFramedExpression(
                                new Parser(currentProgram).parse()
                        ).setVisible(true);
                    } catch (ParsingException pe) {
                        reportError("Non-executable Program", pe);
                    }
            }

            @Override public void mousePressed(MouseEvent e) { }
            @Override public void mouseReleased(MouseEvent e) { }
            @Override public void mouseEntered(MouseEvent e) { }
            @Override public void mouseExited(MouseEvent e) { }
        });

        initFrameAndDisplay();
    }

    /**
     * Create a containing JFrame, add the controls, and display it.
     */
    private void initFrameAndDisplay() {
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

        // Sets up an action listener on the button to grab the current winning
        // program each time the button is pressed. It then starts the animation.
        JButton update = new JButton("Get Current Winner");
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
     * Creates an interpreter with the current program and simulator, after
     * registering as an observer to the simulator, and passes that to a thread
     * that will run the simulation.
     *
     * @param current  the simulation to run
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
                } catch (ParsingException e) {
                    reportError("Non-executable Program", e);
                }
            }
        });
        interpreterThread.start();

        repaint();
	}

    /**
     * Paint the simulation environment on the screen.
     *
     * @param g the graphics object
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
     *
     * @param graph    the graphics object
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
        scalingFactor = (scalingFactor < 1) ? 1 : scalingFactor;

        // Get a half step scaling factor
        halfStep = scalingFactor / 2;

        // Compute a scaling factor for the dinghy image (which is not to scale
        // with the rest of the graphics)
        imageScaleRate = scalingFactor / 10;
        imageScaleRate = (imageScaleRate < 1) ? 1 : imageScaleRate;
    }

    /**
     * @return the preferred dimensions for the currently animated the
     * simulation environment.
     */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sizeX * scalingFactor, sizeY * scalingFactor);
	}

    /**
     * Observer callback -- notifies that the dinghy has bee moved.
     * Updates the dinghy's position schedules repainting.
     *
     * @param o the observable object that was updated
     * @param arg the object passed by notifyObservers
     */
	@Override
	public void update(Observable o, Object arg) {
        position = currentSimulator.getDinghy();
        repaint();
        try { Thread.sleep(DRAW_DELAY); } catch (InterruptedException ignored) { }
    }

    /**
     * Determines the position and heading of the dinghy and draws the boat
     * image on the animation screen.
     *
     * @param g  the graphics object
     * @param x  X position of the dinghy
     * @param y  Y position of the dinghy
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
     * Draw all of the obstacles in the simulation on the animation.
     *
     * @param g  the graphics object
     */
    private void drawObstacles(Graphics2D g) {
		for(Obstacle obstacle : obstacles) {
			int[] pos = obstacle.getPosition();
            drawCenteredScaledCircle(g, pos[0], pos[1]);
		}
	}

    /**
     * Draws a scaled circle centered on a point.
     *
     * @param g    the graphics object (with desired color already set)
     * @param x    the x coordinate
     * @param y    the y coordinate
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
            updateSimulator();
        } catch(Exception e) {
            reportError("Could not retrieve program.", e);
        }
    }

    /**
     * Report an exception to the user with a JOptionPane.
     *
     * @param description    A brief description of the error (context)
     * @param e    the exception that caused error
     */
    private void reportError(String description, Exception e) {
        JOptionPane.showMessageDialog(this,
                "Cause: " + e.getLocalizedMessage(),
                description, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Update the simulation to the selected simulator.
     */
    private void updateSimulator() {
        try {
            ISimulator sim = sims[dropDown.getSelectedIndex()].clone();
            setSimulation(sim);
        } catch (CloneNotSupportedException e) {
            reportError("Could not create simulator", e);
        }
    }

    /**
     * Start a new DrawWinner application.
     * <p>
     * If an address is specified at the command line, it is used to locate a
     * server.  If no address is specified on the command line, the default is
     * 127.0.0.1.  The server is connected and used to create a new DrawWinner
     * object.
     *
     * @param args an optional IP address of the server
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
