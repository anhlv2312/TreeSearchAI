package botmate;

import problem.*;
import simulator.State;

import java.util.*;

public class Agent {

    public static final double EXPLORATION_CONST = 100; // Math.sqrt(2);
    public static final double GAMMA = 0.9;
    public static final int TIMEOUT = 1000;
    public static final int SIMULATION_DEPTH = 30;
    private ProblemSpec ps;
    private MCTSSimulator sim;
    private State rootState;
    private ActionNode rootNode;
    private Random random;

    Agent(ProblemSpec ps) {
        this.ps = ps;
        this.random = new Random();
    }

    public Action selectBestAction(State currentState) {
        long startTime = System.currentTimeMillis();

        rootState = currentState;
        rootNode = new ActionNode(null);
        this.sim = new MCTSSimulator(ps, rootState);


        while (System.currentTimeMillis() - startTime < TIMEOUT) {
            ActionNode promisingNode = selectPromisingNode(rootNode);
            promisingNode.expand(generateActions(sim.getCurrentState()));
            ActionNode nodeToExpand = promisingNode.getRandomChild();
            sim.step(nodeToExpand.getAction());

            nodeToExpand.setValue(rollOut());
            backPropagation(nodeToExpand);
        }

        for (ActionNode node : rootNode.getChildren()) {
            System.out.print(node.getAction().getActionType() + " (" + node.getVisitCount() + ", " + (int)node.getValue() + ") | ");
        }
        System.out.println();
        return rootNode.selectBestNode().getAction();

    }

    private ActionNode selectPromisingNode(ActionNode rootNode) {
        sim.reset();
        ActionNode currentNode = rootNode;
        while (!currentNode.isLeafNode()) {
            currentNode = currentNode.selectBestNode();
            sim.step(currentNode.getAction());
        }
        return currentNode;
    }

    private void backPropagation(ActionNode leafNode) {
        ActionNode currentNode = leafNode;
        while (currentNode != null) {
            currentNode.increaseVisitCount();
            double q = currentNode.getValue();
            ActionNode parentNode = currentNode.getParent();
            if (parentNode!=null) {
                parentNode.setValue((parentNode.getValue() * parentNode.getVisitCount() + q) / (parentNode.getVisitCount() + 1));
            }
            currentNode = parentNode;
        }
    }

    private double rollOut() {
        double value = 0;
        // TODO: change to iterate to the maximum of the number of remaining step
        for (int i = 0; i <= SIMULATION_DEPTH; i ++) {
            State previousState = sim.getCurrentState();
            List<Action> actions = generateActions(previousState);
            State currentState = sim.step(actions.get(random.nextInt(actions.size())));
            if (sim.isGoalState(currentState)) {
                value += 2 * ps.getN() * Math.pow(GAMMA, i);
            } else {
                value += currentState.getPos() * Math.pow(GAMMA, i);
            }
        }
        return value;
    }

    private List<Action> generateActions(State currentState) {

        //TODO: prune the invalid actions for example,
        //TODO: if the fuel tank is full, no need to add fuel, can't change to the same driver or tire

        List<Action> actions = new ArrayList<>();

        actions.add(new Action(ActionType.MOVE));

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR)) {
            for (String car : ps.getCarOrder()) {
                if (currentState != null && !car.equals(currentState.getCarType())) {
                    actions.add(new Action(ActionType.CHANGE_CAR, car));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_DRIVER)) {
            for (String driver : ps.getDriverOrder()) {
                if (currentState != null && !driver.equals(currentState.getDriver())) {
                    actions.add(new Action(ActionType.CHANGE_DRIVER, driver));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRES)) {
            for (Tire tire : ps.getTireOrder()) {
                if (currentState != null && !tire.equals(currentState.getTireModel())) {
                    actions.add(new Action(ActionType.CHANGE_TIRES, tire));
                }
            }
        }

        // TODO: Review this to prune as much as possible
        if (ps.getLevel().isValidActionForLevel(ActionType.ADD_FUEL)) {
            if (currentState != null && currentState.getFuel() < 10) {
                actions = new ArrayList<>();
                for (int i = 1; i < (50 - currentState.getFuel()) / 10; i++) {
                    actions.add(new Action(ActionType.ADD_FUEL, i * 10));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_PRESSURE)) {

            List<TirePressure> tirePressures = new ArrayList<>();

            tirePressures.add(TirePressure.ONE_HUNDRED_PERCENT);
            tirePressures.add(TirePressure.SEVENTY_FIVE_PERCENT);
            tirePressures.add(TirePressure.FIFTY_PERCENT);

            for (TirePressure tirePressure : tirePressures) {
                if (currentState != null && !tirePressure.equals(currentState.getTirePressure())) {
                    actions.add(new Action(ActionType.CHANGE_PRESSURE, tirePressure));
                }
            }

        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER)) {
            for (String car : ps.getCarOrder()) {
                for (String driver : ps.getDriverOrder()) {
                    actions.add(new Action(ActionType.CHANGE_CAR_AND_DRIVER, car, driver));
                }
            }
        }
//
//        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE)) {
//            for (Tire tire : ps.getTireOrder()) {
//                for (int i = 1; i < 5; i++) {
//                    actions.add(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tire, i * 10, TirePressure.ONE_HUNDRED_PERCENT));
//                    actions.add(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tire, i * 10, TirePressure.SEVENTY_FIVE_PERCENT));
//                    actions.add(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tire, i * 10, TirePressure.FIFTY_PERCENT));
//                }
//            }
//        }

        return actions;
    }

}

