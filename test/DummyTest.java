import botmate.InputGenerator;
import botmate.Solver;
import org.junit.Test;
import problem.Action;
import problem.ActionType;
import problem.ProblemSpec;
import problem.TirePressure;
import simulator.Simulator;
import simulator.State;

import java.io.IOException;

public class DummyTest {


    @Test
    public void dummyTest() throws IOException {

        double startTime = System.currentTimeMillis();

        ProblemSpec ps = new ProblemSpec("examples/level_5/input_lvl5.txt");
//        ProblemSpec ps = new ProblemSpec("test.input.txt");
        Simulator sim = new Simulator(ps, "test.dummy.txt");

        State currentState = sim.reset();
        System.out.print(currentState);
        currentState = sim.step(new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT));

        while (!sim.isGoalState(currentState) && currentState != null) {
            System.out.print(currentState);
            currentState = sim.step(new Action(ActionType.MOVE));

            if (currentState != null && currentState.getFuel() < 20 ) {
                currentState = sim.step(new Action(ActionType.ADD_FUEL, 30));
            }

        }

        System.out.println(String.format("Total time: %.2f seconds", (System.currentTimeMillis() - startTime)/1000));

        if (sim.isGoalState(currentState)) {
            System.out.println("Reached the goal in " + sim.getSteps() + "/" +  ps.getMaxT() + " steps" );
        } else if (currentState == null) {
            System.out.println("Failed!" );
        }

    }

}
