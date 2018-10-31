import botmate.InputGenerator;
import botmate.Solver;
import org.junit.Test;
import problem.ProblemSpec;
import simulator.Simulator;

import java.io.IOException;

public class BruteForceTest {

    @Test
    public void bruteForceTest() throws IOException {

        InputGenerator.generateInputFile(5, "test.input.txt");

        ProblemSpec ps = new ProblemSpec("test.input.txt");
        Simulator sim = new Simulator(ps, "test.output.txt");

        Solver.solveProblem(ps, sim);
    }
}
