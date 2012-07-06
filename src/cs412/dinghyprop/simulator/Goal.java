package cs412.dinghyprop.simulator;

public class Goal extends Point{
	
	public Goal(int x, int y){
		super(x, y);
	}
	
	public boolean success(Dinghy dinghy) {
		int dinghyPos[] = dinghy.getPosition();
		int goalPos[] = this.getPosition();
		boolean success = false;
		
		if (dinghyPos[0] == goalPos[0] && dinghyPos[1] == goalPos[1]){
			success = true;
		}
		
		return success;
	}
}