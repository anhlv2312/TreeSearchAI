package botmate;

import problem.ProblemSpec;
import problem.TirePressure;

import java.io.IOException;

public class Solver {
    public static void main(String[] args) {
        ProblemSpec ps;
        try {
            ps = new ProblemSpec("botmate.input1.txt");
            solve(ps);
        } catch (IOException e) {
            System.out.println("IO Exception occurred");
            System.exit(1);
        }
        System.out.println("Finished loading!");

    }

    private static void solve(ProblemSpec ps) {
        State initialState;
        initialState = new State(1, false, 0, false, 0, ps.getFirstCarType(), ProblemSpec.FUEL_MAX,
                TirePressure.ONE_HUNDRED_PERCENT, ps.getFirstDriver(), ps.getFirstTireModel());
        System.out.println(initialState.toString());

    }

}
