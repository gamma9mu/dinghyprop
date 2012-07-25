package cs412.dinghyprop.simulator;

import java.util.LinkedList;
import java.util.Observer;
import java.util.Observable;

public class History implements Observer {
	private LinkedList<Integer> xValues;
	private LinkedList<Integer> yValues;
	private int count;
	
	public History(int posX, int posY) {
		xValues = new LinkedList<Integer>();
		yValues = new LinkedList<Integer>();
		count = 0;
		xValues.add(posX);
		yValues.add(posY);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Dinghy temp = (Dinghy)arg;
		int[] position = temp.getPosition();
		xValues.add(position[0]);
		yValues.add(position[1]);
		count++;
	}
	
	public int getCount() {
		return count;
	}
	
	public int getNextX() {
		if(!xValues.isEmpty())
			return xValues.poll();
		return -1;
	}
	
	public int getNextY() {
		if(!yValues.isEmpty())
			return yValues.poll();
		return -1;
	}
}
