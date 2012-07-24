import javax.swing.*;
import java.awt.*;
import cs412.dinghyprop.simulator.Simulator;
import cs412.dinghyprop.simulator.Obstacle;

public class DrawWinner extends JPanel implements Observer{
	private int sizeX;
	private int sizeY;
	private Simulator currentWinner;
	private int[] goal;
	private int prevDinghyPos;
	private int prevDinghyPos;
	private Obstacle[] obstacles;
	
	
	public DrawWinner() {
		int size[] = currentWinner.getSize();
		sizeX = size[0];
		sizeY = size[1];
		goal = currentWinner.getGoal();
		obstacles = currentWinner.getObstacles();
	}

	@override
	protected void paintComponent(Graphics g) {
		return new Dimension(sizeX, sizeY);
	
	} 
	
	@override
	public Dimension getPreferredSize() {
		
	
	}
	
	public void moveDinghy(int posX, int posY) {
	
	}
}
