package cs412.dinghyprop.simulator;

import java.lang.Math;
/**
 * Simulator skeleton.
 */
public class Simulator {
	private Goal goal;
	private Obstacle[] obstacles;
	private Dinghy dinghy;
	private int sizeX, sizeY, numObstacles;
	
	public Simulator(int maxX, int maxY, int numObstacles, int dinghyX, int dinghyY) {
		sizeX = maxX;
		sizeY = maxY;
		this.numObstacles = numObstacles;
		obstacles = new Obstacle[this.numObstacles];	
		dinghy = new Dinghy(dinghyX, dinghyY);
		
	}
	
	public void addObstacle(int index, int x, int y) {
		obstacles[index] = new Obstacle(x, y);
	}
	
	public void setGoal(int x, int y){
		goal = new Goal(x, y);
	}
	
    public void invoke(String function) {}
    public int  reference(String variable) { return 0; }
	
	
    public int getTravelMetric() { 
		return dinghy.getDistTravelled();
	}
	
    public int getGoalDistanceMetric() { 
		int dinghyPos[] = dinghy.getPosition();
		int goalPos[] = goal.getPosition();
		int result = 0;
		result = (int)Math.sqrt(Math.pow(dinghyPos[1] - goalPos[1], 2) + Math.pow(dinghyPos[0] - goalPos[0], 2));
		return result;
	}
    public int getSuccessMetric() { return 0; }

    public int getFitness() {
        return getGoalDistanceMetric() + getSuccessMetric() + getTravelMetric();
    }
}
