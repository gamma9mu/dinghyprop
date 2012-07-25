import javax.swing.*;
import java.awt.*;
import cs412.dinghyprop.simulator.Simulator;
import cs412.dinghyprop.simulator.Obstacle;
import cs412.dinghyprop.simulator.History;

public class DrawWinner extends JPanel {
	private int sizeX;
	private int sizeY;
	private Simulator currentWinner;
	private int[] goal;
	private int prevDinghyPosX;
	private int prevDinghyPosY;
	private Obstacle[] obstacles;
	private Graphics2D graph;
	
	
	public DrawWinner(Simulator current) {
		currentWinner = current;
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
		graph.fillOval(goal[0], goal[1], 1, 1);
		
		graph.setColor(Color.RED);
		drawObstacles();
		
	
	} 
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sizeX, sizeY);
	
	}
	
	public void moveDinghy(int posX, int posY) {
	
	}
	
	private void drawObstacles() {
		for(Obstacle obstacle : obstacles) {
			int position[] = obstacle.getPosition();
			graph.fillOval(position[0], position[1], 1, 1);
		}
	}
}
