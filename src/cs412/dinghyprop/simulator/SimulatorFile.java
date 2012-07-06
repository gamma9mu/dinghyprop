package cs412.dinghyprop.simulator;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/** 
* This class allows for simulations to be read
* from a file. Required format:
* SizeX SizeY
* DinghyX DinghyY
* numObstacles 
* GoalX GoalY
* Obstacle1X Obstacle1Y
* Obstacle2X Obstacle2Y
* (Other obstacles)
*/
public class SimulatorFile{
	File file;
	Scanner in;
	Simulator sim;
	int[] size, dinghy, goal;
	int numObstacles;
	
	public SimulatorFile(String filename) {
		file = new File(filename);
		try{
			in = new Scanner(file);}
		catch(IOException e){
			System.err.println("Error!");
			e.printStackTrace();
		}
		setSize();
		setDinghy();
		numObstacles = in.nextInt();
		
		sim = new Simulator(size[0], size[1], numObstacles, dinghy[0], dinghy[1]);
		
		setGoal();
		sim.setGoal(goal[0], goal[1]);
		System.out.println("Goal: " + goal[0] + " " + goal[1]);
		for(int i = 0; i < numObstacles; i++){
			int obstX = in.nextInt();
			int obstY = in.nextInt();
			sim.addObstacle(i, obstX, obstY);
			System.out.println("Obstacle " + i + ": " + obstX + " " + obstY);
		}
		
	}
	
	private void setSize(){
		size = new int[2];
		size[0] = in.nextInt();
		size[1] = in.nextInt();
	}
	
	private void setDinghy(){
		dinghy = new int[2];
		dinghy[0] = in.nextInt();
		dinghy[1] = in.nextInt();
	}
	
	private void setGoal() {
		goal = new int[2];
		goal[0] = in.nextInt();
		goal[1] = in.nextInt();
	}
}