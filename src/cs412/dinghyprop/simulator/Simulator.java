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
	
	public void moveDinghy(int x, int y) {
		dinghy.movePos(x, y);
	}
	
	public double getTotalDistance() {
		double result = 0;
		result = (int)Math.sqrt(Math.pow(sizeX, 2) + Math.pow(sizeY, 2));
		return result;
	}
	
    public int getGoalDistanceMetric() { 
		int dinghyPos[] = dinghy.getPosition();
		int goalPos[] = goal.getPosition();
		double goalDist = 0;
		double result = 0;
		goalDist = goal.getDistance(dinghy);
		result = 100 - (goalDist / getTotalDistance()) * 100;
		return (int)result;
	}
    public int getSuccessMetric() { 
		boolean success = goal.success(dinghy);
		int points = 0;
		if(success){
			points = 100;
		}
		return points;
	}

    public int getFitness() {
        return getGoalDistanceMetric() + getSuccessMetric() + getTravelMetric();
    }
}
