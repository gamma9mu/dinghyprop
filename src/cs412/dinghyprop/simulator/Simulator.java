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
	
    public void invoke(String function) {
		// I guess I should work on this part next... ugh 
		switch(function) {
			case("move"):
				dinghy.move(3);
				break;
			case("turn-left"):
				dinghy.turnLeft();
				break;
			case("turn-right"):
				dinghy.turnRight();
				break;
		}
	}
    public int  reference(String variable) { 
		// Time to work on this now... hurray
		int value = 0;
		int goalPos[] = goal.getPosition();
		int pos[] = dinghy.getPosition();
		int min = sizeX + sizeY;
		int temp;
		
		switch(variable) {
			case("front"):
				for(int i = 0; i < numObstacles; i++) {
					temp = dinghy.getDistanceFront(obstacles[i]);
					if(temp < min && temp != -1 && temp != 0)
						min = temp;
					else if(temp == 0)
						System.err.println("There has been a collision");
				}
				break;
			case("short-left"):
				break;
			case("short-right"):
				break;
			case("left"):
				break;
			case("right"):
				break;
			case("rear"):
				break;
			case("positionX"):
				value = pos[0];
				break;
			case("positionY"):
				value = pos[1];
				break;
			case("goal-position-x"):
				value = goalPos[0];
				break;
			case("goal-position-y"):
				value = goalPos[1];
				break;
			case("heading"):
				break;
		}
		
		return value;
	}
	
	
    public int getTravelMetric() { 
		return dinghy.getDistTravelled();
	}
	
	public void moveDinghy(int x, int y) {
		dinghy.movePos(x, y);
	}
	
	public double getTotalDistance() {
		double result = 0;
		result = Math.sqrt(Math.pow(sizeX, 2) + Math.pow(sizeY, 2));
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
