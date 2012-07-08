package cs412.dinghyprop.simulator;

public class Point {
	private int posX;
	private int posY;
	
	public Point(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public int[] getPosition() {
        return new int[]{posX, posY};
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
