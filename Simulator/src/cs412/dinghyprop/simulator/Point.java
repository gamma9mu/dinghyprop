/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

import java.io.Serializable;

/**
 * This is the superclass for Goal, Obstacle, and Dinghy. This class stores
 * position information and provides methods to retrieve and manipulate that
 * information.
 */
public class Point implements Serializable{

    /**
     * the current X position
     */
	private int posX;

    /**
     * the current Y position.
     */
    private int posY;
	
	/**
	 * Sets the initial position of the point.
     *
	 * @param posX Initial X value of point
	 * @param posY Initial Y value of point
	 */
	public Point(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	/**
	 * @return xy-array containing the current position
	 */
	public int[] getPosition() {
        return new int[]{posX, posY};
	}
	
	/**
	 * @param x the new X position
	 */
	protected void setX(int x) {
		posX = x;
	}
	
	/**
	 * @param y the new Y position
	 */
	protected void setY(int y) {
		posY = y;
	}
	
	/**
	 * Increases the X position.
     *
	 * @param x the amount to increase X by
	 */
	protected void incX(int x) {
		posX += x;
	}
	
	/** 
	 * Increases the Y position.
     *
	 * @param y the amount to increase Y by
	 */
	protected void incY(int y) {
		posY += y;
	}
	
	/**
	 * @param p another point
	 * @return the distance between this point and {@code p}
	 */
	protected int getDistance(Point p) {
        int[] pos = p.getPosition();
		
		int distX = pos[0] - this.posX;
		int distY = pos[1] - this.posY;

        return (int) Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
	}
	
	/**
	 * Calculates the distance to another point along the x-axis.
     *
	 * @param p the other point
	 * @return the x-component distance between this point and {@code p}
	 */
	protected int getDistanceX(Point p) {
        int[] pos = p.getPosition();
        return pos[0] - this.posX;
	}

    /**
     * Calculates the distance to another point along the y-axis.
     *
     * @param p the other point
     * @return the y-component distance between this point and {@code p}
     */
	protected int getDistanceY(Point p) {
        int[] pos = p.getPosition();
        return pos[1] - this.posY;
	}
}
