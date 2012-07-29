/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

/**
*  This class stores current information about the
*  dinghy that is in the simulation
*/
public class Dinghy extends Point{
	// Variable to store the total distance travelled
	private int distTravelled;
	// Enumeration to represent the direction of the dinghy
    private enum Direction { NORTH, EAST, SOUTH, WEST;}
	// Variable to store current direction of dinghy
    private Direction direc;

	/**
	*  Constructor that sets up initial position and
	*  direction of the dinghy.
	*  @param startX The initial X position of the dinghy.
	*  @param startY The initial Y position of the dinghy.
	*/
	public Dinghy(int startX, int startY) {
		super(startX, startY);
		distTravelled = 0;
		direc = Direction.NORTH;
	}
	
	
	/**
	*  This constructor allows for the dinghy to be cloned
	*  to allow for the same simulation to be used by all
	*  slave programs.
	*  @param dinghy This is the dinghy that needs to be cloned
	*/
	public Dinghy(Dinghy dinghy) {
		super(dinghy.getPosition()[0], dinghy.getPosition()[1]);
		distTravelled = dinghy.distTravelled;
		direc = dinghy.direc;
	}

	/**
	*  Allows for the dinghy to be moved to a new position.
	*  (Probably not needed)
	*  @param distX The X distance to move the dinghy.
	*  @param distY The Y distance to move the dinghy.
	*/
	protected void movePos(int distX, int distY){
        int[] currPos = this.getPosition();
		distTravelled += calculateDistTravel(distX, distY, currPos);
		this.setX(currPos[0] + distX);
		this.setY(currPos[1] + distY);
	}

    /**
     * Wrap the dinghy around the map.
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
	*  Moves the dinghy a given distance in the
	*  current direction.
	*  @param dist The distance to move the dinghy.
	*/
	protected void move(int dist) {
		distTravelled += dist;

		switch(direc) {
			case NORTH:
				incY(dist);
				break;
			
			case EAST:
				incX(dist);
				break;
			
			case SOUTH:
				dist = 0 - dist;
				incY(dist);
				break;
			
			case WEST:
				dist = 0 - dist;
				incX(dist);
				break;
		}
	}
	
	/**
	*  Turns the dinghy to the right.
	*/
	protected void turnRight() {
		direc = rightDirection();
	}
	
	/**
	*  Turns the dinghy to the left.
	*/
	protected void turnLeft() {
		direc = leftDirection();
	}
	
	/**
	*  Returns the current direction of the
	*  dinghy.
	*  @return Returns the direction of the dinghy as an int.
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
	*  Gets the current distance that the dinghy
	*  has travelled.
	*  @return Returns the current distance travelled
	*/
	protected int getDistTravelled() {
		return distTravelled;
	}
	
	/**
	*  Method that calculates the distance between current position
	*  and the position dinghy will move to.(Probably not needed)
	*  @return distance between two points as an int.
	*/
	private int calculateDistTravel(int changeX, int changeY, int[] currPos) {
		return (int)Math.sqrt(Math.pow(changeY - currPos[1], 2) + Math.pow(changeX - currPos[0], 2));
	}

	/**
	*  Gets the direction to the left of the dinghy
	*  @return The direction to the left of the dinghy.
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
	*  Gets the direction to the right of the dinghy.
	*  @return The direction to the right of the dinghy.
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
	*  Gets the direction to the rear of the dinghy.
	*  @return The direction to the rear of the dinghy.
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
	*  Calculates the distance to the given obstacle in a specific direction.
	*  @param direction The direction used to calculate distance.
	*  @param obst The obstacle to which distance will be calculated.
	*  @return The distance to the obstacle in the given direction(-1 if obstacle not in direction).
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
	*  Gets the distance to the given obstacle in front
	*  of the dinghy.
	*  @param obst The current obstacle in distance calculation.
	*  @return The distance to the obstacle(-1 if not in front of dinghy).
	*/
    protected int getDistanceFront(Obstacle obst) {
        return getDistanceInDirection(direc, obst);
    }

	/**
	*  Gets the distance to the given obstacle in the direction
	*  to the left of the dinghy.
	*  @param obst The current obstacle in distance calculation.
	*  @return The distance to the obstacle(-1 if obstacle not to the left of dinghy).
	*/
	protected int getDistanceLeft(Obstacle obst) {
        return getDistanceInDirection(leftDirection(), obst);
	}
	
	/**
	*  Gets the distance to the given obstacle in the direction
	*  to the right of the dinghy.
	*  @param obst The current obstacle in distance calculation.
	*  @return The distance to the obstacle(-1 if obstacle not to the right of dinghy).
	*/
	protected int getDistanceRight(Obstacle obst) {
        return getDistanceInDirection(rightDirection(), obst);
	}
	
	/**
	*  Gets the distance to the given obstacle in the direction
	*  to the rear of the dinghy.
	*  @param obst The current obstacle in distance calculation.
	*  @return The distance to the obstacle(-1 if obstacle not behind dinghy).
	*/
	protected int getDistanceRear(Obstacle obst) {
        return getDistanceInDirection(rearDirection(), obst);
	}
	
	/**
	*  Calculates the distance to the obstacle at a 45
	*  degree angle to the left of the dinghy.
	*  @param obst The current obstacle in distance calculation.
	*  @return The distance to the obstacle (-1 if not at 45 degree angle to the left).
	*/
	protected int getDistanceShortLeft(Obstacle obst) {
		int[] obstaclePosition = obst.getPosition();
		int[] dinghyPosition = this.getPosition();
		int result = -1;
		
		switch (direc) {
			case NORTH:
				if(checkConditions(obstaclePosition, dinghyPosition, -1, '>')) {
					result = this.getDistance(obst);
				}
				break;
			case EAST:
				if(checkConditions(obstaclePosition, dinghyPosition, 1, '>')) {
					result = this.getDistance(obst);
				}
				break;
			case SOUTH:
				if(checkConditions(obstaclePosition, dinghyPosition, -1, '<')) {
					result = this.getDistance(obst);
				}				
				break;
			case WEST:
				if(checkConditions(obstaclePosition, dinghyPosition, 1, '<')) {
					result = this.getDistance(obst);
				}
				break;
		}
		
		return result;
	}
	
	/**
	*  Calculates the distance to the obstacle at a 45
	*  degree angle to the right of the dinghy.
	*  @param obst The current obstacle in distance calculation.
	*  @return The distance to the obstacle(-1 if not at a 45 degree angle to the right).
	*/
	protected int getDistanceShortRight(Obstacle obst) {
		int[] obstaclePosition = obst.getPosition();
		int[] dinghyPosition = this.getPosition();
		int result = -1;
		
		switch(direc) {
			case NORTH:
				if(checkConditions(obstaclePosition, dinghyPosition, 1, '>')) {
					result = this.getDistance(obst);
				}
				break;
			case EAST:
				if(checkConditions(obstaclePosition, dinghyPosition, -1, '<')) {
					result = this.getDistance(obst);
				}
				break;
			case SOUTH:
				if(checkConditions(obstaclePosition, dinghyPosition, 1, '<')) {
					result = this.getDistance(obst);
				}
				break;
			case WEST:
				if(checkConditions(obstaclePosition, dinghyPosition, -1, '>')) {
					result = this.getDistance(obst);
				}
				break;
		}
		return result;
	}
	
	/**
	*  Checks to make sure that the slope is correct and the obstacle is in
	*  the correct plane for the slope.
	*  @param obstaclePos An array that stores the current obstacle position.
	*  @param dinghyPos An array that stores the current dinghy position.
	*  @param expectedVal Stores the expected slope value.
	*  @param oper Tells method wheter to use greater-than or less-than symbol
	*  @return Returns true if slope is correct and obstacle is in correct plane.
	*/
	private boolean checkConditions(int[] obstaclePos, int[] dinghyPos, int expectedVal, char oper) {
		boolean result = false;
		if(oper == '<'){
			if(checkSlope(obstaclePos, dinghyPos, expectedVal) && obstaclePos[1] < dinghyPos[1]){
				result = true;
			}
		}
		else if(oper == '>') {
			if(checkSlope(obstaclePos, dinghyPos, expectedVal) && obstaclePos[1] > dinghyPos[1]){
				result = true;
			}
		}
		return result;
	}
	
	/**
	*  Calculates the slope between the dinghy and the given obstacle.
	*  @param obstaclePosition An array that stores the current obstacle position
	*  @param dinghyPosition An array that stores the current dinghy position
	*  @return The slope between the dinghy and the given obstacle.
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
	
	/**
	*  Checks to see if the slope is equal to the expected value.
	*  @param obstaclePosition An array that stores the current obstacle position.
	*  @param dinghyPosition An array that stores the current dinghy position.
	*  @param expectedValue The expected slope value.
	*  @return True if the slope is equal to expectedValue.
	*/
	private boolean checkSlope(int[] obstaclePosition, int[] dinghyPosition, int expectedValue) {
		boolean result = false;
		if(getSlope(obstaclePosition, dinghyPosition) == expectedValue) {
			result = true;
		}
		
		return result;
	}
}
