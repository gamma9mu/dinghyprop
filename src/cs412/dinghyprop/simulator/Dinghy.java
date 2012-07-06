package cs412.dinghyprop.simulator;

import java.lang.Math;
/**
*  This class stores current information about the
*  dinghy that is in the simulation
*/

public class Dinghy extends Point{
	private int distTravelled;
	private enum Direction { NORTH, EAST, SOUTH, WEST };
	private Direction direc;
	
	public Dinghy(int startX, int startY) {
		super(startX, startY);
		distTravelled = 0;
		direc = Direction.NORTH;
	}
	
	protected void movePos(int distX, int distY){
		int currPos[] = this.getPosition();
		distTravelled += Math.sqrt(Math.pow(distY - currPos[1], 2) + Math.pow(distX - currPos[0], 2));
		this.setX(currPos[0] + distX);
		this.setY(currPos[1] + distY);
	}
	
	protected int getDistTravelled() {
		return distTravelled;
	}
	
	protected void turnRight() {
		switch(direc) {
			case NORTH:
				direc = Direction.EAST;
				break;
			case EAST:
				direc = Direction.SOUTH;
				break;
			case SOUTH:
				direc = Direction.WEST;
				break;
			case WEST:
				direc = Direction.NORTH;
				break;
		}
	}
}