import javax.swing.*;
import java.awt.*;
import cs412.dinghyprop.simulator.Simulator;
import cs412.dinghyprop.simulator.Obstacle;
import cs412.dinghyprop.simulator.History;
import cs412.dinghyprop.simulator.SimulatorRandom;

public class DrawWinner extends JPanel {
	private int sizeX;
	private int sizeY;
	private Simulator currentWinner;
	private int[] goal;
	private int prevDinghyPosX;
	private int prevDinghyPosY;
	private Obstacle[] obstacles;
	private Graphics2D graph;
	private History history;
	private boolean continueRunning;
	private final int SIZEX = 300;
	private final int SIZEY = 300;
	private final int MAX_OBSTACLE = 20;
	
	
	public DrawWinner() {
		continueRunning = true;
		currentWinner = new SimulatorRandom(SIZEX, SIZEY, MAX_OBSTACLE).getSimulator();
		history = currentWinner.getHistory();
		int size[] = currentWinner.getSize();
		sizeX = size[0];
		sizeY = size[1];
		goal = currentWinner.getGoal();
		obstacles = currentWinner.getObstacles();
		
		
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		graph = (Graphics2D)g;
		
		graph.setColor(Color.GREEN);
		graph.fillOval(goal[0] * 2, goal[1] * 2, 10, 10);
		
		graph.setColor(Color.RED);
		drawObstacles();
		
		while(continueRunning) {
			moveDinghy(history.getNextX(), history.getNextY());
		}
		
	
	} 
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sizeX * 2, sizeY * 2);
	
	}
	
	public void moveDinghy(int posX, int posY) {
		System.out.println(posX + " " + posY);
		if(posX == -1 || posY == -1) {
			continueRunning = false; }
		else {
			graph.setColor(Color.BLUE);
			graph.fillOval(posX * 2, posY * 2, 10, 10); }
	}
	
	private void drawObstacles() {
		int count = 0;
		for(Obstacle obstacle : obstacles) {
			int position[] = obstacle.getPosition();
			graph.fillOval(position[0] * 2, position[1] * 2, 10, 10);
			count++;
		}
		System.out.println(count);
	}
	
	private static void createGui() {
		DrawWinner draw = new DrawWinner();
		JFrame frame = new JFrame("animation");
		frame.getContentPane().add(draw);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGui();
			}
		});
	}
	
}
