package cs412.dinghyprop.simulator;

import java.io.Serializable;

/**
*  This is the superclass for Goal, Obstacle,
*  and Dinghy. This class stores position information
*  and provides methods to retrieve and manipulate
*  that information
*/
public class Point implements Serializable{

	// Variable to store the current X position.
	private int posX;
	// Variable to store the current Y position.
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
	
	/**
	*  This method increases the X position by the
	*  given amount.
	*  @param x The amount to increase X by.
	*/
	protected void incX(int x) {
		posX += x;
	}
	
	/** 
	*  This method increases the Y position by the
	*  given amount.
	*  @param y The amount to increase Y by.
	*/
	protected void incY(int y) {
		posY += y;
	}
	
	/**
	*  This method calculates the distance between
	*  this point and the provided point p.
	*  @param p The other point used to calculated distance
	*  @return The distance between this point and the given point p.
	*/
	protected int getDistance(Point p) {
        int[] pos = p.getPosition();
		
		int distX = pos[0] - this.posX;
		int distY = pos[1] - this.posY;

        return (int) Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
	}
	
	/**
	*  This method calculates the distance between this point's
	*  X value and the given point p's X value.
	*  @param p The other point used to calculate distance.
	*  @return The distance between the X values of the two points.
	*/
	protected int getDistanceX(Point p) {
        int[] pos = p.getPosition();
        return pos[0] - this.posX;
	}
	
	/**
	*  This method calculates the distance between this point's
	*  X value and the given point p's X value.
	*  @param p The other point used to calculate distance.
	*  @return The distance between the X values of the two points.
	*/
	protected int getDistanceY(Point p) {
        int[] pos = p.getPosition();
        return pos[1] - this.posY;
	}
}
