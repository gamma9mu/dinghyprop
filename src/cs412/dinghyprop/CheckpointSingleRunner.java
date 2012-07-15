package cs412.dinghyprop;

import cs412.dinghyprop.genetics.CheckpointLoader;
import cs412.dinghyprop.genetics.GeneticProgram;

/**
 * Run a GP from a checkpoint.
 */
public class CheckpointSingleRunner {

    /**
     * Runs a check-pointed GP with a fresh {@code SingleRunner}.
     * @param args    One argument: the checkpoint delivery
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: CheckpointSingleRunner <checkpoint_dir>");
            System.exit(-1);
        }

        CheckpointLoader cl = new CheckpointLoader(args[0]);
        GeneticProgram gp = cl.instantiate();
        SingleRunner.run(gp);
    }
}
