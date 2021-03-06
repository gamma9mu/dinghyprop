/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.genetics;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.*;

/**
 * Dinghy navigation genetic programming
 *
 * After construction of a new {@code GeneticProgram}, any parameters should be
 * set followed by calling {@code initialize()}.  Individuals should not be
 * requested until initialization is complete.  The use of any setters,
 * except {@code setProgramFitness()}, after the population may have strange
 * effects, but are not disabled because this is random search...
 *
 * After construction from an exiting population, re-initializing will erase
 * the population provided during construction.
 */
public final class GeneticProgram {
    /**
     * When creating individuals for the initial population, this is the default
     * density of if statements that will be generated (expressed as a
     * fractional percent).
     */
    public static final double DEFAULT_IF_DENSITY = 0.1;

    /**
     * When creating individuals for the initial population, this is the default
     * density of integer terminals that will be generated (expressed as a
     * fractional percent).  This will also be used in terminal mutation,
     * setting the ratio of numbers relative to symbolic terminals.
     */
    public static final double DEFAULT_CONSTANT_DENSITY = 0.1;

    /**
     * The default rate of crossover.
     */
    public static final double DEFAULT_CROSSOVER_RATE = 0.9;

    /**
     * The default rate of individual mutation.
     */
    public static final double DEFAULT_MUTATION_RATE = 0.08;

    /**
     * The default rate of individual reproduction.
     */
    public static final double DEFAULT_REPRODUCTION_RATE =
            1 - (DEFAULT_CROSSOVER_RATE + DEFAULT_MUTATION_RATE);

    /**
     * The methods used to generate initial populations: grow, fill, and ramped
     * half-and-half.
     */
    public static enum INIT_POP_METHOD { GROW, FILL, RHALF_AND_HALF }

    // arithmetic operators
    private static final Set<String> functions =
            new HashSet<String>(Arrays.asList("+", "-", "*", "/", "^"));
    // comparison operators
    private static final Set<String> comparitors =
            new HashSet<String>(Arrays.asList("<", "<=", ">", ">=", "==", "!="));
    // non-numeric terminals
    private static final Set<String> terminals =
            new HashSet<String>(Arrays.asList("(move)", "(turn-left)",
                    "(turn-right)", "front", "short-left", "short-right", "left",
                    "right", "rear", "position-x", "position-y", "goal-position-x",
                    "goal-position-y", "heading"));

    // the current generation
    private Program[] population = null;
    // the count of individuals in a generation
    private final int populationSize;

    // this object selects individuals for genetic operators
    private Selector selector = new TournamentSelector(2);

    // population initialization method
    private INIT_POP_METHOD init_pop_method = INIT_POP_METHOD.RHALF_AND_HALF;
    // max depth of initial generation's programs
    private int initialMaxDepth = 5;
    // if construct density in initial population
    private double ifDensity = DEFAULT_IF_DENSITY;
    // constant:symbolic terminal ratio
    private double constDensity = DEFAULT_CONSTANT_DENSITY;

    // genetic operator usage ratios
    private double crossoverRate = DEFAULT_CROSSOVER_RATE;
    private double mutationRate = DEFAULT_MUTATION_RATE;
    private double reproductionRate = DEFAULT_REPRODUCTION_RATE;

    // objects wanting notification of individual creation
    private List<IPopulationObserver> observers =
            new ArrayList<IPopulationObserver>(2);

    // a good RNG
    private final Random rand = new SecureRandom();

    /**
     * Creates a new GP object and initialize its population.
     *
     * @param populationSize    the size of population to use
     * @param method            the initialization method
     * @param maxDepth          the maximum initial depth of any individual
     *                          program tree
     */
    public GeneticProgram(int populationSize, INIT_POP_METHOD method, int maxDepth) {
        this.populationSize = populationSize;
        this.init_pop_method = method;
        this.initialMaxDepth = maxDepth;
    }

    /**
     * Convenience constructor for in-package utilities.
     *
     * @param population       The population to use
     * @param crossoverRate    The crossover rate
     * @param mutationRate     The mutation rate
     */
    GeneticProgram(Program[] population, double crossoverRate, double mutationRate) {
        this.population = population;
        this.populationSize = population.length;
        setCrossoverRate(crossoverRate);
        setMutationRate(mutationRate);
    }

    /**
     * Initializes the first generation.  This is a separate action to allow an
     * object to register itself as a population observer before the first
     * population is created.
     */
    public void initialize() {
        population = new Program[populationSize];
        switch (init_pop_method) {
            case GROW:
                for (int i = 0; i < population.length; i++) {
                    population[i] = new Program(grow(initialMaxDepth));
                    notifyObservers(i);
                }
                break;
            case FILL:
                for (int i = 0; i < population.length; i++) {
                    population[i] = new Program(fill(initialMaxDepth));
                    notifyObservers(i);
                }
                break;
            case RHALF_AND_HALF:
                rampedHalfAndHalf(initialMaxDepth);
                break;
        }
    }

    /**
     * @return  a randomly chosen terminal or constant
     */
    private String randomTerminal() {
        if (rand.nextDouble() < constDensity)
            return Integer.toBinaryString(rand.nextInt(100));
        else
            return (String) terminals.toArray()[rand.nextInt(terminals.size())];
    }

    /**
     * @return  a randomly chosen function
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
     * Grow san individual by randomly choosing from the available functions
     * and terminals at every point (except leaf nodes, which are always
     * terminals).
     *
     * @param maxHeight    the maximum height of the individual
     * @return  a randomly grown individual
     */
    private String grow(int maxHeight) {
        String res = grow_help(maxHeight);
        if (!res.startsWith("(") && !res.endsWith(")")) {
            res = '(' + randomFunction() + ' ' + grow_help(maxHeight - 1)
                    + ' ' + res + ')';
        }
        return res;
    }

    /**
     * Performs the majority of the work of {@link #grow(int)} .
     *
     * @param maxHeight    the maximum height of the individual
     * @return  a randomly grown subtree
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
        return '(' + randomFunction() + ' ' + grow_help(nextMax)
                + ' ' + grow_help(nextMax) + ')';
    }

    /**
     * Grows an individual to fill a tree to a given maximum height.  Only leaf
     * nodes will be terminals.
     *
     * <b>Important:</b> Calling this method with {@code maxHeight} = 1 will
     * produce in a single terminal, which will likely not parse as a valid
     * program.
     *
     * @param maxHeight    the maximum height of the individual
     * @return  a randomly (fill-)grown individual
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
     * Initializes a population using the grow method on half of the
     * individuals and the fill method on the rest.
     *
     * @param maxDepth    the maximum depth of any individuals tree
     */
    private void rampedHalfAndHalf(int maxDepth) {
        int half = population.length / 2;
        int i = 0;
        while (i < half) {
            population[i] = new Program(grow(maxDepth));
            notifyObservers(i);
            i++;
        }
        while (i < population.length) {
            population[i] = new Program(fill(maxDepth));
            notifyObservers(i);
            i++;
        }
    }

    /**
     * Adds an object as a population observer.
     *
     * @param observer    the observing object
     */
    public void addPopulationObserver(IPopulationObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes a registered population observer.
     *
     * @param observer    the observer to remove
     */
    public void removePopulationObserver(IPopulationObserver observer) {
        if (observers.contains(observer))
            observers.remove(observer);
    }

    /**
     * @return the ratio for generating constants versus symbolic terminals as
     * a fractional percent
     */
    public double getConstDensity() {
        return constDensity;
    }

    /**
     * @return  the (average) percent of individuals created through crossover
     */
    public double getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * @return  the density of if statements to use when growing individuals
     */
    public double getIfDensity() {
        return ifDensity;
    }

    /**
     * @return  the (average) percent of individuals created through mutation.
     */
    public double getMutationRate() {
        return mutationRate;
    }

    /**
     * @return  the size of the population
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * @param index    the individual program's index into the population
     * @return  the individual at the requested position
     */
    public Program getProgram(int index) {
        return population[index];
    }

    /**
     * @return  the (average) percent of individuals that will be reproduced
     * generally (1 - (mutation_rate + crossover_rate)
     */
    public double getReproductionRate() {
        return reproductionRate;
    }

    /**
     * @return  the current selector
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * @param constDensity ratio of constants versus symbolics used in
     *                     generating terminals as a fractional percent
     */
    public void setConstDensity(double constDensity) {
        this.constDensity = constDensity;
    }

    /**
     * @param crossoverRate    crossover rate expressed as a fractional percent
     */
    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
        ensureValidRates();
    }

    /**
     * @param ifDensity    density of if statements to use when growing
     *                     individuals expressed as a fractional percent
     */
    public void setIfDensity(double ifDensity) {
        this.ifDensity = ifDensity;
    }

    /**
     * @param mutationRate    the mutation rat expressed as afraction percent
     */
    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
        ensureValidRates();
    }

    /**
     * @param programIndex    the index into the population of a program
     * @param fitness         the fitness value to assign to that program
     */
    public void setProgramFitness(int programIndex, int fitness) {
        population[programIndex].fitness = fitness;
    }

    /**
     * @param selector    the population selector
     */
    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    /**
     * Apply the genetic operators to create the next population.
     * <b>Important:</b> This method will not verify that all individuals have been
     * evaluated.
     */
    public void createNextGeneration() {
        Program[] nextGeneration = new Program[populationSize];
        double crossover = crossoverRate;
        double mutation = crossover + mutationRate;

        for (int i = 0; i < populationSize; i++) {
            double propogationType = rand.nextDouble();
            if (propogationType <= crossover) {
                nextGeneration[i] = crossover();
            } else if (propogationType <= mutation) {
                // randomly choose between point and subtree mutation
                if (rand.nextBoolean())
                    nextGeneration[i] = pointMutation();
                else
                    nextGeneration[i] = subtreeMutation();
            } else {
                nextGeneration[i] = reproduce();
            }
            notifyObservers(i);
        }

        population = nextGeneration;
    }

    /**
     * Notifies any population observers of the creation of a new individual.
     *
     * @param index    the index of the created individual
     */
    private void notifyObservers(int index) {
        for (IPopulationObserver observer : observers)
            observer.individualCreated(index, population[index]);
    }

    /**
     * Convenience method for {@link #savePopulation(PrintWriter)}.
     * <p>
     * Writes each individual in the population to a OutputStream, one program
     * to a line and formatted by {@link Program#toString()}.
     *
     * @param out    the OutputStream to save the population to
     */
    public void savePopulation(OutputStream out) {
        savePopulation(new PrintWriter(out));
    }

    /**
     * Writes each individual in the population to a PrintWriter, one program
     * to a line and formatted by {@link Program#toString()}.
     *
     * @param out    the PrintWriter to save the population to
     */
    public void savePopulation(PrintWriter out) {
        out.println(toString());
    }

    /**
     * Performs crossover on two selected individuals producing one offspring.
     *
     * @return  a program produced by replacing one part of its first parent by
     * a part of its second parent
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
                + p0.program.substring(end0 + 1);

        return new Program(newProgram);
    }

    /**
     * Selects a random opening parenthesis from within a string.
     *
     * @param str    the string to select from
     * @return  the index of a randomly chosen '('
     */
    private int randomStartParen(String str) {
        int parens = 0;
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == '(') parens++;

        int off = rand.nextInt(parens);
        int idx = str.indexOf('(');
        while (off > 0) {
            idx = str.indexOf('(', idx + 1);
            off--;
        }
        return idx;
    }

    /**
     * Finds the matching closing parenthesis to a given opening parenthesis in
     * an S-expression.
     *
     * @param str      the S-expression
     * @param start    the index of the opening parenthesis
     * @return  the index of the matched closing parenthesis or -1 if none
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
            }
        }
        return -1;
    }

    /**
     * Performs reproduction on a selected individual.
     * @return  a program selected from the population by the selector
     */
    private Program reproduce() {
        return selector.select(population);
    }

    /**
     * Performs point mutation on a single point of a selected individual.
     *
     * @return  a new individual with a single point replaced by a new function
     * or terminal (as appropriate)
     */
    private Program pointMutation() {
        String program = selector.select(population).program;
        int start = randomStartParen(program);

        while (program.charAt(start) == '(') // find start of symbol
            start++;
        int end = start + 1;
        while ("( )".indexOf(program.charAt(end)) == -1) // find the end
                end++;

        String replacement = getMutationReplacement(program.substring(start, end));
        String newProgram = program.substring(0, start)
                + replacement
                + program.substring(end);
        return new Program(newProgram);
    }

    /**
     * Obtains a replacement for any member of an individual program feature.
     *
     * @param str    A function or terminal to replace
     * @return  a function or terminal drawn from the same pool as {@code str}
     */
    private String getMutationReplacement(String str) {
        String replacement = str; // fallback: no mutation
        if (functions.contains(str)) {
            do {
                replacement = randomFunction();
            } while (str.compareTo(replacement) == 0);
        } else if (comparitors.contains(str)) {
            do {
                replacement = randomComparison();
            } while (str.compareTo(replacement) == 0);
        } else if (terminals.contains(str)) {
            do {
                replacement = randomTerminal();
            } while (str.compareTo(replacement) == 0);
        } else { // A number
            try {
                Integer.parseInt(str); // verify first
                do {
                    replacement = randomTerminal();
                } while (str.compareTo(replacement) == 0);
            } catch (NumberFormatException ignored) { }
        }
        return replacement;
    }

    /**
     * Performs subtree mutation on a subtree of a selected individual.
     *
     * @return  a new individual with a subtree replaced by a newly grown
     * subtree
     */
    private Program subtreeMutation() {
        String program = selector.select(population).program;
        int start = randomStartParen(program);
        int end = findMatchingParen(program, start) + 1;
        int depth = getTreeDepth(program, start, end);
        depth = (depth > 1) ? depth : 2; // new tree's minimum depth = 2

        String replacement = grow(depth);
        String newProgram;
        if (start > 0)
            newProgram = program.substring(0, start)
                + replacement
                + program.substring(end);
        else
            newProgram = replacement;

        return new Program(newProgram);
    }

    /**
     * Calculates the depth of a program subtree.
     *
     * @param program    the program text
     * @param start      the starting index (first parenthesis)
     * @param end        the ending index (matching parenthesis)
     * @return  the depth of the lowest terminal found, treating the substring
     * subtree as an S-expression
     */
    private int getTreeDepth(String program, int start, int end) {
        int maxDepth = 1;
        int current = 1;
        for (int i = start + 1; i < end; i++) {
            switch (program.charAt(i)) {
                case '(':
                    current++;
                    break;
                case ')':
                    current--;
                    break;
            }
            maxDepth = (current > maxDepth) ? current : maxDepth;
        }
        return maxDepth;
    }

    /**
     * Ensures the values for crossover rate, mutation rate, and reproduction
     * rate are sane; crossover is preferred to mutation is preferred to
     * reproduction.
     */
    private void ensureValidRates() {
        double total = crossoverRate + mutationRate + reproductionRate;

        if (total > 1) {
            double over = total - 1;
            if (reproductionRate > 0) {
                if (reproductionRate > over) {
                    reproductionRate -= over;
                    over = 0;
                } else {
                    over -= reproductionRate;
                    reproductionRate = 0;
                }
            }
            if (mutationRate > 0 && over > 0) {
                if (mutationRate > over) {
                    mutationRate -= over;
                    over = 0;
                } else {
                    over -= mutationRate;
                    mutationRate = 0;
                }
            }
            if (crossoverRate > 0 && over > 0) {
                if (crossoverRate > over) {
                    crossoverRate -= over;
                } else {
                    System.err.println("ensureValidRates: unreachable code has been reached.");
                    crossoverRate = 0;
                }
            }
        } else if (total < 1) {
            double under = 1 - total;
            reproductionRate += under;
        } else {
            // sanity achieved
        }
    }

    @Override
    public String toString() {
        // # <pop_size> <selector> <x-over_rate> <mutation_rate> <reproduction_rate>
        StringBuilder sb = new StringBuilder("# ");
        sb.append(String.format("%d ", populationSize));
        sb.append(selector.toString());
        sb.append(' ');
        sb.append(String.format("%01.4f ", crossoverRate));
        sb.append(String.format("%01.4f ", mutationRate));
        sb.append(String.format("%01.4f\n", reproductionRate));

        for (int i = 0; i < populationSize; i++) {
            sb.append(population[i].toString());
            sb.append('\n');
        }

        return sb.toString();
    }
}
