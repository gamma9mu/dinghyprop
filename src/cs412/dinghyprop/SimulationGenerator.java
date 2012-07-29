package cs412.dinghyprop;

import java.util.Random;
import java.io.*;

public class SimulationGenerator {
    private static final int MAX_FILES = 25;
    private final int MAX_X = 200;
    private final int MAX_Y = 200;
    private Random rand;
    private FileWriter out;
    private int sizeX, sizeY, maxObstacles;

    public SimulationGenerator(File fileName) throws IOException {
        //System.out.println("Creating file: " + fileName);
        rand = new Random();
        out = new FileWriter(fileName);
        sizeX = rand.nextInt(MAX_X);
        out.write(sizeX + " ");
        sizeY = rand.nextInt(MAX_Y);
        out.write(sizeY + "\n");

        generateDinghy();

        maxObstacles = (sizeX * sizeY) / 100;
        int numObstacles = generateNumObst();

        generateGoal();

        generateObstacles(numObstacles);

        out.close();

    }

    private void generateDinghy() throws IOException {
        int dinghyX = rand.nextInt(sizeX);
        out.write(dinghyX + " ");
        int dinghyY = rand.nextInt(sizeY);
        out.write(dinghyY + "\n");
    }

    private int generateNumObst() throws IOException {
        int numObst = rand.nextInt(maxObstacles);
        out.write(numObst + "\n");

        return numObst;
    }

    private void generateGoal() throws IOException {
        int goalX = rand.nextInt(sizeX);
        out.write(goalX + " ");
        int goalY = rand.nextInt(sizeY);
        out.write(goalY + "\n");
    }

    private void generateObstacles(int numObstacles) throws IOException {
        for(int i = 0; i < numObstacles; i++) {
            int obstacleX = rand.nextInt(sizeX);
            out.write(obstacleX + " ");
            int obstacleY = rand.nextInt(sizeY);
            out.write(obstacleY + "\n");
        }
    }

    public static void main(String[] args) {
        String initialPath;
        if(args.length > 0)
            initialPath = args[0];
        else
            initialPath = ".";




        for(int i = 0; i < MAX_FILES; i++) {
            String fileName = initialPath;
            if(i < 10) {
                fileName += "0";
            }
            File test = new File(fileName + i);
            if(!test.exists()){
                try {
                    SimulationGenerator sim = new SimulationGenerator(test);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
                System.out.println("File " + fileName + i + " already exists");
        }

    }
}
