package cs412.dinghyprop.genetics;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Loads a checkpoint for continuing a GP run.
 */
public final class CheckpointLoader {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static Map<String, Constructor> selectors =
            new HashMap<String, Constructor>(1);

    private BufferedReader in;

    private int popSize;
    private String selector;
    private double crossOver;
    private double mutation;
    private double reproduction;

    /**
     * Register a constructor for a class that implements the {@code Selector}
     * interface.  The constructor can be zero-argument or one(int)-argument.
     *
     * This method should be called from the static initializer of the calling
     * class.
     * @param name    The unqualified name of the calling class
     * @param ctor    The constructor
     */
    public static void registerSelector(String name, Constructor ctor) {
        selectors.put(name, ctor);
    }

    /**
     * Instantiate a registered {@code Selector}-implementing class.
     * @param name        The name of the class to instantiate
     * @param argument    The int-typed argument to the constructor.
     * @return  A {@code Selector}
     * @throws InvocationTargetException if newInstance fails
     * @throws IllegalAccessException if newInstance fails
     * @throws InstantiationException if newInstance fails
     */
    private static Selector instantiateSelector(String name, int argument)
            throws InvocationTargetException, IllegalAccessException,
            InstantiationException {
        Constructor ctor = selectors.get(name);
        Class[] params = ctor.getParameterTypes();
        if (params.length != 1) {
            return (Selector) ctor.newInstance();
        }
        return (Selector) ctor.newInstance(argument);
    }

    /**
     * Instantiate a registered {@code Selector}-implementing class.
     * @param name        The name of the class to instantiate
     * @return  A {@code Selector}
     * @throws InvocationTargetException if newInstance fails
     * @throws IllegalAccessException if newInstance fails
     * @throws InstantiationException if newInstance fails
     */
    private static Selector instantiateSelector(String name)
            throws InvocationTargetException, IllegalAccessException,
            InstantiationException {
        Constructor ctor = selectors.get(name);
        return (Selector) ctor.newInstance();
    }

    /**
     * Parse the "data line" from a checkpoint file.  The line should have thr
     * form:
     * <pre>
     # &lt;pop_size&gt; &lt;selector&gt; &lt;x-over_rate&gt; &lt;mutation_rate&gt; &lt;reproduction_rate&gt;
     * </pre>
     * including the leading '#'.
     * @throws IOException  if an exception is thrown while reading the input
     */
    private void readDataLine() throws IOException {
        String input = in.readLine();
        while (input.trim().compareTo("") == 0) {
            input = in.readLine();
        }

        String[] fields = WHITESPACE.split(input.trim());
        assert (fields[0].compareTo("#") != 0);
        popSize = Integer.parseInt(fields[1]);
        selector = fields[2];
        crossOver = Double.parseDouble(fields[3]);
        mutation = Double.parseDouble(fields[4]);
        reproduction = Double.parseDouble(fields[5]);
    }
}
