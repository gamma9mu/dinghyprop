package cs412.dinghyprop.simulator;

/**
*  This class stores current information about the
*  dinghy that is in the simulation
*/
public class Dinghy extends Point{
	private int distTravelled;



    private enum Direction { NORTH, EAST, SOUTH, WEST;}
    private Direction direc;

	public Dinghy(int startX, int startY) {
		super(startX, startY);
		distTravelled = 0;
		direc = Direction.NORTH;
	}

    public Dinghy(Dinghy dinghy) {
        super(dinghy.getPosition()[0], dinghy.getPosition()[1]);
        distTravelled = dinghy.distTravelled;
        direc = dinghy.direc;
    }

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

        if (currPos[1] > y)
            setY(currPos[1] - y);
    }

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
	
	protected void turnRight() {
        direc = rightDirection();
    }
	
	protected void turnLeft() {
        direc = leftDirection();
    }
	
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
	
	protected int getDistTravelled() {
		return distTravelled;
	}
	
	private int calculateDistTravel(int changeX, int changeY, int[] currPos) {
		return (int)Math.sqrt(Math.pow(changeY - currPos[1], 2) + Math.pow(changeX - currPos[0], 2));
	}

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

    protected int getDistanceFront(Obstacle obst) {
        return getDistanceInDirection(direc, obst);
    }

	protected int getDistanceLeft(Obstacle obst) {
        return getDistanceInDirection(leftDirection(), obst);
	}
	
	protected int getDistanceRight(Obstacle obst) {
        return getDistanceInDirection(rightDirection(), obst);
	}
	
	protected int getDistanceRear(Obstacle obst) {
        return getDistanceInDirection(rearDirection(), obst);
	}
	
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
	
	private int getSlope(int[] obstaclePosition, int[] dinghyPosition) {
		return (obstaclePosition[1] - dinghyPosition[1]) / (obstaclePosition[0] - dinghyPosition[0]);
	}
	
	private boolean checkSlope(int[] obstaclePosition, int[] dinghyPosition, int expectedValue) {
		boolean result = false;
		if(getSlope(obstaclePosition, dinghyPosition) == expectedValue) {
			result = true;
		}
		
		return result;
	}
}
