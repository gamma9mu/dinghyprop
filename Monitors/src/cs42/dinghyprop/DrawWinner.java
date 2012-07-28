package cs42.dinghyprop;

import cs412.dinghyprop.IMaster;
import cs412.dinghyprop.simulator.*;
import cs412.dinghyprop.interpreter.*;
import cs412.dinghyprop.genetics.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Observable;
import java.rmi.*;

/**
 * This class creates the animation of the current winning program.
 *
 */
public class DrawWinner extends JPanel implements Observer{
    private static final long serialVersionUID = -5236126589222504417L;
    private static final int DRAW_DELAY = 300;
    private int sizeX, sizeY;
	private Simulator currentSimulator = null;
    private int[] goal = null;
	private Obstacle[] obstacles = null;
    private static IMaster master = null;
	private static JComboBox dropDown = null;
	private static ISimulator[] sims = null;
	private static DrawWinner draw = null;
    private int[] position = {0, 0};
    protected transient volatile Thread interpreterThread = null;

    /**
     * Constructor that sets initial size of animation window
     */
    public DrawWinner() {
		sizeX = 30;
		sizeY = 30;
	}

    /**
     * This method sends the simulation to the interpreter and paints the simulation on the screen
     * @param current  The current simulation being passed to the interpreter
     * @param prog  The current program being tested by the interpreter.
     * @throws CloneNotSupportedException When a clone is not supported on the simulation.
     */
	public void setSimulation(ISimulator current, final Program prog) throws CloneNotSupportedException {
        if (interpreterThread != null)
            interpreterThread = null;
		currentSimulator = ((Simulator) current).clone();
        currentSimulator.addObserver(this);
		int[] size = currentSimulator.getSize();
		sizeX = size[0];
		sizeY = size[1];

        position = currentSimulator.getDinghy();

		goal = currentSimulator.getGoal();
		obstacles = currentSimulator.getObstacles();
        System.out.println(prog.program);
        interpreterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Interpreter interpreter = new Interpreter(currentSimulator, prog.program);
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
		if(currentSimulator != null) {
			graph.setColor(Color.GREEN);
			graph.fillOval(goal[0] * 10, goal[1] * 10, 10, 10);
		
			graph.setColor(Color.RED);
			drawObstacles(g);
            moveDinghy(g, position[0], position[1]);
        }
	}

    /**
     * This method gets the dimensions for the animation from the simulation environment
     * @return The size of the animation screen.
     */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sizeX * 10, sizeY * 10);
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
     * @param posX  The X Position of the dinghy.
     * @param posY  The Y Position of the dinghy.
     */
	public void moveDinghy(Graphics g, int posX, int posY) {
		this.repaint();
		g.setColor(Color.BLUE);
		int tempX = posX * 10;
		int tempY = posY * 10;
		int[] xPositions = null;
		int[] yPositions = null;
		try{
			switch(currentSimulator.reference("heading")) {
				case 180:
                    xPositions = new int[]{tempX, tempX,
						5 + tempX, 10 + tempX, 10 + tempX};
					yPositions = new int[]{17 + tempY, 10 + tempY,
                        tempY, 10 + tempY, 17 + tempY};
					break;
				case 90:
                    xPositions = new int[]{tempX, 10 + tempX,
						17 + tempX, 10 + tempX, tempX};
					yPositions = new int[]{tempY, tempY,
                        5 + tempY, 10 + tempY, 10 + tempY};
					break;
				case 0:
                    xPositions = new int[]{tempX, tempX,
						5 + tempX, 10 + tempX, 10 + tempX};
					yPositions = new int[]{tempY, 10 + tempY,
                        17 + tempY, 10 + tempY, tempY};
					break;
				case 270:
                    xPositions = new int[]{17 + tempX, 7 + tempX,
						tempX, 7 + tempX, 17 + tempX};
					yPositions = new int[]{tempY, tempY,
                        5 + tempY, 10 + tempY, 10 + tempY};
					break;
			}
		} catch(cs412.dinghyprop.simulator.VariableReferenceException e) {
			System.out.println("We caught something");
		}
		g.fillPolygon(xPositions, yPositions, 5);
	}

    /**
     * This method draws all of the obstacles in the simulation environment on the animation screen.
     * @param g  The current Graphics object.
     */
    private void drawObstacles(Graphics g) {
		for(Obstacle obstacle : obstacles) {
			int[] position = obstacle.getPosition();
			g.fillOval(position[0] * 2, position[1] * 2, 10, 10);
		}
	}

    /**
     * This method starts the animation process every time a user presses the button to request the current winner. It
     * does this by retrieving the current selected simulation and the calling the getCurrentLeader method in the master
     * program. It then sends the simulation and the program to the setSimulation method.
     */
    private void startAnimation() {
        int index = dropDown.getSelectedIndex();
        Program program = null;
        try {
            program = master.getCurrentLeader();
        } catch(Exception e) {
            e.printStackTrace();
        }
        ISimulator sim = sims[index];
        try {
            draw.setSimulation(sim, program);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method gets all of the simulators from master by calling getEvaluationSimulators. It then creates the JFrame
     * that will show all of the GUI components. This includes the drop down box, button, and animation screen.
     */
    private static void createGui() {

		try {
			sims = master.getEvaluationSimulators();
		} catch(Exception e) {
			e.printStackTrace();
		}


		dropDown = new JComboBox();
		for(int i = 0; i < sims.length; i++) {
			dropDown.addItem("Simulator " + i);
		}

        draw = new DrawWinner();

		JButton button = new JButton("Get Current Winner");
        /**
         * Sets up an action listener on the button to grab the current winning program each time the button
         * is pressed. It then starts the animation.
         */
		button.addActionListener(
			new ActionListener() {
				@Override
                public void actionPerformed(ActionEvent e) {
					draw.startAnimation();
				}
			}
		);

		JFrame frame = new JFrame("animation");
		frame.add(dropDown, BorderLayout.NORTH);
		frame.add(draw, BorderLayout.CENTER);
		frame.add(button, BorderLayout.SOUTH);

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

	}

    /**
     * The main method that sets up the RMI connection based on CLI arguments. It then calls createGui to set up the
     * JFrame.
     * @param args Command Line arguments that specify the IP address to connect to.
     */
    public static void main(String[] args) {
		String masterName = "//";
		if(args.length == 0)
			masterName += "localhost";
		else
			masterName += args[0];
		masterName += "/Master";
		try {
			master = (IMaster)Naming.lookup(masterName);
		} catch(java.rmi.ConnectException ce){
			System.err.println("Could not connect");
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
            public void run() {
				createGui();
			}
		});
	}
	
}
