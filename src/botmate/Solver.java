package botmate;

import problem.*;
import simulator.*;

import java.io.IOException;

public class Solver {

    private final static int SAMPLE_COUNT = 1000;

    private static ProblemSpec ps;
    private static Simulator sim;

    public static void main(String[] args) {
        try {
            ps = new ProblemSpec(args[0]);
            sim = new Simulator(ps, args[1]);
        } catch (IOException | ArrayIndexOutOfBoundsException exception) {
            System.out.println("Error while loading input/output file: " + exception.toString());
            System.exit(1);
        }
        solveProblem();
    }

    private static void solveProblem() {
        Agent agent = new Agent(ps);
        State state = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());
        while (true) {
            Action action= agent.selectAction(state, SAMPLE_COUNT);
            state = sim.step(action);
            if (sim.isGoalState(state) || state == null) {
                break;
            }
        }

    }
}
