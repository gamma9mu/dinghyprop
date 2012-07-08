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
		if (function.compareTo("move") == 0)
            dinghy.move(3);
        else if (function.compareTo("turn-left") == 0)
            dinghy.turnLeft();
        else if (function.compareTo("turn-right") == 0)
            dinghy.turnRight();
        else
            System.err.println("Simulator: Unknown function called: " + function);
	}

    public int reference(String variable) {
		// Time to work on this now... hurray
        int[] goalPos = goal.getPosition();
		int[] pos = dinghy.getPosition();
		int min = sizeX + sizeY;

        if (variable.compareTo("front") == 0) {
            for (Obstacle obstacle : obstacles) {
                int temp = dinghy.getDistanceFront(obstacle);
                if (temp < min && temp != -1 && temp != 0)
                    min = temp;
                else if (temp == 0)
                    System.err.println("There has been a collision");
            }
        } else if (variable.compareTo("short-left") == 0) {
            ;
        } else if (variable.compareTo("short-right") == 0) {
            ;
        } else if (variable.compareTo("left") == 0) {
            ;
        } else if (variable.compareTo("right") == 0) {
            ;
        } else if (variable.compareTo("rear") == 0) {
            ;
        } else if (variable.compareTo("position-x") == 0) {
            return pos[0];
        } else if (variable.compareTo("position-y") == 0) {
            return pos[1];
        } else if (variable.compareTo("goal-position-x") == 0) {
            return goalPos[0];
        } else if (variable.compareTo("goal-position-y") == 0) {
            return goalPos[1];
        } else if (variable.compareTo("heading") == 0) {
            ;
        } else {
            System.err.println("Simulator: Unknown variable referenced: " + variable);
        }

		return 0;
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
