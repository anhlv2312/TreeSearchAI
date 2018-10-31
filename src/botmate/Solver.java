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
        double startTime = System.currentTimeMillis();
        Agent agent = new Agent(ps);
        State currentState = sim.reset();
        while (true) {
            System.out.print(currentState);
            Action action = agent.selectBestAction(currentState, ps.getMaxT()-sim.getSteps());
            currentState = sim.step(action);
            if (sim.isGoalState(currentState)) {
                System.out.println("Reached the GOAL!" );
                break;
            } else if (currentState == null) {
                System.out.println("Failed!" );
                break;
            }
        }
        System.out.println(String.format("Total time: %.2f seconds", (System.currentTimeMillis() - startTime)/1000));
    }
}
