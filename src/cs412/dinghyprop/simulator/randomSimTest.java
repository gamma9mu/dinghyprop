/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

public class randomSimTest{
	private static final int SIZEX = 300;
	private static final int SIZEY = 300;
	private static final int MAX_OBSTACLE = 20;

    public static void main(String[] args) {
        SimulatorRandom sim = new SimulatorRandom(SIZEX, SIZEY, MAX_OBSTACLE);
		System.out.println(sim);
	}
}
