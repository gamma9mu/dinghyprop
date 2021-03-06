/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.simulator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

/** 
 * This class allows for simulations to be read from a file.
 * <p>
 * Required format:
 * <pre>
 * SizeX SizeY
 * DinghyX DinghyY
 * numObstacles
 * GoalX GoalY
 * Obstacle1X Obstacle1Y
 * Obstacle2X Obstacle2Y
 * (Other obstacles)
 * </pre>
*/
public class SimulatorFile {
    private static Logger log = Logger.getLogger("SimulatorFile");

    /**
     * The file storing the simulator specification
     */
    File file;

    /**
     * An input scanner for file
     */
    Scanner in = null;

    /**
     * The simulation being created
     */
    Simulator sim;

    /**
     * The specified size of the simulation
     */
    int[] size;

    /**
     * The specified dignhy position
     */
    int[] dinghy;

    /**
     * The specified goal position
     */
    int[] goal;

    /**
     * The number of obstacles specified
     */
    int numObstacles;

    /**
     * Creates and initializes a simulation environment as specified in a file.
     *
     * @param filename the file containing the simulation environment
     */
    public SimulatorFile(File filename) {
		file = filename;
		try{
			in = new Scanner(file);}
		catch(IOException e){
			log.throwing("SimulatorFile", "SimulatorFile", e);
		}
		setSize();
		setDinghy();
		numObstacles = in.nextInt();
		
		sim = new Simulator(size[0], size[1], numObstacles, dinghy[0], dinghy[1]);
		
		setGoal();
		sim.setGoal(goal[0], goal[1]);
		for(int i = 0; i < numObstacles; i++){
			int obstX = in.nextInt();
			int obstY = in.nextInt();
			sim.addObstacle(i, obstX, obstY);
		}
		
	}

    /**
     * @return the simulator constructed from the file
     */
    public Simulator getSim() {
        return sim;
    }

    /**
     * Sets the size of the simulation environment by reading from the file.
     */
	private void setSize(){
		size = new int[2];
		size[0] = in.nextInt();
		size[1] = in.nextInt();
	}

    /**
     * Sets the position of the dinghy by reading from the file.
     */
	private void setDinghy(){
		dinghy = new int[2];
		dinghy[0] = in.nextInt();
		dinghy[1] = in.nextInt();
	}

    /**
     * Sets the position of the goal by reading from the file.
     */
	private void setGoal() {
		goal = new int[2];
		goal[0] = in.nextInt();
		goal[1] = in.nextInt();
	}
}
