package cs412.dinghyprop.simulator;

import java.util.Random;

public class randomSimTest{
	private static final int SIZEX = 300;
	private static final int SIZEY = 300;
	private static final int NUM_OBSTACLE = 5;

	public static void main(String args[]) {
		Random rand = new Random();
		
		Simulator sim = new Simulator (SIZEX, SIZEY, NUM_OBSTACLE, 0, 0);
		
		for(int i = 0; i < 5; i++) {
			int obstacleX = rand.nextInt(SIZEX);
			int obstacleY = rand.nextInt(SIZEY);
			sim.addObstacle(i, obstacleX, obstacleY);
			System.out.println("Obstacle " + i + ": " + obstacleX + " " + obstacleY);
		}
		
		int goalX = rand.nextInt(SIZEX);
		int goalY = rand.nextInt(SIZEY);
		sim.setGoal(goalX, goalY);
		System.out.println("Goal: " + goalX + " " + goalY);
		
		System.out.println("Distance from goal: " + sim.getGoalDistanceMetric());
	}
}