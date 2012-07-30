/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop.genetics;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads a checkpoint for continuing a GP run.
 */
public final class CheckpointLoader {
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static Map<String, Constructor> selectors =
            new HashMap<String, Constructor>(1);

    private File directory;
    private BufferedReader in = null;

    private int popSize = 0;
    private String selector = null;
    private double crossOver = 0.0;
    private double mutation = 0.0;

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
     * Create a new checkpoint loader from a checkpoint directory.
     * @param checkpointDirectory    The checkpoint directory
     */
    public CheckpointLoader(File checkpointDirectory) {
        directory = checkpointDirectory;
    }

    /**
     * Create a new checkpoint loader from a checkpoint directory.
     * @param checkpointDirectory    The path to the checkpoint directory
     */
    public CheckpointLoader(String checkpointDirectory) {
        directory = new File(checkpointDirectory);
    }

    /**
     * Instantiate the last check-pointed generation.
     * @return  A {@code GeneticProgram} from this loaders directory
     */
    public GeneticProgram instantiate() {
        if (checkFinished()) return null;

        Program[] programs;
        try {
            in = new BufferedReader(new FileReader(getLastGenerationFile()));
            readDataLine();
            programs = getPrograms();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

        GeneticProgram gp = new GeneticProgram(programs, crossOver, mutation);
        Selector sel = createSelector();
        if (sel != null)
            gp.setSelector(sel);
        return gp;
    }

    /**
     * Instantiate the selector described by the selector field in the GP's
     * data line's selector field.
     * @return A {@code Selector} or null
     */
    private Selector createSelector() {
        Pattern splitter = Pattern.compile("(\\w+)\\((\\d*)\\)");
        Matcher matcher = splitter.matcher(selector);
        if (matcher.matches()) {
            String name = matcher.group(1);
            String argument = matcher.group(2);
            try {
                if (argument == null || argument.trim().compareTo("") == 0) {
                    return instantiateSelector(name.trim());
                } else {
                    return instantiateSelector(name.trim(),
                            Integer.parseInt(argument));
                }
            } catch (Exception ignored) { }
        }
        return null;
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
    }

    /**
     * Read the programs from the input.
     * @return  An array of the recreated {@code Program}s
     * @throws IOException if an error occurs while reading
     */
    private Program[] getPrograms() throws IOException {
        Program[] programs = new Program[popSize];
        int read = 0;
        while (read < popSize) {
            String line = in.readLine().trim();
            while (WHITESPACE.matcher(line).matches())
                line = in.readLine().trim();
            programs[read] = Program.fromString(line);
            read++;
        }
        return programs;
    }

    /**
     * Check the checkpoint directory for a file listing the final evolved
     * generation
     * @return  Whether the GP run completed
     */
    private boolean checkFinished() {
        File finalGP = new File(directory, "final_generation");
        return finalGP.exists();
    }

    /**
     * Find the last file whose name begins with "gen_" when the files are
     * sorted alphabetically.
     * @return  The matched file or null
     */
    private File getLastGenerationFile() {
        File[] genFiles = directory.listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
                return name.startsWith("gen_");
            }
        });

        if (genFiles == null || genFiles.length == 0) {
            return null;
        }

        Arrays.sort(genFiles, new Comparator<File>() {
            @Override public int compare(File o1, File o2) {
                return - o1.getName().compareTo(o2.getName());
            }
        });

        return genFiles[0];
    }
}
