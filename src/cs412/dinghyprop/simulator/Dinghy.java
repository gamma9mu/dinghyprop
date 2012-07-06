package cs412.dinghyprop.simulator;

/**
*  This class stores current information about the
*  dinghy that is in the simulation
*/

public class Dinghy{
	private int positionX, positionY;
	
	public Dinghy(int startX, int startY) {
		positionX = startX;
		positionY = startY;
	}
	
	public int[] getPosition(){
		int position[] = {positionX, positionY};
		return position;
	}
}