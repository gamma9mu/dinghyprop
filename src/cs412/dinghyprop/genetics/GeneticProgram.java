package cs412.dinghyprop.genetics;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * GP overseer.
 */
public final class GeneticProgram {
    /**
     * When creating individuals for the initial population, this is the default
     * density of if statements that will be generated (expressed as a
     * fractional percent).
     */
    public static final double DEFAULT_IF_DENSITY = 0.1;

    /**
     * The methods used to generate initial populations: grow, fill, and ramped
     * half-and-half.
     */
    public static enum INIT_POP_METHOD { GROW, FILL, RHALF_AND_HALF }

    private static final Set<String> functions =
            new HashSet<String>(Arrays.asList("+", "-", "*", "/", "^"));
    private static final Set<String> comparitors =
            new HashSet<String>(Arrays.asList("<", "<=", ">", ">=", "==", "!="));
    private static final Set<String> terminals =
            new HashSet<String>(Arrays.asList("(move)", "(turn-left)",
                    "(turn-right)", "front", "short-left", "short-right", "left",
                    "right", "rear", "position-x", "position-y", "goal-position-x",
                    "goal-position-y", "heading"));

    private Program[] population;
    private int populationSize;
    private double ifDensity = DEFAULT_IF_DENSITY;
    private Random rand = new SecureRandom();
    private Selector selector = new TournamentSelector(2);

    /**
     * Create a new GP object and initialize its population.
     * @param populationSize    the size of population to use
     * @param method            the initialization method
     * @param maxDepth          the maximum initial depth of any individual's
     *                          program tree
     */
    public GeneticProgram(int populationSize, INIT_POP_METHOD method, int maxDepth) {
        this.populationSize = populationSize;
        population = new Program[populationSize];
        switch (method) {
            case GROW:
                for (int i = 0; i < population.length; i++) {
                    population[i] = new Program(grow(maxDepth));
                }
                break;
            case FILL:
                for (int i = 0; i < population.length; i++) {
                    population[i] = new Program(fill(maxDepth));
                }
                break;
            case RHALF_AND_HALF:
                rampedHalfAndHalf(maxDepth);
                break;
        }
    }

    /**
     * Choose a random terminal.
     * @return  A randomly chosen terminal
     */
    private String randomTerminal() {
        return (String) terminals.toArray()[rand.nextInt(terminals.size())];
    }

    /**
     * Choose a random function.
     * @return  A randomly chosen function
     */
    private String randomFunction() {
        return (String) functions.toArray()[rand.nextInt(functions.size())];
    }

    /**
     * Choose a random comparison operator.
     * @return  A randomly chosen comparison operator
     */
    private String randomComparison() {
        return (String) comparitors.toArray()[rand.nextInt(comparitors.size())];
    }

    /**
     * Grow an individual by randomly choosing from the available
     * functions and terminals at every point (except leaf nodes,
     * which are always terminals).
     * @param maxHeight    The maximum height of the individual
     * @return  A randomly grown individual
     */
    private String grow(int maxHeight) {
        String res = grow_help(maxHeight);
        if (!res.startsWith("(") && !res.endsWith(")")) {
            res = "(+ 0 " + res + ')';
        }
        return res;
    }

    /**
     * Performs the majority of the work of {@code grow(int)}.
     * @param maxHeight    The maximum height of the individual
     * @return  A randomly grown individual
     */
    private String grow_help(int maxHeight) {
        int nextMax = maxHeight - 1;
        if (maxHeight >= 2 && rand.nextDouble() < ifDensity) {
            return "(if (" + randomComparison() + ' ' + grow_help(nextMax)
                    + ' ' + grow_help(nextMax) + ") " + grow_help(nextMax)
                    + ' ' + grow_help(nextMax) + ')';
        }
        if (maxHeight == 1 || rand.nextBoolean()) {
            return randomTerminal();
        }
        return '(' + randomFunction() + ' ' + grow(nextMax) + ' ' + grow(nextMax) + ')';
    }

    /**
     * Grow an individual to fill a tree to a given maximum height.  Only leaf
     * nodes will be terminals.
     * @param maxHeight    The maximum height of the individual
     * @return  A newly grown individual
     */
    private String fill(int maxHeight) {
        int nextMax = maxHeight - 1;
        if (maxHeight >= 2 && rand.nextDouble() < ifDensity) {
            return "(if (" + randomComparison() + ' ' + grow_help(nextMax)
                    + ' ' + grow_help(nextMax) + ") " + grow_help(nextMax)
                    + ' ' + grow_help(nextMax) + ')';
        }
        if (maxHeight == 1) {
            return randomTerminal();
        }
        return '(' + randomFunction() + ' ' + fill(nextMax) + ' ' + fill(nextMax) + ')';
    }

    /**
     * Initialize a population using the grow method on half of the individuals
     * and the fill method on the rest.
     * @param maxDepth    The maximum depth of any individuals tree
     */
    private void rampedHalfAndHalf(int maxDepth) {
        int half = population.length / 2;
        int i = 0;
        while (i < half) {
            population[i] = new Program(grow(maxDepth));
            i++;
        }
        while (i < population.length) {
            population[i] = new Program(fill(maxDepth));
            i++;
        }
    }

    /**
     * Get the population size.
     * @return  The size of the population.
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Get the density of if statements to use when growing individuals.
     * @return  The density of if statements to use when growing individuals
     */
    public double getIfDensity() {
        return ifDensity;
    }

    /**
     * Set the density of if statements to use when growing individuals.
     * @param ifDensity    The density of if statements to use when growing
     *                     individuals
     */
    public void setIfDensity(double ifDensity) {
        this.ifDensity = ifDensity;
    }

    /**
     * Crossover 2 selected individuals producing one offspring.
     * @return  A program produced by replacing one part of its first parent by
     * a part of its second parent.
     */
    private Program crossover() {
        Program p0 = selector.select(population);
        Program p1 = selector.select(population);

        int start0 = randomStartParen(p0.program);
        int end0 = findMatchingParen(p0.program, start0);

        int start1 = randomStartParen(p1.program);
        int end1 = findMatchingParen(p1.program, start1);

        String newProgram = p0.program.substring(0, start0)
                + p1.program.substring(start1, end1 + 1)
                + p0.program.substring(end0);

        return new Program(newProgram);
    }

    /**
     * Select a random opening parenthesis from within a string.
     * @param str    The string to select from
     * @return  The index of a randomly chosen '('
     */
    private int randomStartParen(String str) {
        int parens = 0;
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == '(') parens++;

        int off = rand.nextInt(parens + 1);
        int idx = str.indexOf('(', 0) + 1;
        while (off > 0) {
            idx = str.indexOf('(', idx) + 1;
            off--;
        }
        return idx - 1;
    }

    /**
     * Find the matching closing parenthesis to a given opening parenthesis in
     * an S-expression.
     * @param str      The S-epxression
     * @param start    The index of the opening parenthesis
     * @return  The index of the matched closing parenthesis or -1 if none
     * could be found
     */
    private int findMatchingParen(String str, int start) {
        if (str.charAt(start) != '(') {
            return -1;
        }
        int match = 0;
        for (int i = start + 1; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '(':
                    match++;
                    break;
                case ')':
                    if (match == 0) {
                        return i;
                    }
                    match--;
                    break;
                default:
                    break;
            }
        }
        return -1;
    }

    /**
     * Test main
     * @param args    ignored
     */
    public static void main(String[] args) {
        GeneticProgram gp = new GeneticProgram(2, INIT_POP_METHOD.FILL, 5);
        for (Program ind : gp.population) {
            System.out.println(ind);
        }

        System.out.println("CX: " + gp.crossover().program);
    }
}
