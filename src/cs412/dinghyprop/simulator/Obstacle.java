package cs412.dinghyprop.simulator;

import java.lang.Math;

public class Obstacle extends point{
	
	public Obstacle(int x, int y) {
		super(x, y);
	}
	
	public int getDistance(int dinghyX, int dinghyY) { 
		int result = 0;
		int position[] = this.getPosition();
		int distY = dinghyY - position[1];
		int distX = dinghyX - position[0];
		result = (int) Math.sqrt(Math.pow(distY, 2.0) + Math.pow(distX, 2.0));
		
		return result;
	}
	
		
}