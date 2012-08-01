/*
 * Brian Guthrie and Kevin Reuter
 * DinghyProp
 * CS412 - Summer 2012
 */

package cs412.dinghyprop;

import cs412.dinghyprop.simulator.Simulator;
import cs412.dinghyprop.simulator.SimulatorFile;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 * Loads all files from a directory as simulation environment specifications.
 */
public class SimulationDirLoader {

    /**
     * The directory path
     */
    private File directory;

    /**
     * Creates a new directory loader.
     *
     * @param directory    the directory to load simulators from
     */
    public SimulationDirLoader(String directory) {
        this.directory = new File(directory);
        if (!this.directory.isDirectory()) {
            throw new IllegalArgumentException(directory + "is not a directory.");
        }
    }

    /**
     * Loads the simulators from the files in the directory.
     *
     * @return  an array of the loaded simulators
     */
    public Simulator[] load() {
        File[] files = getDirectoryFiles();

        List<Simulator> simulators = new LinkedList<Simulator>();
        for (File file : files) {
            simulators.add(loadFile(file));
        }

        return simulators.toArray(new Simulator[simulators.size()]);
    }

    /**
     * @return a listing of plain files in this loaders directory
     */
    private File[] getDirectoryFiles() {
        return directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    /**
     * Delegates the loading of a file to {@link SimulatorFile}.
     *
     * @param file    the file to load from
     * @return a simulator as specified in {@code file}
     */
    private Simulator loadFile(File file) {
        SimulatorFile sf = new SimulatorFile(file);
        return sf.getSim();
    }

    @Override
    public String toString() {
        return "SimulationDirLoader(" + directory + ')';
    }
}
