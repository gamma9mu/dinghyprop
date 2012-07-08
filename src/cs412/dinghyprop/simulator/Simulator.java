package cs412.dinghyprop.simulator;

/**
 * Dinghy environment simulator.
 */
public class Simulator implements Cloneable {
    private static final int DEFAULT_TERMINATION_FITNESS = 300;
    private Goal goal = null;
	private Obstacle[] obstacles;
	private Dinghy dinghy;
	private int sizeX;
    private int sizeY;
    private boolean canContinue = true;
    private int terminationFitness = DEFAULT_TERMINATION_FITNESS;

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
        int[] goalPos = goal.getPosition();
		int[] pos = dinghy.getPosition();
		int min = sizeX + sizeY;

        if (variable.compareTo("front") == 0) {
            return referenceFront(min);
        } else if (variable.compareTo("short-left") == 0) {
            ;
        } else if (variable.compareTo("short-right") == 0) {
            ;
        } else if (variable.compareTo("left") == 0) {
            return referenceLeft(min);
        } else if (variable.compareTo("right") == 0) {
            return referenceRight(min);
        } else if (variable.compareTo("rear") == 0) {
            return referenceRear(min);
        } else if (variable.compareTo("position-x") == 0) {
            return pos[0];
        } else if (variable.compareTo("position-y") == 0) {
            return pos[1];
        } else if (variable.compareTo("goal-position-x") == 0) {
            return goalPos[0];
        } else if (variable.compareTo("goal-position-y") == 0) {
            return goalPos[1];
        } else if (variable.compareTo("heading") == 0) {
            return dinghy.getDirection();
        }

        System.err.println("Simulator: Unknown variable referenced: \"" + variable + '"');
		return 0;
	}

    /**
     * Handle a reference to the variable "front".
     * @param upperBound    A suggested upper bound
     * @return  the distance to the closest item in front of the dinghy.
     */
    private int referenceFront(int upperBound) {
        for (Obstacle obstacle : obstacles) {
            int temp = dinghy.getDistanceFront(obstacle);
            if (temp < upperBound && temp != -1 && temp != 0)
                upperBound = temp;
            else if (temp == 0) {
                canContinue = false;
            }
        }
        return upperBound;
    }

    /**
     * Handle a reference to the variable "left".
     * @param upperBound    A suggested upper bound
     * @return  the distance to the closest item to the left of the dinghy.
     */
    private int referenceLeft(int upperBound) {
        for (Obstacle obstacle : obstacles) {
            int temp = dinghy.getDistanceLeft(obstacle);
            if (temp < upperBound && temp != -1 && temp != 0)
                upperBound = temp;
            else if (temp == 0) {
                canContinue = false;
            }
        }
        return upperBound;
    }

    /**
     * Handle a reference to the variable "right".
     * @param upperBound    A suggested upper bound
     * @return  the distance to the closest item to the right of the dinghy.
     */
    private int referenceRight(int upperBound) {
        for (Obstacle obstacle : obstacles) {
            int temp = dinghy.getDistanceRight(obstacle);
            if (temp < upperBound && temp != -1 && temp != 0)
                upperBound = temp;
            else if (temp == 0) {
                canContinue = false;
            }
        }
        return upperBound;
    }

    /**
     * Handle a reference to the variable "rear".
     * @param upperBound    A suggested upper bound
     * @return  the distance to the closest item to the rear of the dinghy.
     */
    private int referenceRear(int upperBound) {
        for (Obstacle obstacle : obstacles) {
            int temp = dinghy.getDistanceRear(obstacle);
            if (temp < upperBound && temp != -1 && temp != 0)
                upperBound = temp;
            else if (temp == 0) {
                canContinue = false;
            }
        }
        return upperBound;
    }

    public int getTravelMetric() {
        int travelMetric = dinghy.getDistTravelled();
        travelMetric = (travelMetric > 100) ? 100 : travelMetric;
		return travelMetric;
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

    /**
     * Determine whether execution can continue.
     * @return  Whether execution can continue
     */
    public boolean canContinue() {
        return getFitness() < terminationFitness && canContinue;
    }

    @Override
    public Simulator clone() throws CloneNotSupportedException {
        Simulator clone = (Simulator) super.clone();
        clone.dinghy = new Dinghy(dinghy);
        System.arraycopy(obstacles, 0, clone.obstacles, 0, obstacles.length);
        return clone;
    }

    /**
     * Retrieve the termination fitness currently in use.
     * @return  The goal fitness
     */
    public int getTerminationFitness() {
        return terminationFitness;
    }

    /**
     * Set the termination fitness currently in use.
     * @param terminationFitness    The new goal fitness
     */
    public void setTerminationFitness(int terminationFitness) {
        this.terminationFitness = terminationFitness;
    }
}
