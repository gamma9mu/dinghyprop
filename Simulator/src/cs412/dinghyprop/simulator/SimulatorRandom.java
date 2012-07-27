package cs412.dinghyprop.simulator;

import java.security.SecureRandom;
import java.util.Random;

/**
 * This class creates a random simulation environment based on specified size and number of obstacles
 */
public class SimulatorRandom {
	private int sizeX, sizeY, numObstacles;
	private Random ran;
    private Simulator sim;

    /**
     * Constructor that makes the simulation environment
     * @param maxX The maximum X size of the simulation environment
     * @param maxY The maximum Y size of the simulation environment
     * @param maxObstacles The maximum number of obstacles in the simulation environment
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
     * Sets the goal to a random position within the simulation environment
     */
	public void setGoal() {
		int goalX = ran.nextInt(sizeX);
		int goalY = ran.nextInt(sizeY);
		sim.setGoal(goalX, goalY);
	}

    /**
     * Sets the obstacles to random positions within the simulation environment
     */
	public void setObstacles() {
		for (int i = 0; i < numObstacles; i++) {
			int posX = ran.nextInt(sizeX);
			int posY = ran.nextInt(sizeY);

			sim.addObstacle(i, posX, posY);
		}
	}

    /**
     * Returns the Simulator
     * @return The current simulator
     */
    public ISimulator getSimulator() {
        return sim;
    }

	public String toString(){
		String result = "";
		result += "Distance metric: " + sim.getGoalDistanceMetric();
		return result;
	}
}
