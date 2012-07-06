package cs412.dinghyprop.simulator;

import java.util.Random;

public class randomSimTest{
	private static final int SIZEX = 300;
	private static final int SIZEY = 300;
	private static final int MAX_OBSTACLE = 20;

	public static void main(String args[]) {
		
		SimulatorRandom sim = new SimulatorRandom(SIZEX, SIZEY, MAX_OBSTACLE);
		
		System.out.println(sim);
		//sim.moveDinghy(20, 40);
		//System.out.println("Distance from goal: " + sim);
	}
}