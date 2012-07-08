package cs412.dinghyprop.simulator;

/**
 * Simulator skeleton.
 */
public class Simulator {
	private Goal goal = null;
	private Obstacle[] obstacles;
	private Dinghy dinghy;
	private int sizeX;
    private int sizeY;

    public Simulator(int maxX, int maxY, int numObstacles, int dinghyX, int dinghyY) {
		sizeX = maxX;
		sizeY = maxY;
        obstacles = new Obstacle[numObstacles];
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
		int[] goalPos;
		int[] pos = dinghy.getPosition();
		
		switch(variable) {
			case("front"):
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
				goalPos = goal.getPosition();
				value = goalPos[0];
				break;
			case("goal-position-y"):
				goalPos = goal.getPosition();
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
        return Math.sqrt(Math.pow(sizeX, 2) + Math.pow(sizeY, 2));
	}
	
    public int getGoalDistanceMetric() {
        double goalDist = goal.getDistance(dinghy);
        double result = 100 - (goalDist / getTotalDistance()) * 100;
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
