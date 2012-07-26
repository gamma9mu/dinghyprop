package cs412.dinghyprop.simulator;

import java.security.SecureRandom;
import java.util.Random;

public class SimulatorRandom {
	private int sizeX, sizeY, numObstacles;
	private Random ran;
    private Simulator sim;

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

	public void setGoal() {
		int goalX = ran.nextInt(sizeX);
		int goalY = ran.nextInt(sizeY);
		sim.setGoal(goalX, goalY);
	}

	public void setObstacles() {
		for (int i = 0; i < numObstacles; i++) {
			int posX = ran.nextInt(sizeX);
			int posY = ran.nextInt(sizeY);

			sim.addObstacle(i, posX, posY);
		}
	}

    public ISimulator getSimulator() {
        return sim;
    }

	public String toString(){
		String result = "";
		result += "Distance metric: " + sim.getGoalDistanceMetric();
		return result;
	}
}