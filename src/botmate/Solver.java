package botmate;

import problem.*;
import simulator.*;
import java.io.IOException;

public class Solver {

    private final static int SAMPLE_COUNT = 1000;

    private static ProblemSpec ps;
    private static Simulator simulator;
    private static Agent agent;
    private static State initialState;


    public static void main(String[] args) {
        try {
            ps = new ProblemSpec(args[1]);
            simulator = new Simulator(ps, args[2]);
            solve(ps);
        } catch (IOException e) {
            System.out.println("IO Exception occurred");
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Missing input/output file");
            System.exit(1);
        }

        initialState = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());

        agent = new Agent(ps);

    }

    private static void solve(ProblemSpec ps) {

        State currentState = initialState;
        while (true) {
            Action action= agent.selectAction(currentState, SAMPLE_COUNT);
            currentState = simulator.step(action);
            if (simulator.isGoalState(currentState) || currentState == null) {
                break;
            }
        }

    }
}
