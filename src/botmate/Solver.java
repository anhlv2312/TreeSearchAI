package botmate;

import problem.*;
import simulator.*;
import java.io.IOException;



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
        Agent agent = new Agent(ps, sim);
        agent.search();
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
