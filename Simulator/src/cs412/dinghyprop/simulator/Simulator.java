/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

import java.util.Observable;

/**
 * Dinghy environment simulator
 * <p>
 * Manages the state of a simulation and provides variable referencing and
 * function invocation to interpreters.
 */
public class Simulator extends Observable implements ISimulator {
    private static final long serialVersionUID = 3186189958128685645L;

    /**
     * The default fitness for termination
     */
	private static final int DEFAULT_TERMINATION_FITNESS = 300;

    /**
     * The goal of the simulation
     */
	private Goal goal = null;

    /**
     * The obstacles in the simulation
     */
	private Obstacle[] obstacles;

    /**
     * The dinghy in the simulation
     */
	private Dinghy dinghy;

    /**
     * Initial start to goal distance
     */
    private int startToGoalDist = 0;

    /**
     * The width of the simulation
     */
	private int sizeX;

    /**
     * The height of the simulation
     */
    private int sizeY;

    /**
     * Determines whether simulation can continue
     */
	private boolean canContinue = true;

    /**
     * Stores the current termination fitness
     */
	private int terminationFitness = DEFAULT_TERMINATION_FITNESS;

	/**
	 * Sets up the simulator environment.
     *
	 * @param maxX the width of the simulator
	 * @param maxY the height of the simulator
	 * @param numObstacles the number of obstacles in the simulator
	 * @param dinghyX the initial X position of the dinghy
	 * @param dinghyY the initial Y position of the dinghy
	 */
	public Simulator(int maxX, int maxY, int numObstacles, int dinghyX, int dinghyY) {
		sizeX = maxX;
		sizeY = maxY;
		obstacles = new Obstacle[numObstacles];
		dinghy = new Dinghy(dinghyX, dinghyY);
	}
	
	/**
	 * Moves an obstacle to the simulation environment.
     *
	 * @param index the index of the obstacle
	 * @param x the new x-position of this obstacle
	 * @param y the new y-position of this obstacle
	 */
	public void addObstacle(int index, int x, int y) {
		obstacles[index] = new Obstacle(x, y);
	}
	
	/**
	 * @param x the x-position of the goal
	 * @param y the y-position of the goal
	 */
	public void setGoal(int x, int y){
		goal = new Goal(x, y);
        startToGoalDist = dinghy.getDistance(goal);
	}
	
	@Override
    public void invoke(String function) throws UnknownFunctionException {
        if (function.compareTo("move") == 0)
            invokeMove();
        else if (function.compareTo("turn-left") == 0)
            dinghy.turnLeft();
        else if (function.compareTo("turn-right") == 0)
            dinghy.turnRight();
        else
            throw new UnknownFunctionException(function);

        setChanged();
        notifyObservers();
	}
	
	/**
	 * This method moves the dinghy one spot in its current direction. It also
     * wraps the dinghy to the other side of the simulation if it reaches the
     * edge.
     *
     * This method also checks whether the dinghy has collided with an obstacle
     * or has reached the goal.
	 */
	private void invokeMove() {
		dinghy.move();
		dinghy.wrap(sizeX, sizeY);
        referenceFront(1);
        if (dinghy.getDistance(goal) == 0)
            canContinue = false;
	}

	@Override
    public int reference(String variable) throws VariableReferenceException {
		int[] goalPos = goal.getPosition();
		int[] pos = dinghy.getPosition();
		int min = sizeX + sizeY;

		if (variable.compareTo("front") == 0) {
			return referenceFront(min);
		} else if (variable.compareTo("short-left") == 0) {
			return referenceShortLeft(min);
		} else if (variable.compareTo("short-right") == 0) {
			return referenceShortRight(min);
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

		throw new VariableReferenceException(variable);
	}

	/**
	 * Handle a reference to the variable "front".
     *
	 * @param upperBound    a suggested upper bound
	 * @return  the distance to the closest item in front of the dinghy
	 */
	private int referenceFront(int upperBound) {
		for (Obstacle obstacle : obstacles) {
			int temp = dinghy.getDistanceFront(obstacle);
            if (temp == 0)
                canContinue = false;
            if (temp < upperBound && temp != -1 && temp != 0)
                return temp;
		}
		return upperBound;
	}

	/**
	 * Handle a reference to the variable "left".
     *
	 * @param upperBound    a suggested upper bound
	 * @return  the distance to the closest item to the left of the dinghy
	 */
	private int referenceLeft(int upperBound) {
		for (Obstacle obstacle : obstacles) {
			int temp = dinghy.getDistanceLeft(obstacle);
            if (temp == 0)
                canContinue = false;
            if (temp < upperBound && temp != -1 && temp != 0)
                return temp;
		}
		return upperBound;
	}

	/**
	 * Handle a reference to the variable "right".
     *
	 * @param upperBound    a suggested upper bound
	 * @return  the distance to the closest item to the right of the dinghy
	 */
	private int referenceRight(int upperBound) {
		for (Obstacle obstacle : obstacles) {
			int temp = dinghy.getDistanceRight(obstacle);
            if (temp == 0)
                canContinue = false;
            if (temp < upperBound && temp != -1 && temp != 0)
				return temp;

		}
		return upperBound;
	}

	/**
	 * Handle a reference to the variable "rear".
     *
	 * @param upperBound    a suggested upper bound
	 * @return  the distance to the closest item to the rear of the dinghy
	 */
	private int referenceRear(int upperBound) {
		for (Obstacle obstacle : obstacles) {
			int temp = dinghy.getDistanceRear(obstacle);
            if (temp == 0)
                canContinue = false;
            if (temp < upperBound && temp != -1 && temp != 0)
				return temp;
		}
		return upperBound;
	}
	
	/**
	 * Handle a reference to the variable "short-left"
     *
	 * @param upperBound    a suggested upper bound
	 * @return the distance to the closest item at a 45 degree angle to the
     * left of the dinghy
	 */
	private int referenceShortLeft(int upperBound) {
		for (Obstacle obstacle : obstacles) {
			int temp = dinghy.getDistanceShortLeft(obstacle);
            if (temp == 0)
                canContinue = false;
            if(temp < upperBound && temp !=-1 && temp != 0)
				return temp;
		}
		return upperBound;
	}
	
	/**
	 * Handle a reference to the variable "short-right"
	 * @param upperBound    a suggested upper bound
	 * @return the distance to the closest item at a 45 degree angle to the
     * right of the dinghy
	 */
	private int referenceShortRight(int upperBound) {
		for (Obstacle obstacle : obstacles) {
			int temp = dinghy.getDistanceShortRight(obstacle);
            if (temp == 0)
                canContinue = false;
            if(temp < upperBound && temp != -1 && temp !=0)
				return temp;
		}
		return upperBound;
	}

	/**
	 * @return the distance travelled (capped at 100)
	 */
	public int getTravelMetric() {
		int travelMetric = dinghy.getDistTravelled();
		travelMetric = (travelMetric > 100) ? 100 : travelMetric;
		return travelMetric;
	}

    /**
	 * @return the percent improvement in the dinghy's distance from the goal
	 */
	public int getGoalDistanceMetric() {
		int improvement = startToGoalDist - dinghy.getDistance(goal);
        improvement = (improvement < 0) ? 0 : improvement;
        return 100 * (improvement / startToGoalDist);
	}
	
	/**
	 * @return 100 if the goal was reached, 0 if it was not.
	 */
	public int getSuccessMetric() {
        if(goal.success(dinghy))
            return 100;
        return 0;
    }

	/**
	 * Calculates the fitness of the program. This is calculated by adding the
     * goal distance metric, the success metric, and the travel metric.
     *
	 * @return the fitness of the program
	 */
	@Override
    public int getFitness() {
		return getGoalDistanceMetric() + getSuccessMetric() + getTravelMetric();
	}

	/**
	 * @return  whether execution can continue
	 */
	@Override
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

	@Override
    public int getTerminationFitness() {
		return terminationFitness;
	}

	/**
	 * @param terminationFitness    the new goal fitness
	 */
	public void setTerminationFitness(int terminationFitness) {
		this.terminationFitness = terminationFitness;
	}
	
	/**
	 * @return the size of the simulation environment.
	 */
	public int[] getSize() {
        return new int[]{sizeX, sizeY};
	}
	
	/**
	 * @return The position of the goal.
	 */
	public int[] getGoal() {
		return goal.getPosition();
	}
	
	/**
	 * @return the array of obstacles.
	 */
	public Obstacle[] getObstacles() {
		return obstacles;
	}

    /**
     * @return xy-array of the dinghy's current position
     */
    public int[] getDinghy() {
		return dinghy.getPosition();
	}
}
