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
		
	
	} 
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sizeX, sizeY);
	
	}
	
	public void moveDinghy(int posX, int posY) {
	
	}
}
