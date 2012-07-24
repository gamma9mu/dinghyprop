package cs412.dinghyprop.simulator;

import java.util.LinkedList;
import java.util.Observer;
import java.util.Observable;

public class History implements Observer {
	private LinkedList<Integer> xValues;
	private LinkedList<Integer> yValues;
	private int count;
	
	public History() {
		xValues = new LinkedList();
		yValues = new LinkedList();
		count = 0;
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
		return xValues.poll();
	}
	
	public int getNextY() {
		return yValues.poll();
	}
}
