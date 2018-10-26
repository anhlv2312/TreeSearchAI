package botmate;

import problem.*;
import simulator.*;

import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;


public class Solver {
    public static void main(String[] args) {
        try {
            ProblemSpec ps = new ProblemSpec("botmate.input1.txt");
            Simulator sim = new Simulator(ps);
            solve(ps, sim);
        } catch (IOException e) {
            System.out.println("IO Exception occurred");
            System.exit(1);
        }
    }

    private static void solve(ProblemSpec ps, Simulator sim) {
        // initialize a Tree (true: directed)
        TreeMap<State, Integer> states = new TreeMap<>();
        State initialState, nextState;

        // Generate the initial state
        initialState = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());
        System.out.print(initialState);


        states.put(initialState, 0);
        Action firstAction = new Action(ActionType.MOVE);
        nextState = sim.step(firstAction);
        System.out.print(nextState);

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
