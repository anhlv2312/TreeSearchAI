package botmate;

import problem.*;
import simulator.*;

import java.io.IOException;

public class Solver {

    public static final int EXPLORATION_TIMEOUT = 10000;
    public static final int EXPLORATION_CONSTANT = EXPLORATION_TIMEOUT/150;

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

    public static boolean solveProblem(ProblemSpec ps, Simulator sim) {
        double startTime = System.currentTimeMillis();
        Agent agent = new Agent(ps);
        State currentState = sim.reset();

        int currentPos = currentState.getPos();

        while (!sim.isGoalState(currentState) && currentState != null) {
            System.out.print("Step: " + sim.getSteps() + " - ");
            currentPos = currentState.getPos();
            Action action = agent.selectBestAction(currentState, ps.getMaxT()-sim.getSteps());
            currentState = sim.step(action);
        }

        System.out.println(String.format("Total time: %.2f seconds", (System.currentTimeMillis() - startTime)/1000));

        if (sim.isGoalState(currentState)) {
            System.out.println("Reached the goal in " + sim.getSteps() + "/" +  ps.getMaxT() + " steps" );
            return true;
        }

        System.out.println("Failed at position " + currentPos + "/" + ps.getN() );
        return false;

    }

}
