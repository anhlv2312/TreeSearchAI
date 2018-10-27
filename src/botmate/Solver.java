package botmate;

import problem.*;
import simulator.*;

import java.io.IOException;

public class Solver {



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
        solveProblemLearning();
    }

    private static void solveProblemLearning() {
        LearningAgent agent = new LearningAgent(ps);
        State state = sim.reset();
        while (true) {
            System.out.println(ps.getEnvironmentMap()[state.getPos()]);
            for (Action action : agent.getActions(state)) {
                sim.step(action);
            }
            state = sim.step(new Action(ActionType.MOVE));
            if (sim.isGoalState(state) || state == null) {
                break;
            }
            if (state.getFuel() < 10) {
                sim.step(new Action(ActionType.ADD_FUEL, 40));
            }
        }

    }

    private static void solveProblem() {
        Agent agent = new Agent(ps);
        State state = sim.reset();
        while (true) {
            Action action = agent.selectAction(state);
            state = sim.step(action);
            if (sim.isGoalState(state) || state == null) {
                break;
            }
        }

    }
}
