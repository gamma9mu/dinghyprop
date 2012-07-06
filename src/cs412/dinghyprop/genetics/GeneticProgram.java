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
    public static final double DEFAULT_IF_DENSITY = 0.1;
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

    private String[] population;
    private int populationSize;
    private double ifDensity = DEFAULT_IF_DENSITY;
    private Random rand = new SecureRandom();

    public GeneticProgram(int populationSize, INIT_POP_METHOD method, int maxDepth) {
        this.populationSize = populationSize;
        population = new String[populationSize];
        switch (method) {
            case GROW:
                for (int i = 0; i < population.length; i++) {
                    population[i] = grow(maxDepth);
                }
                break;
            case FILL:
                for (int i = 0; i < population.length; i++) {
                    population[i] = fill(maxDepth);
                }
                break;
            case RHALF_AND_HALF:
                rampedHalfAndHalf(maxDepth);
                break;
        }
    }

    private String randomTerminal() {
        return (String) terminals.toArray()[rand.nextInt(terminals.size())];
    }

    private String randomFunction() {
        return (String) functions.toArray()[rand.nextInt(functions.size())];
    }

    private String randomComparison() {
        return (String) comparitors.toArray()[rand.nextInt(comparitors.size())];
    }

    private String grow(int maxHeight) {
        String res = grow_help(maxHeight);
        if (!res.startsWith("(") && !res.endsWith(")")) {
            res = "(+ 0 " + res + ')';
        }
        return res;
    }

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

    private void rampedHalfAndHalf(int maxDepth) {
        int half = population.length / 2;
        int i = 0;
        while (i < half) {
            population[i] = grow(maxDepth);
            i++;
        }
        while (i < population.length) {
            population[i] = fill(maxDepth);
            i++;
        }
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public double getIfDensity() {
        return ifDensity;
    }

    public void setIfDensity(double ifDensity) {
        this.ifDensity = ifDensity;
    }

    public static void main(String[] args) {
        GeneticProgram gp = new GeneticProgram(10, INIT_POP_METHOD.FILL, 10);
        for (String ind : gp.population) {
            System.out.println(ind);
        }
    }
}
