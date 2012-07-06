package cs412.dinghyprop.simulator;

import java.lang.Math;
/**
*  This class stores current information about the
*  dinghy that is in the simulation
*/

public class Dinghy extends Point{
	private int positionX, positionY;
	private int distTravelled;
	
	public Dinghy(int startX, int startY) {
		super(startX, startY);
		distTravelled = 0;
	}
	
	public void movePos(int distX, int distY){
		int currPos[] = this.getPosition();
		distTravelled += Math.sqrt(Math.pow(distY - currPos[1], 2) + Math.pow(distX - currPos[0], 2));
		this.setX(currPos[0] + distX);
		this.setY(currPos[1] + distY);
	}
}