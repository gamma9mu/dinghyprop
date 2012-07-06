package cs412.dinghyprop.simulator;

public class Goal{
	private int positionX;
	private int positionY;
	
	public Goal(int x, int y){
		positionX = x;
		positionY = y;
	}
	
	public int[] getPosition(){
		int position[] = {positionX, positionY};
		return position;
	}
}