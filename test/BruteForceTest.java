import botmate.InputGenerator;
import botmate.Solver;
import org.junit.Test;
import static org.junit.Assert.*;
import problem.ProblemSpec;
import simulator.Simulator;

import java.io.IOException;


public class BruteForceTest {

    @Test
    public void level1() throws IOException {
        assertTrue(testLevel(1));
    }

    @Test
    public void level2() throws IOException {
        assertTrue(testLevel(2));
    }
    @Test
    public void level3() throws IOException {
        assertTrue(testLevel(3));
    }
    @Test
    public void level4() throws IOException {
        assertTrue(testLevel(4));
    }
    @Test
    public void level5() throws IOException {
        assertTrue(testLevel(5));
    }
    private boolean testLevel(int level) throws IOException {
        String inputFile = "test." + level + ".input.txt";
        String outputFile = "test." + level + ".output.txt";

//        InputGenerator.generateInputFile(level, inputFile );
        ProblemSpec ps = new ProblemSpec(inputFile);
        Simulator sim = new Simulator(ps, outputFile);

        System.out.println();

        return Solver.solveProblem(ps, sim);
    }
}
