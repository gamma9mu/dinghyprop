package cs412.dinghyprop.genetics;

/**
 * Collects the details of a generated program.
 */
public class Program {
    public int fitness;
    public String program;

    public Program(String program) {
        this.fitness = -1;
        this.program = program;
    }

    @Override
    public String toString() {
        return '[' + fitness + "] " + program;
    }
}
