package cs412.dinghyprop;

import cs412.dinghyprop.simulator.*;
import cs412.dinghyprop.interpreter.*;
import cs412.dinghyprop.genetics.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Observable;
import java.rmi.*;


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
    private transient Thread interpreterThread = null;


    public DrawWinner() {
		sizeX = 30;
		sizeY = 30;
	}
	
	public void setSimulation(ISimulator current, final Program prog) throws CloneNotSupportedException {
        if (interpreterThread != null)
            interpreterThread.stop();
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
                    interpreter.run(100);
                } catch (ParsingException pe) {
                    pe.printStackTrace();
                }
            }
        });
        interpreterThread.start();

        repaint();
	}

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
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sizeX * 10, sizeY * 10);
	}

	@Override
	public void update(Observable o, Object arg) {
        position = currentSimulator.getDinghy();
        repaint();
        try { Thread.sleep(DRAW_DELAY); } catch (InterruptedException ignored) { }
    }
	
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
	
	private void drawObstacles(Graphics g) {
		for(Obstacle obstacle : obstacles) {
			int[] position = obstacle.getPosition();
			g.fillOval(position[0] * 2, position[1] * 2, 10, 10);
		}
	}


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
