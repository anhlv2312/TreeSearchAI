import botmate.Solver;
import org.junit.Rule;
import org.junit.Test;
import problem.ProblemSpec;
import simulator.Simulator;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class StatisticTest {
    public static final int MAX_LOOP =10;

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

    @Test
    @Repeat(times = MAX_LOOP)
    public void level1() throws IOException {
        assertTrue(testLevel(1));
    }

    @Test
    @Repeat(times = MAX_LOOP)
    public void level2() throws IOException {
        assertTrue(testLevel(2));
    }

    @Test
    @Repeat(times = MAX_LOOP)
    public void level3() throws IOException {
        assertTrue(testLevel(3));
    }

    @Test
    @Repeat(times = MAX_LOOP)
    public void level4() throws IOException {
        assertTrue(testLevel(4));
    }

    @Test
    @Repeat(times = MAX_LOOP)
    public void level5() throws IOException {
        assertTrue(testLevel(5));
    }


    private boolean testLevel(int level) throws IOException {
//        String inputFile = "test." + level + ".input.txt";
//        String outputFile = "test." + level + ".output.txt";
        Date date = new Date();
        long time = date.getTime();

        String inputFile = "examples/level_" + level + "/input_lvl" + level + "_2.txt";
        String outputFile = "examples/level_" + level + "/output_lvl" + level + "_2"+time+".txt";

//        InputGenerator.generateInputFile(level, inputFile );
        ProblemSpec ps = new ProblemSpec(inputFile);
        Simulator sim = new Simulator(ps, outputFile);

        return Solver.solveProblem(ps, sim);
    }
}
