package cs412.dinghyprop.simulator;

/**
*  This is the superclass for Goal, Obstacle,
*  and Dinghy. This class stores position information
*  and provides methods to retrieve and manipulate
*  that information
*/
public class Point {
	private int posX;
	private int posY;
	
	/**
	*  Constructor that sets the initial position
	*  of the point.
	*  @param posX Initial X value of point
	*  @param posY Initial Y value of point
	*/	
	public Point(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	/**
	*  Method to retrieve the current position
	*  @return An array containing the current position
	*/
	public int[] getPosition() {
        return new int[]{posX, posY};
	}
	
	/**
	*  This method allows simulation to change the
	*  X value of the point
	*  @param x The new value of the X position
	*/
	protected void setX(int x) {
		posX = x;
	}
	
	/**
	*  This method allows simulation to change the
	*  Y value of the point
	*  @param y The new value of the Y position
	*/
	protected void setY(int y) {
		posY = y;
	}
	
	protected void incX(int x) {
		posX += x;
	}
	
	protected void incY(int y) {
		posY += y;
	}
	
	protected int getDistance(Point p) {
        int[] pos = p.getPosition();
		
		int distX = pos[0] - this.posX;
		int distY = pos[1] - this.posY;

        return (int) Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
	}
	
	protected int getDistanceX(Point p) {
        int[] pos = p.getPosition();
        return pos[0] - this.posX;
	}
	
	protected int getDistanceY(Point p) {
        int[] pos = p.getPosition();
        return pos[1] - this.posY;
	}
}
