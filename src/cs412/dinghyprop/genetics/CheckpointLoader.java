package cs412.dinghyprop.genetics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads a checkpoint for continuing a GP run.
 */
public final class CheckpointLoader {
    private static Map<String, Constructor> selectors =
            new HashMap<String, Constructor>(1);

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

    // # <pop_size> <selector> <x-over_rate> <mutation_rate> <reproduction_rate>
}
