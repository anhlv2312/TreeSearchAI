package botmate;

import problem.*;
import simulator.*;

import java.io.IOException;

public class Solver {

    public static void main(String[] args) {

        try {
            ProblemSpec ps = new ProblemSpec(args[0]);
            Simulator sim = new Simulator(ps, args[1]);
            solveProblem(ps, sim);
        } catch (IOException | ArrayIndexOutOfBoundsException exception) {
            System.out.println("Error while loading input/output file: " + exception.toString());
            System.exit(1);
        }

    }

    public static void solveProblem(ProblemSpec ps, Simulator sim) {
        Agent agent = new Agent(ps);
        State state = sim.reset();
        while (true) {
            System.out.print(state);
            Action action = agent.selectBestAction(state);
            System.out.println(action.getActionType());
            state = sim.step(action);
            if (sim.isGoalState(state) || state == null) {
                break;
            }
        }

    }
}
