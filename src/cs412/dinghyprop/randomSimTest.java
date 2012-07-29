/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

//package cs412.dinghyprop.simulator;
import cs412.dinghyprop.simulator.SimulatorRandom;


public class randomSimTest{
	private static final int SIZEX = 300;
	private static final int SIZEY = 300;
	private static final int MAX_OBSTACLE = 20;
	private static SimulatorRandom sim;

	public static void main(String[] args) {
		
		sim = new SimulatorRandom(SIZEX, SIZEY, MAX_OBSTACLE);
		
				
		System.out.println(sim);
		//sim.moveDinghy(20, 40);
		//System.out.println("Distance from goal: " + sim);
	}
}
