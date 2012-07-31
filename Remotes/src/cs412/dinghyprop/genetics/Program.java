/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.genetics;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generated program state management
 */
public class Program implements Serializable {
    private static final long serialVersionUID = 1364731017270512774L;

    /**
     * RegEx to split the serialized components for reconstructing a plain-text
     * serialized Program object.
     */
    private static final Pattern PARSE_PROGRAM =
            Pattern.compile("^\\[(\\d+)\\]\\s+(.*)$");

    /**
     * The program text
     */
    public final String program;

    /**
     * The computed fitness of the program
     *
     * This field is package protected to allow simple access from e.g.
     * Selector objects.
     */
    int fitness;

    /**
     * @param program    the program's text
     */
    public Program(String program) {
        this.fitness = -1;
        this.program = program;
    }

    /**
     * Read a program from a line in the format used in {@link #toString()}.
     *
     * @param input    the String representation of a program
     * @return  a program object configured from the input String, possibly
     * having an empty text attribute if parsing failed.
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
     * @return  the integer fitness value of this program
     */
    public int getFitness() {
        return fitness;
    }

    /**
     * Create a String serializing the necessary details of this program object
     * suitable for saving and restoring from plain text files.
     *
     * @return a String representation of this program
     */
    @Override
    public String toString() {
        return '[' + Integer.toString(fitness) + "] " + program;
    }
}
