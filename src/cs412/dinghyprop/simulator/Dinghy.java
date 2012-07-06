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
		distTravelled += calculateDistTravel(distX, distY, currPos);
		this.setX(currPos[0] + distX);
		this.setY(currPos[1] + distY);
	}
	
	protected void move(int dist) {
		int currPos[] = this.getPosition();
		switch(direc) {
			case NORTH:
				distTravelled += calculateDistTravel(0, dist, currPos);
				this.incY(dist);
				break;
				
			case EAST:
				distTravelled += calculateDistTravel(dist, 0, currPos);
				this.incX(dist);
				break;
				
			case SOUTH:
				dist = 0 - dist;
				distTravelled += calculateDistTravel(0, dist, currPos);
				this.incY(dist);
				break;
				
			case WEST:
				dist = 0 - dist;
				distTravelled += calculateDistTravel(dist, 0, currPos);
				this.incX(dist);
				break;
		}
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
	
	protected void turnLeft() {
		switch(direc) {
			case NORTH:
				direc = Direction.WEST;
				break;
			case WEST:
				direc = Direction.SOUTH;
				break;
			case SOUTH:
				direc = Direction.EAST;
				break;
			case EAST:
				direc = Direction.NORTH;
				break;
		}
	}
	
	protected String getDirection() {
		String result = "";
		switch(direc) {
			case NORTH:
				result = "north";
				break;
			case EAST:
				result = "east";
				break;
			case SOUTH:
				result = "south";
				break;
			case WEST:
				result = "west";
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
}