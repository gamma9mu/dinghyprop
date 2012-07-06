package cs412.dinghyprop.simulator;

import java.util.Random;

public class SimulatorRandom {
	private int sizeX, sizeY, numObstacles;
	private Random ran;
	private Simulator sim;

	public SimulatorRandom(int maxX, int maxY, int maxObstacles) {
		sizeX = maxX;
		sizeY = maxY;
		ran = new Random();
		numObstacles = ran.nextInt(maxObstacles) + 1;
		
		int dinghyX = ran.nextInt(sizeX);
		int dinghyY = ran.nextInt(sizeY);
		
		sim = new Simulator(sizeX, sizeY, numObstacles, dinghyX, dinghyY);
		
		System.out.println("Dinghy Position: " + dinghyX + " " + dinghyY);
		
		setGoal();
		
		setObstacles();
		
	}
	
	public void setGoal() {
		int goalX = ran.nextInt(sizeX);
		int goalY = ran.nextInt(sizeY);
		sim.setGoal(goalX, goalY);
		
		System.out.println("Goal: " + goalX + " " + goalY);
	}
	
	public void setObstacles() {
		for (int i = 0; i < numObstacles; i++) {
			int posX = ran.nextInt(sizeX);
			int posY = ran.nextInt(sizeY);
			
			sim.addObstacle(i, posX, posY);
			
		}
	}
	
	public String toString(){
		String result = "";
		result += "Distance to goal: " + sim.getGoalDistanceMetric();
		return result;
	}
}