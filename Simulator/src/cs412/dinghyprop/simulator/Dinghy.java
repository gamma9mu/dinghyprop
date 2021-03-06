/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

/**
 * This class stores current information about the dinghy in a simulation.
 */
public class Dinghy extends Point{
    private static final long serialVersionUID = -4129944364781066537L;

    /**
     * Stores the total distance travelled
     */
	private int distTravelled;

    /**
     * Enumeration to represent the direction of the dinghy
     */
    private enum Direction { NORTH, EAST, SOUTH, WEST }

    /**
     * Stores current direction of dinghy
     */
    private Direction direc;

    /**
     * Constructor that sets up initial position and direction of the dinghy.
     *
     * @param startX The initial X position of the dinghy.
     * @param startY The initial Y position of the dinghy.
     */
	public Dinghy(int startX, int startY) {
		super(startX, startY);
		distTravelled = 0;
		direc = Direction.NORTH;
	}
	
	/**
 	 * This constructor allows for the dinghy to be cloned to allow for the
     * same simulation to be used by all slave programs.
     *
	 * @param dinghy This is the dinghy that needs to be cloned
	 */
	public Dinghy(Dinghy dinghy) {
		super(dinghy.getPosition()[0], dinghy.getPosition()[1]);
		distTravelled = dinghy.distTravelled;
		direc = dinghy.direc;
	}

    /**
     * Wrap the dinghy around the map.
     *
     * @param x    The x dimension of the map
     * @param y    The y dimension of the map
     */
	public void wrap(int x, int y) {
        int[] currPos = getPosition();

        if (currPos[0] > x)
            setX(currPos[0] - x);

        if (currPos[0] < 0)
            setX(currPos[0] + x);

        if (currPos[1] > y)
            setY(currPos[1] - y);

        if (currPos[1] < 0)
            setY(currPos[1] + y);
	}

	/**
	 * Moves the dinghy a given distance in the current direction.
     */
	protected void move() {
		distTravelled++;

		switch(direc) {
			case NORTH:
				incY(1);
				break;
			case EAST:
				incX(1);
				break;
			case SOUTH:
				incY(-1);
				break;
			case WEST:
				incX(-1);
				break;
		}
	}
	
	/**
	 * Turns the dinghy to the right.
	 */
	protected void turnRight() {
		direc = rightDirection();
	}
	
	/**
	 * Turns the dinghy to the left.
	 */
	protected void turnLeft() {
		direc = leftDirection();
	}
	
	/**
	 * @return the current direction of the dinghy in degrees
	 */
	protected int getDirection() {
		int result = 0;
			switch(direc) {
				case NORTH:
					result = 0;
					break;
				case EAST:
					result = 90;
					break;
				case SOUTH:
					result = 180;
					break;
				case WEST:
					result = 270;
					break;
			}
		return result;
	}
	
	/**
	 * @return the current total distance travelled by the dinghy
	 */
	protected int getDistTravelled() {
		return distTravelled;
	}

    /**
	 * @return the direction to the left of the dinghy
	 */
	private Direction leftDirection() {
		switch (direc) {
			case NORTH:
				return Direction.WEST;
			case EAST:
				return Direction.NORTH;
			case SOUTH:
				return Direction.EAST;
			case WEST:
			default:
				return Direction.SOUTH;
		}
	}

    /**
	 * @return the direction to the right of the dinghy
	 */
	private Direction rightDirection() {
		switch (direc) {
			case NORTH:
				return Direction.EAST;
			case EAST:
				return Direction.SOUTH;
			case SOUTH:
				return Direction.WEST;
			case WEST:
			default:
				return Direction.NORTH;
		}
	}

	/**
	 * @return the direction to the rear of the dinghy
	 */
	private Direction rearDirection() {
		switch (direc) {
			case NORTH:
				return Direction.SOUTH;
			case EAST:
				return Direction.WEST;
			case SOUTH:
				return Direction.WEST;
			case WEST:
			default:
				return Direction.NORTH;
		}
	}

	/**
	 * Calculates the distance to the given obstacle in a specific direction.
     *
	 * @param direction the direction used to calculate distance.
	 * @param obst the obstacle to which distance will be calculated.
	 * @return the distance to the obstacle in the given direction(-1 if obstacle not in direction).
	 */
	protected int getDistanceInDirection(Direction direction, Obstacle obst) {
		int[] obstPos = obst.getPosition();
		int[] dinghyPos = this.getPosition();
		int result = 0;
		int temp;
		switch(direction) {
			case NORTH:
				temp = obstPos[1] - dinghyPos[1];
				if(temp >= 0 && (obstPos[0] - dinghyPos[0]) == 0)
					result = temp;
				else
					result = -1;
				break;
			case EAST:
				temp = obstPos[0] - dinghyPos[0];
				if(temp >= 0 && (obstPos[1] - dinghyPos[1]) == 0)
					result = temp;
				else
					result = -1;
				break;
			case SOUTH:
				temp = dinghyPos[1] - obstPos[1];
				if(temp >= 0 && (dinghyPos[0] - obstPos[0]) == 0)
					result = temp;
				else
					result = -1;
				break;
			case WEST:
				temp = dinghyPos[0] - obstPos[0];
				if(temp >= 0 && (dinghyPos[1] - obstPos[1]) == 0)
					result = temp;
				else
					result = -1;
				break;
		}
		return result;
	}
	
	/**
	 * @param obst an obstacle
	 * @return the distance to the obstacle or -1 if it is not in front of the
     * dinghy
	 */
    protected int getDistanceFront(Obstacle obst) {
        return getDistanceInDirection(direc, obst);
    }

    /**
     * @param obst an obstacle
     * @return the distance to the obstacle or -1 if it is not in to the left
     * of the dinghy
     */
	protected int getDistanceLeft(Obstacle obst) {
        return getDistanceInDirection(leftDirection(), obst);
	}

    /**
     * @param obst an obstacle
     * @return the distance to the obstacle or -1 if it is not in to the right
     * of the dinghy
     */
	protected int getDistanceRight(Obstacle obst) {
        return getDistanceInDirection(rightDirection(), obst);
	}

    /**
     * @param obst an obstacle
     * @return the distance to the obstacle or -1 if it is not in behind the
     * dinghy
     */
	protected int getDistanceRear(Obstacle obst) {
        return getDistanceInDirection(rearDirection(), obst);
	}
	
	/**
	 * @param obst an obstacle
	 * @return the distance to the obstacle or -1 if it is not at 45 degree
     * angle to the left of the dinghy
	 */
	protected int getDistanceShortLeft(Obstacle obst) {
		int[] obstaclePosition = obst.getPosition();
		int[] dinghyPosition = this.getPosition();

        if (direc == Direction.NORTH && checkConditions(obstaclePosition, dinghyPosition, -1, '>'))
            return this.getDistance(obst);
        if (direc == Direction.EAST && checkConditions(obstaclePosition, dinghyPosition, 1, '>'))
            return this.getDistance(obst);
        if (direc == Direction.SOUTH && checkConditions(obstaclePosition, dinghyPosition, -1, '<'))
            return this.getDistance(obst);
        if (direc == Direction.WEST && checkConditions(obstaclePosition, dinghyPosition, 1, '<'))
            return this.getDistance(obst);

		return -1;
	}
	
    /**
     * @param obst an obstacle
     * @return the distance to the obstacle or -1 if it is not at 45 degree
     * angle to the right of the dinghy
     */
	protected int getDistanceShortRight(Obstacle obst) {
		int[] obstaclePosition = obst.getPosition();
		int[] dinghyPosition = this.getPosition();

        if (direc == Direction.NORTH && checkConditions(obstaclePosition, dinghyPosition, 1, '>'))
            return this.getDistance(obst);
        if (direc == Direction.EAST && checkConditions(obstaclePosition, dinghyPosition, -1, '<'))
            return this.getDistance(obst);
        if (direc == Direction.SOUTH && checkConditions(obstaclePosition, dinghyPosition, 1, '<'))
            return this.getDistance(obst);
        if (direc == Direction.WEST && checkConditions(obstaclePosition, dinghyPosition, -1, '>'))
            return this.getDistance(obst);

        return -1;
	}
	
	/**
	 * Checks to make sure that the slope is correct and the obstacle is in the
     * correct plane for the slope.
     *
	 * @param obstaclePos xy-array that stores the current obstacle position
	 * @param dinghyPos xy-array that stores the current dinghy position
	 * @param expectedVal the expected slope value
	 * @param oper comparison specified as '&lt;' or '&gt;'
	 * @return true if slope is correct and obstacle is in correct plane.
	 */
	private boolean checkConditions(int[] obstaclePos, int[] dinghyPos, int expectedVal, char oper) {
        if (oper == '<')
            return getSlope(obstaclePos, dinghyPos) == expectedVal && obstaclePos[1] < dinghyPos[1];
		else // oper == '>'
            return getSlope(obstaclePos, dinghyPos) == expectedVal && obstaclePos[1] > dinghyPos[1];
	}
	
	/**
	 * Calculates the slope between the dinghy and a given obstacle.
     *
	 * @param obstaclePosition xy-array that stores the current obstacle position
	 * @param dinghyPosition xy-array that stores the current dinghy position
	 * @return the slope between the dinghy and the obstacle
	 */
	private int getSlope(int[] obstaclePosition, int[] dinghyPosition) {
		int result = 0;
		int numerator = obstaclePosition[1] - dinghyPosition[1];
		int denominator = obstaclePosition[0] - dinghyPosition[0];
		if(denominator != 0) {
			result = numerator / denominator;
		}
		
		return result;
	}
}
