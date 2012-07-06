package cs412.dinghyprop.simulator;

import java.util.Random;

public class randomSimTest{
	private static final int SIZEX = 300;
	private static final int SIZEY = 300;
	private static final int NUM_OBSTACLE = 5;

	public static void main(String args[]) {
		
		SimulatorRandom sim = new SimulatorRandom(SIZEX, SIZEY, NUM_OBSTACLE);
		
		System.out.println("Distance from goal: " + sim);
		//sim.moveDinghy(20, 40);
		//System.out.println("Distance from goal: " + sim);
	}
}