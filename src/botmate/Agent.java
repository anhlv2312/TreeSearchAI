package botmate;

import problem.*;
import simulator.Simulator;
import simulator.State;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Agent {
    private ProblemSpec ps;
    private Simulator sim;
    private Node rootNode;
    private Node currentNode;
    private List<State> solution;

    public Agent(ProblemSpec ps, Simulator sim) {
        this.ps = ps;
        this.sim = sim;
        State initialState = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());
        solution = new LinkedList<>();
        solution.add(initialState);
        rootNode = new Node(initialState);
        currentNode = rootNode;
    }

    public List<State> search() {
        List<State> solution = new LinkedList<>();

        for (Action action : generateActions()) {
            System.out.println(action.getActionType() + " " + action.getCarType());
        }

        return solution;
    }

    private List<Node> expandNode() {
        List<State> states = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();

        return nodes;
    }

    private Action selectAction() {
        return generateActions().get(0);
    }

    private List<Action> generateActions() {

        //TODO: prune the invalid actions for example,
        //TODO: if the fuel tank is full, no need to add fuel, can't change to the same driver or tire


        List<Action> actions = new ArrayList<>();

        actions.add(new Action(ActionType.MOVE));

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR)) {
            for (String car : ps.getCarOrder()) {
                actions.add(new Action(ActionType.CHANGE_CAR, car));
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_DRIVER)) {
            for (String driver : ps.getDriverOrder()) {
                actions.add(new Action(ActionType.CHANGE_DRIVER, driver));
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRES)) {
            for (Tire tire : ps.getTireOrder()) {
                actions.add(new Action(ActionType.CHANGE_TIRES, tire));
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.ADD_FUEL)) {
            for (int i = 1; i < 5; i++) {
                actions.add(new Action(ActionType.ADD_FUEL, i * 10));
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_PRESSURE)) {
            actions.add(new Action(ActionType.CHANGE_PRESSURE, TirePressure.ONE_HUNDRED_PERCENT));
            actions.add(new Action(ActionType.CHANGE_PRESSURE, TirePressure.SEVENTY_FIVE_PERCENT));
            actions.add(new Action(ActionType.CHANGE_PRESSURE, TirePressure.FIFTY_PERCENT));
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER)) {
            for (String car : ps.getCarOrder()) {
                for (String driver : ps.getDriverOrder()) {
                    actions.add(new Action(ActionType.CHANGE_CAR_AND_DRIVER, car, driver));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE)) {
            for (Tire tire : ps.getTireOrder()) {
                for (int i = 1; i < 5; i++) {
                    actions.add(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tire, i * 10, TirePressure.ONE_HUNDRED_PERCENT));
                    actions.add(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tire, i * 10, TirePressure.SEVENTY_FIVE_PERCENT));
                    actions.add(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tire, i * 10, TirePressure.FIFTY_PERCENT));
                }
            }
        }

        return actions;
    }

    private boolean isFinish(State currentState) {
        return sim.isGoalState(currentState);
    }

}
