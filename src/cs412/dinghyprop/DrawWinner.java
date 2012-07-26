package cs412.dinghyprop;

import javax.swing.*;
import java.awt.*;
import cs412.dinghyprop.simulator.Simulator;
import cs412.dinghyprop.simulator.Obstacle;
import cs412.dinghyprop.simulator.SimulatorRandom;
import cs412.dinghyprop.simulator.Dinghy;
import java.util.Observer;
import java.util.Observable;

public class DrawWinner extends JPanel implements Observer{
	private int sizeX;
	private int sizeY;
	private Simulator currentWinner;
	private int[] goal;
	private int prevDinghyPosX;
	private int prevDinghyPosY;
	private Obstacle[] obstacles;
	private Graphics2D graph;
	private static final int SIZEX = 300;
	private static final int SIZEY = 300;
	private static final int MAX_OBSTACLE = 20;
	
	
	public DrawWinner(Simulator current) {
		currentWinner = current;
		currentWinner.addObserver(this);
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
			
	
	} 
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sizeX * 2, sizeY * 2);
	
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Dinghy temp = (Dinghy)arg;
		int[] position = temp.getPosition();
		moveDinghy(position[0], position[1]);
	}
	
	public void moveDinghy(int posX, int posY) {
		this.repaint();
		graph.setColor(Color.BLUE);
		int tempX = posX * 2;
		int tempY = posY * 2;
		int[] xPositions = null;
		int[] yPositions = null;
		try{
			switch(currentWinner.reference("heading")) {
				case 0:
					int[] xTemp = {0 + tempX, 0 + tempX, 
						5 + tempX, 10 + tempX, 10 + tempX};
					int[] yTemp = {17 + tempY, 10 + tempY, 
						0 + tempY, 10 + tempY, 17 + tempY};
					xPositions = xTemp;
					yPositions = yTemp;
					break;
				case 90:
					int[] xTemp90 = {0 + tempX, 10 + tempX, 
						17 + tempX, 10 + tempX, 0 + tempX};
					int[] yTemp90 = {0 + tempY, 0 + tempY, 
						5 + tempY, 10 + tempY, 10 + tempY};
					xPositions = xTemp90;
					yPositions = yTemp90;
					break;
				case 180:
					int[] xTemp180 = {0 + tempX, 0 + tempX, 
						5 + tempX, 10 + tempX, 10 + tempX};
					int[] yTemp180 = {0 + tempY, 10 + tempY, 
						17 + tempY, 10 + tempY, 0 + tempY};
					xPositions = xTemp180;
					yPositions = yTemp180;
					break;
				case 270:
					int[] xTemp270 = {17 + tempX, 7 + tempX, 
						0 + tempX, 7 + tempX, 17 + tempX};
					int[] yTemp270 = {0 + tempY, 0 + tempY, 
						5 + tempY, 10 + tempY, 10 + tempY};
					xPositions = xTemp270;
					yPositions = yTemp270;
					break;
			}
		} catch(cs412.dinghyprop.simulator.VariableReferenceException e) {
			
		}
		graph.fillPolygon(xPositions, yPositions, 5);
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
	
	
	private static void createGui(Simulator sim) {
		DrawWinner draw;
		if (sim == null)
			draw = new DrawWinner((Simulator)
                    new SimulatorRandom(SIZEX, SIZEY, MAX_OBSTACLE).getSimulator());
		else
			draw = new DrawWinner(sim);
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
				createGui(null);
			}
		});
	}
	
}
