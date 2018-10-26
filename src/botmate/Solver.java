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

        Simulator simulator = new Simulator(ps);
        Agent agent = new Agent(ps);

        int count = 0;
        while (count<10000) {
            agent.runSimulation();
            count++;
        }
        Node current = agent.getRootNode();
        State currentState;
        int step = 0;
        while (current.children != null) {
            step++;
            current = agent.select(current);
            currentState = simulator.step(current.action);
            System.out.print("Step " + step + ": \t" + current.action.getActionType() + " - " + currentState);
        }

    }

    private static String toString(State state) {
        String output = "";
        output += ("(");
        output += (state.getPos() + ",");
        output += (state.isInSlipCondition()?1:0 + ",");
        output += (state.isInBreakdownCondition()?1:0 + ",");
        output += (state.getCarType() + ",");
        output += (state.getDriver() + ",");
        output += (state.getTireModel() + ",");
        output += (state.getFuel() + ",");
        output += (state.getTirePressure());
        output += (")");
        return output;
    }

}
