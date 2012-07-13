package cs412.dinghyprop.genetics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collects the details of a generated program.
 */
public class Program {
    private static final Pattern PARSE_PROGRAM =
            Pattern.compile("^\\[(\\d+)\\]\\s+(.*)$");
    public final String program;
    int fitness;

    /**
     * Creates a {@code Program} object.
     * @param program    The program text.
     */
    public Program(String program) {
        this.fitness = -1;
        this.program = program;
    }

    /**
     * Read a program from a line in the format of {@code toString()}.
     * @param input    The line
     * @return  A {@code Program} configured from {@code input} or an empty
     * program if parsing failed.
     */
    public static Program fromString(String input) {
        Matcher match = PARSE_PROGRAM.matcher(input);
        if (match.matches()) {
            String programText = match.group(2);
            Program program = new Program(programText);
            try {
                program.fitness = Integer.parseInt(match.group(2));
            } catch (NumberFormatException ignored) {}
            return program;
        }
        return new Program("");
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
        return '[' + Integer.toString(fitness) + "] " + program;
    }
}
