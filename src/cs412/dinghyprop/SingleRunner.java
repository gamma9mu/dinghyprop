package cs412.dinghyprop;

import cs412.dinghyprop.genetics.GeneticProgram;
import cs412.dinghyprop.genetics.Program;
import cs412.dinghyprop.interpreter.Interpreter;
import cs412.dinghyprop.simulator.Simulator;
import cs412.dinghyprop.simulator.SimulatorRandom;

/**
 * Non-distributed GP runner.
 */
public class SingleRunner {
    public static void main(String[] args) {
        GeneticProgram gp = new GeneticProgram(100,
                GeneticProgram.INIT_POP_METHOD.RHALF_AND_HALF, 10);

        for (int iter = 0; iter < 1000; iter++) {
            System.out.println("Iteration: " + iter);
            int fitnesses = 0;
            int maxFitenss = 0;
            for (int i = 0; i < gp.getPopulationSize(); i++) {
                Program program = gp.getProgram(i);
                Simulator sim = new SimulatorRandom(20, 20, 10).getSimulator();
                Interpreter interpreter = new Interpreter(sim, program.program);
                for (int round = 0; round < 100; round++) {
                    interpreter.execute();
                    if (sim.getGoalDistanceMetric() == 100) {
                        break;
                    }
                }
                int fitness = sim.getFitness();
                program.fitness = fitness;
                fitnesses += fitness;
                maxFitenss = (fitness > maxFitenss) ? fitness : maxFitenss;
            }
            System.out.println("Max: " + maxFitenss
                    + "\t Avg: " + (fitnesses/gp.getPopulationSize()));
        }
    }
}
