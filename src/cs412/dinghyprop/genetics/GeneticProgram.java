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
     * Test main
     * @param args    ignored
     */
    public static void main(String[] args) {
        GeneticProgram gp = new GeneticProgram(10, INIT_POP_METHOD.FILL, 10);
        for (Program ind : gp.population) {
            System.out.println(ind);
        }
    }
}
