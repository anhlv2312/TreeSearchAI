package botmate;

import problem.*;
import simulator.*;
import java.io.IOException;

public class Solver {
    public static void main(String[] args) {
        try {
            ProblemSpec ps = new ProblemSpec("botmate.input1.txt");
            solve(ps);
        } catch (IOException e) {
            System.out.println("IO Exception occurred");
            System.exit(1);
        }
    }

    private static void solve(ProblemSpec ps) {

        Simulator simulator = new Simulator(ps, "botmate.output1.txt");
        Agent agent = new Agent(ps);
        State currentState = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());
        while (!simulator.isGoalState(currentState)) {
            Action action= agent.selectAction(currentState, 1000);
            currentState = simulator.step(action);
            if (currentState == null) {
                break;
            }
        }



    }
}
