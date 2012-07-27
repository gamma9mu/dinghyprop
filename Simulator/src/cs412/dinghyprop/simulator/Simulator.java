package cs412.dinghyprop.simulator;

import java.io.Serializable;
import java.util.Observable;
/**
 * Dinghy environment simulator.
 */
public class Simulator extends Observable implements ISimulator, Serializable {
	private static final int DEFAULT_TERMINATION_FITNESS = 300;
	private Goal goal = null;
	private Obstacle[] obstacles;
	private Dinghy dinghy;
	private int sizeX;
	private int sizeY;
	private boolean canContinue = true;
	private int terminationFitness = DEFAULT_TERMINATION_FITNESS;
	static final long serialVersionUID = 3186189958128685645L;

	/**
	*  Constructor that sets up the simulator environment
	*  @param maxX The maximum X value for the simulator
	*  @param maxY The maximum Y value for the simulator
	*  @param numObstacles The number of obstacles in the simulation
	*  @param dinghyX The initial X position of the dinghy
	*  @param dinghyY The initial Y position of the dinghy
	*/
	public Simulator(int maxX, int maxY, int numObstacles, int dinghyX, int dinghyY) {
		sizeX = maxX;
		sizeY = maxY;
		obstacles = new Obstacle[numObstacles];
		dinghy = new Dinghy(dinghyX, dinghyY);
	}
	
	/**
	*  This method adds an obstacle to the simulation environment.
	*  @param index The current index in the array of obstacles
	*  @param x The X position of this obstacle
	*  @param y The Y position of this obstacle
	*/
	public void addObstacle(int index, int x, int y) {
		obstacles[index] = new Obstacle(x, y);
	}
	
	/**
	*  This method adds the goal to the simulation environment
	*  @param x The X position of the goal
	*  @param y The Y position of the goal
	*/
	public void setGoal(int x, int y){
		goal = new Goal(x, y);
	}
	
	/**
	*  This method receives an action from the interpreter and
	*  tells the dinghy to take that action.
	*  @param function The action that must be taken by the dinghy
	*  @throws UnknownFunctionException if the action is not valid.
	*/
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
	*  This method moves the dinghy one spot in its current
	*  direction. It also wraps the dinghy to the other side of the
	*  simulation if it reaches the edge.
	*/
	private void invokeMove() {
		dinghy.move(1);
		dinghy.wrap(sizeX, sizeY);
	}

	/**
	*  This method receives a reference to a variable from the interpreter
	*  and sends back its value.
	*  @param variable The variable that is referenced by the interpreter.
	*  @return The value of the referenced variable
	*  @throws VariableReferenceException If the variable does not exist
	*/
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
	
	/**
	 * Handle a reference to the variable "short-left"
	 * @param upperBound	A suggested upper bound
	 * @return the distance to the closest item at a 45 degree angle to the left of the dinghy.
	 */
	private int referenceShortLeft(int upperBound) {
		for (Obstacle obstacle : obstacles) {
			int temp = dinghy.getDistanceShortLeft(obstacle);
			if(temp < upperBound && temp !=-1 && temp != 0)
				upperBound = temp;
			else if (temp == 0) 
				canContinue = false;
							
			
		}
		return upperBound;
	}
	
	/**
	 * Handle a reference to the variable "short-right"
	 * @param upperBound	A suggested upper bound
	 * @return the distance to the closest item at a 45 degree angle to the right of the dinghy.
	 */
	private int referenceShortRight(int upperBound) {
		for (Obstacle obstacle : obstacles) {
			int temp = dinghy.getDistanceShortRight(obstacle);
			if(temp < upperBound && temp != -1 && temp !=0)
				upperBound = temp;
			else if (temp == 0)
				canContinue = false;
		}
		return upperBound;
	}

	/**
	*  Calculates the distance travelled for scoring the
	*  program
	*  @return The travel metric for scoring
	*/
	public int getTravelMetric() {
		int travelMetric = dinghy.getDistTravelled();
		travelMetric = (travelMetric > 100) ? 100 : travelMetric;
		return travelMetric;
	}
	
	/**
	*  Moves the dinghy by a given amount
	*  Not sure this is needed anymore.
	*  @param x The X value used to move the dinghy
	*  @param y The Y value used to move the dinghy
	*/
	public void moveDinghy(int x, int y) {
		dinghy.movePos(x, y);
	}
	
	/**
	*  Gets the total size of the simulation environment
	*  @return The total size of the simulation environment
	*/
	public double getTotalDistance() {
        	return Math.sqrt(Math.pow(sizeX, 2) + Math.pow(sizeY, 2));
	}
	
	/**
	*  Gets the distance to goal divided by the total distance for scoring
	*  the program.
	*  @return The goal distance metric for scoring.
	*/
	public int getGoalDistanceMetric() {
		double goalDist = goal.getDistance(dinghy);
		double result = 100 - (goalDist / getTotalDistance()) * 100;
		return (int)result;
	}
	
	/**
	*  Checks to see if the dinghy reached the goal for scoring
	*  the program.
	*  @return 100 if the goal was reached, 0 if it was not.
	*/
	public int getSuccessMetric() { 
		boolean success = goal.success(dinghy);
		int points = 0;
		if(success){
			points = 100;
		}
		return points;
	}

	/**
	*  Calculates the fitness of the program. This is calculated by adding
	*  the goal distance metric, the success metric, and the travel metric.
	*  @return The fitness of the program
	*/
	@Override
    public int getFitness() {
		return getGoalDistanceMetric() + getSuccessMetric() + getTravelMetric();
	}

	/**
	* Determine whether execution can continue.
	* @return  Whether execution can continue
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
	
	/**
	*  Gets the size of the simulation environment.
	*  @return The size of the simulation environment.
	*/
	public int[] getSize() {
		int[] size = {sizeX, sizeY};
		return size;
	}
	
	/**
	*  Gets the position of the goal.
	*  @return The position of the goal.
	*/
	public int[] getGoal() {
		return goal.getPosition();
	}
	
	/**
	*  Gets the obstacles.
	*  @return The array of obstacles.
	*/
	public Obstacle[] getObstacles() {
		return obstacles;
	}
	
	public int[] getDinghy() {
		return dinghy.getPosition();
	}
	

	
}
