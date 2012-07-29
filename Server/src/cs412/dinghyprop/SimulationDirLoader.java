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
 * Simulation directory loader
 */
public class SimulationDirLoader {

    private File directory;

    /**
     * Create a new directory loader.
     * @param directory    The directory to load simulators from
     */
    public SimulationDirLoader(String directory) {
        this.directory = new File(directory);
        if (!this.directory.isDirectory()) {
            throw new IllegalArgumentException(directory + "is not a directory.");
        }
    }

    /**
     * Load the simulators from the files in the directory.
     * @return  An array of the loaded simulators
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
     * Get a listing of plain files in this loaders directory.
     * @return  All non-directories in {@code this.directory}
     */
    private File[] getDirectoryFiles() {
        return directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    private Simulator loadFile(File file) {
        SimulatorFile sf = new SimulatorFile(file);
        return sf.getSim();
    }

    @Override
    public String toString() {
        return "SimulationDirLoader(" + directory + ')';
    }
}
