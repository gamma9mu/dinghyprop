/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

import java.security.SecureRandom;
import java.util.Random;

/**
 * This class creates a random simulation environment based on specified size
 * and number of obstacles.
 */
public class SimulatorRandom {
    /**
     * The width of the created simulator
     */
	private int sizeX;

    /**
     * The height of the created simulator
     */
    int sizeY;

    /**
     * The number of obstacles in the created simulator
     */
    int numObstacles;

    /**
     * The RNG
     */
	private Random ran;

    /**
     * The simulator being created
     */
    private Simulator sim;

    /**
     * Creates a simulation environment.
     *
     * @param maxX the maximum X size of the simulation environment
     * @param maxY the maximum Y size of the simulation environment
     * @param maxObstacles the maximum number of obstacles in the simulation
     *                     environment
     */
	public SimulatorRandom(int maxX, int maxY, int maxObstacles) {
		sizeX = maxX;
		sizeY = maxY;
		ran = new SecureRandom();
		numObstacles = ran.nextInt(maxObstacles) + 1;

		int dinghyX = ran.nextInt(sizeX);
		int dinghyY = ran.nextInt(sizeY);

		sim = new Simulator(sizeX, sizeY, numObstacles, dinghyX, dinghyY);
		setGoal();
		setObstacles();
	}

    /**
     * Sets the goal to a random position within the simulation environment.
     */
	public void setGoal() {
		int goalX = ran.nextInt(sizeX);
		int goalY = ran.nextInt(sizeY);
		sim.setGoal(goalX, goalY);
	}

    /**
     * Sets the obstacles to random positions within the simulation environment.
     */
	public void setObstacles() {
		for (int i = 0; i < numObstacles; i++) {
			int posX = ran.nextInt(sizeX);
			int posY = ran.nextInt(sizeY);

			sim.addObstacle(i, posX, posY);
		}
	}

    /**
     * @return the created simulator
     */
    public ISimulator getSimulator() {
        return sim;
    }

	@Override
    public String toString() {
		String result = "";
		result += "Distance metric: " + sim.getGoalDistanceMetric();
		return result;
	}
}
