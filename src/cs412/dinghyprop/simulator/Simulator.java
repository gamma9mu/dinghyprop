package cs412.dinghyprop.simulator;

import java.util.Random;
/**
 * Simulator skeleton.
 */
public class Simulator {
	private Goal goal;
	private Obstacle[] obstacles;
	private int sizeX, sizeY, numObstacles;
	private final int MAX_OBSTACLES = 20;
	private Random rand = new Random();
	
	public Simulator(int maxX, int maxY) {
		sizeX = maxX;
		sizeY = maxY;
		numObstacles = rand.nextInt(MAX_OBSTACLES) + 1;
		obstacles = new Obstacle[numObstacles];
		
		
		
		
		
	}
	
	public void addObstacle(int index, int x, int y) {
		obstacles[index] = new Obstacle(x, y);
	}
	
	public void setGoal(int x, int y){
		goal = new Goal(x, y);
	}
	
    public void invoke(String function) {}
    public int  reference(String variable) { return 0; }
    public int getTravelMetric() { return 0; }
    public int getGoalDistanceMetric() { return 0; }
    public int getSuccessMetric() { return 0; }

    public int getFitness() {
        return getGoalDistanceMetric() + getSuccessMetric() + getTravelMetric();
    }
}
