package cs412.dinghyprop.simulator;

import java.lang.Math;
public class Point {
	private int posX;
	private int posY;
	
	public Point(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public int[] getPosition() {
		int position[] = {posX, posY};
		return position;
	}
	
	protected void setX(int x) {
		posX = x;
	}
	
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
		int result = 0;
		
		int pos[] = p.getPosition();
		
		int distX = pos[0] - this.posX;
		int distY = pos[1] - this.posY;
		
		result = (int)Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
		return result;
	}
	
	protected int getDistanceX(Point p) {
		int result = 0;
	
		int pos[] = p.getPosition();
		
		result = pos[0] - this.posX;
		
		return result;
	}
	
	protected int getDistanceY(Point p) {
		int result = 0;
		
		int pos[] = p.getPosition();
		
		result = pos[1] - this.posY;
		
		return result;
	}
}