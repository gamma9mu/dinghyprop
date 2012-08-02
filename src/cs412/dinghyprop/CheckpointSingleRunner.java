/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import cs412.dinghyprop.genetics.CheckpointLoader;
import cs412.dinghyprop.genetics.GeneticProgram;

/**
 * Entry-point which runs a GeneticProgram, using SingleRunner, from a
 * checkpoint.
 */
public class CheckpointSingleRunner {

    /**
     * Runs a check-pointed GP with a fresh SingleRunner.
     * @param args    two arguments: the checkpoint directory and the simulation
     *                directory
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: CheckpointSingleRunner <checkpoint_dir> <simulation_dir>");
            System.exit(-1);
        }

        CheckpointLoader cl = new CheckpointLoader(args[0]);
        SimulationDirLoader sdl = new SimulationDirLoader(args[1]);
        GeneticProgram gp = cl.instantiate();
        if (gp != null) {
            SingleRunner.run(gp, sdl.load());
        } else {
            System.err.println("Could not load checkpoint.");
        }
    }
}
