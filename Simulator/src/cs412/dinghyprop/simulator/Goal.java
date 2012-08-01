/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

/**
*  This class is a subclass of Point and allows for Goal objects to be created.
*/
public class Goal extends Point{
	
	/**
	 * The constructor that sets up the position of the goal.
     *
	 * @param x The X position of the goal.
	 * @param y The Y position of the goal.
	 */
	public Goal(int x, int y){
		super(x, y);
	}
	
	/**
     * This method determines if the Dinghy has reached the goal.
     *
     * @param dinghy Reference to the dinghy to determine if it has reached the
     *               goal.
     * @return Returns true if the dinghy has reached the goal.
     */
	public boolean success(Dinghy dinghy) {
		int[] dinghyPos = dinghy.getPosition();
		int[] goalPos = this.getPosition();
		boolean success = false;
		
		if (dinghyPos[0] == goalPos[0] && dinghyPos[1] == goalPos[1]){
			success = true;
		}
		
		return success;
	}
}
