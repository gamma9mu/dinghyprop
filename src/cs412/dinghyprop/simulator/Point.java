package cs412.dinghyprop.simulator;

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
}