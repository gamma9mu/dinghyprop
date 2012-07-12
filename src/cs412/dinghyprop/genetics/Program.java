package cs412.dinghyprop.genetics;

/**
 * Collects the details of a generated program.
 */
public class Program {
    public final String program;
    int fitness;

    public Program(String program) {
        this.fitness = -1;
        this.program = program;
    }

    /**
     * Obtain the calculated fitness of this program.
     * @return  The integer fitness or -1 if none has been calculated.
     */
    public int getFitness() {
        return fitness;
    }

    @Override
    public String toString() {
        return '[' + fitness + "] " + program;
    }
}
