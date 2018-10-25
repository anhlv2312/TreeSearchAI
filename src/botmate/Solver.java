package botmate;

import botmate.adts.*;
import botmate.mcts.CarState;
import botmate.mcts.StateCostPair;
import problem.*;
import simulator.*;


import java.io.IOException;
import java.util.List;

public class Solver {
    public static void main(String[] args) {
        try {
            ProblemSpec ps = new ProblemSpec("botmate.input1.txt");
            Simulator sim = new Simulator(ps);
//            solve(ps, sim);
            solvebytree(ps,sim);
        } catch (IOException e) {
            System.out.println("IO Exception occurred");
            System.exit(1);
        }
    }

    private static void solve(ProblemSpec ps, Simulator sim) {
        // initialize a graph (true: directed)
        Graph<State, Action> states = new AdjacencyMapGraph<>(true);
        State initialState, nextState;

        // Generate the initial state
        initialState = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());
        System.out.print(initialState);

        // Insert the initial state into the graph
        Vertex<State> initialVertex = states.insertVertex(initialState);

        // Initialize a sample action
        Action firstAction = new Action(ActionType.MOVE);

        // Simulate the action and get the next state
        nextState = sim.step(firstAction);

        // Insert the next state into the graph
        Vertex<State> nextVertex = states.insertVertex(nextState);

        // Insert the edge (of action) from initial to next state,
        // the edge could be Action or an object of action and probability
        states.insertEdge(initialVertex, nextVertex, firstAction);
        System.out.print(nextState);

    }

    private static void solvebytree(ProblemSpec ps, Simulator sim){

        // Generate the initial state
        State initialState, nextState;
        initialState = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());
        CarState initial = new CarState(initialState);


        // Initialize first sample action and second movement
        Action firstAction = new Action(ActionType.MOVE);
        nextState = sim.step(firstAction);
        CarState second = new CarState(nextState);
        initial.addSuccessor(second,10);

        // Simulate the second action and third movement
        Action secondAction = new Action(ActionType.MOVE);
        nextState = sim.step(secondAction);
        CarState third = new CarState(nextState);
        initial.addSuccessor(third,10);

        //print first state
        System.out.print(initialState);

        // loop for every next state
        List<StateCostPair> pathToGoal = initial.getSuccessors();
        for (int i = 0; i < pathToGoal.size(); i++) {
            System.out.print(pathToGoal.get(i).treeState.outputState());
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
