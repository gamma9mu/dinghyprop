package cs412.dinghyprop.simulator;

import java.lang.Math;

public class Obstacle {
	private int positionX;
	private int positionY;
	
	public Obstacle(int x, int y) {
		positionX = x;
		positionY = y;
	}
	
	public int getDistance(int dinghyX, int dinghyY) { 
		int result = 0;
		int distY = dinghyY - positionY;
		int distX = dinghyX - positionX;
		result = (int) Math.sqrt(Math.pow((double)distY, 2.0) + Math.pow((double)distX, 2.0));
		
		return result;
	}
	
		
}