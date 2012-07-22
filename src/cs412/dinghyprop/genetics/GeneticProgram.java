package cs412.dinghyprop.genetics;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.*;

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

    private final int populationSize;
    private double ifDensity = DEFAULT_IF_DENSITY;
    private final Random rand = new SecureRandom();
    private Selector selector = new TournamentSelector(2);

    private double crossoverRate = DEFAULT_CROSSOVER_RATE;
    private double mutationRate = DEFAULT_MUTATION_RATE;
    private double reproductionRate = DEFAULT_REPRODUCTION_RATE;

    private List<IPopulationObserver> observers =
            new ArrayList<IPopulationObserver>(2);

    /**
     * Create a new GP object and initialize its population.
     * @param populationSize    the size of population to use
     * @param method            the initialization method
     * @param maxDepth          the maximum initial depth of any individual
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
     * Convenience constructor for in-package utilities.
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
     * Add an object as a population observer.
     * @param observer    The observing object
     */
    public void addPopulationObserver(IPopulationObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove a registered population observer.
     * @param observer    The observer to remove
     */
    public void removePopulationObserver(IPopulationObserver observer) {
        if (observers.contains(observer))
            observers.remove(observer);
    }

    /**
     * Retrieve the rate of crossover.
     * @return  The (average) percent of individuals created through crossover.
     */
    public double getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * Get the density of if statements to use when growing individuals.
     * @return  The density of if statements to use when growing individuals
     */
    public double getIfDensity() {
        return ifDensity;
    }

    /**
     * Retrieve the rate of mutation.
     * @return  The (average) percent of individuals created through mutation.
     */
    public double getMutationRate() {
        return mutationRate;
    }

    /**
     * Get the population size.
     * @return  The size of the population.
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Obtain an individual from the population.
     * @param index    The individuals index into the population
     * @return  The individual at the requested position
     */
    public Program getProgram(int index) {
        return population[index];
    }

    /**
     * Retrieve the reproduction rate (1 - (mutation_rate + crossover_rate).
     * @return  The (average) percent of individuals that will be reproduced
     */
    public double getReproductionRate() {
        return reproductionRate;
    }

    /**
     * Obtain the current selector.
     * @return  The current selector
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * Set the crossover rate.
     * @param crossoverRate    A percent expressed as a decimal [0,1].
     */
    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
        ensureValidRates();
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
     * Set the mutation rate.
     * @param mutationRate    A percent expressed as a decimal [0,1].
     */
    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
        ensureValidRates();
    }

    /**
     * Set the fitness value of a given program.
     * @param programIndex    The index into the population of the given program
     * @param fitness         The fitness value to assign to the program
     */
    public void setProgramFitness(int programIndex, int fitness) {
        population[programIndex].fitness = fitness;
    }

    /**
     * Set the population selector.
     * @param selector    The new selector.
     */
    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    /**
     * Apply the genetic operators to create the next population.
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
     * Notify all the population observers of the creation of a new individual.
     * @param index    The index of the individual
     */
    private void notifyObservers(int index) {
        for (IPopulationObserver observer : observers)
            observer.individualCreated(index, population[index]);
    }

    /**
     * Convenience method for {@code savePopulation(PrintWriter out)}.
     *
     * Write each individual in the population to a {@code OutputStream}, one
     * program to a line and formatted by {@code Program.toString()}.
     * @param out    The {@code OutputStream} to save the population to
     */
    public void savePopulation(OutputStream out) {
        savePopulation(new PrintWriter(out));
    }

    /**
     * Write each individual in the population to a {@code PrintWriter}, one
     * program to a line and formatted by {@code Program.toString()}.
     * @param out    The {@code PrintWriter} to save the population to
     */
    public void savePopulation(PrintWriter out) {
        out.println(toString());
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
                + p0.program.substring(end0 + 1);

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

        int off = rand.nextInt(parens);
        int idx = str.indexOf('(');
        while (off > 0) {
            idx = str.indexOf('(', idx + 1);
            off--;
        }
        return idx;
    }

    /**
     * Find the matching closing parenthesis to a given opening parenthesis in
     * an S-expression.
     * @param str      The S-expression
     * @param start    The index of the opening parenthesis
     * @return  The index of the matched closing parenthesis or -1 if none
     * could be found
     */
    private int findMatchingParen(String str, int start) {
        try {
            if (str.charAt(start) != '(') {
                return -1;
        }
        } catch (StringIndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            System.err.println('"' + str + '"');
            System.err.println("idx: " + start);
            throw e;
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
     * Reproduce a selected individual.
     * @return  A program selected from the population by the {@code selector}.
     */
    private Program reproduce() {
        return selector.select(population);
    }

    /**
     * Mutate a single point of a selected individual.
     * @return  A new individual with a single point replaced by a new function
     * or terminal (as appropriate).
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
     * Obtain a replacement for any member of an individual
     * @param str    A function or terminal to replace
     * @return  A function or terminal drawn from the same pool as {@code str}.
     */
    private String getMutationReplacement(String str) {
        String replacement = str;
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
        }
        return replacement; // fallback: no mutation
    }

    /**
     * Mutate a subtree of a selected individual.
     * @return  A new individual with a subtree replaced by a newly grown
     * subtree.
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
     * Calculate the depth of a program subtree.
     * @param program    The program text
     * @param start      The starting index (first paren.)
     * @param end        The ending index ({@code start}'s matching paren.)
     * @return  The depth of the lowest terminal under the substring's subtree.
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
     * Ensure the values for {@code crossoverRate}, {@code mutationRate}, and
     * {@code reproductionRate} are sane; crossover is preferred to mutation is
     * preferred to reproduction.
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
