package botmate;

import problem.*;
import simulator.State;

import java.util.*;

public class Agent {

    class Node {
        Action action;
        double value;
        int visitCount;
        Node(Action action) {
            this.action = action;
            this.value = 0;
            this.visitCount = 0;
        }
    }

    private static final double EXP_CONST = Math.sqrt(2.0);
    private static final double EPSILON = 1e-6;
    private Random random = new Random();
    private ProblemSpec ps;
    private List<Node> nodes;
    private int visitCount;

    Agent(ProblemSpec ps) {
        this.ps = ps;
    }

    public Action selectAction(State currentState, int numberOfSample) {
        visitCount = 0;
        nodes = new ArrayList<>();
        for (Action action: generateActions(currentState)) {
            nodes.add(new Node(action));
        }
        int count = 0;
        while (count < numberOfSample){
            Node node = this.getBestNode();
            double value = runSimulation(node, currentState);
            updateStats(node, value);
            count++;
        }
        return this.getBestNode().action;
    }

    private Node getBestNode() {
        Node selected = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Node node : nodes) {
            double UCT = node.value + EXP_CONST * Math.sqrt(Math.log(visitCount+1)/(node.visitCount+ EPSILON));
            if (UCT > bestValue) {
                selected = node;
                bestValue = UCT;
            }
        }
        return selected;
    }

    private Action getRandomAction(State state) {
        List<Action> actions = generateActions(state);
        return actions.get(random.nextInt(actions.size()));
    }

    private double runSimulation(Node node, State state) {
        Action action = getRandomAction(state);
        // TODO: add simulation here by random action
        return random.nextInt(10);
    }

    private void updateStats(Node node, double value) {
        node.visitCount++;
        node.value += value;
    }

    private List<Action> generateActions(State state) {

        //TODO: prune the invalid actions for example,
        //TODO: if the fuel tank is full, no need to add fuel, can't change to the same driver or tire

        List<Action> actions = new ArrayList<>();

        actions.add(new Action(ActionType.MOVE));

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR)) {
            for (String car : ps.getCarOrder()) {
                if (state!=null && !car.equals(state.getCarType())) {
                    actions.add(new Action(ActionType.CHANGE_CAR, car));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_DRIVER)) {
            for (String driver : ps.getDriverOrder()) {
                if (state!=null && !driver.equals(state.getDriver())) {
                    actions.add(new Action(ActionType.CHANGE_DRIVER, driver));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRES)) {
            for (Tire tire : ps.getTireOrder()) {
                if (state!=null && !tire.equals(state.getTireModel())) {
                    actions.add(new Action(ActionType.CHANGE_TIRES, tire));
                }
            }
        }


        // TODO: Review this to prune as much as possible
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

}

