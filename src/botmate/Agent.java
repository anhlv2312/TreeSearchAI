package botmate;

import problem.*;
import simulator.State;

import java.util.*;

public class Agent {

    private ProblemSpec ps;
    private RollOutSimulator sim;
    private int startPos;
    private int remainingStep;
    private List<TirePressure> tirePressures;

    Agent(ProblemSpec ps) {
        this.ps = ps;
        tirePressures = new ArrayList<>();
        tirePressures.add(TirePressure.ONE_HUNDRED_PERCENT);
        tirePressures.add(TirePressure.SEVENTY_FIVE_PERCENT);
        tirePressures.add(TirePressure.FIFTY_PERCENT);
    }

    public Action selectBestAction(State currentState, int remainingStep) {
        long startTime = System.currentTimeMillis();

        TreeNode rootNode = new TreeNode(null);
        this.remainingStep = remainingStep;
        this.startPos = currentState.getPos();
        this.sim = new RollOutSimulator(ps, currentState);

        System.out.print(currentState);

        while (System.currentTimeMillis() - startTime < Solver.EXPLORATION_TIMEOUT) {
            TreeNode promisingNode = selectPromisingNode(rootNode);
            promisingNode.expand(generateActions(sim.getCurrentState()));

            RollOutSimulator rollSim = new RollOutSimulator(ps, sim);
            for (TreeNode nodeToRollOut : promisingNode.getChildren() ) {
                double value = rollOut(rollSim, nodeToRollOut.getAction());
                nodeToRollOut.setValue(value);
                backPropagation(nodeToRollOut);
            }
        }

        int totalVisit = 0;
        double totalValue = 0;
        int nodeCount = 0;
        for (TreeNode node : rootNode.getChildren()) {
            System.out.print("A" + node.getAction().getActionType().getActionNo() + "(" + node.getVisitCount() + "|"
                    + String.format( "%.3f", node.getValue()) + ") ");
            totalVisit += node.getVisitCount();
            totalValue += node.getValue();
            nodeCount += 1;
        }
        System.out.println();
        TreeNode bestNode = rootNode.selectBestChild();
        System.out.println(bestNode.getAction().getActionType()
                + " | Value: " + String.format( "%.3f", bestNode.getValue())
                + " | Average: " + String.format( "%.3f", totalValue/nodeCount)
                + " | Visit: " + bestNode.getVisitCount()
                + " | Total: " + totalVisit
                + " | Nodes: " + nodeCount
                + " | Percentage: " + bestNode.getVisitCount()*100/totalVisit + "%");

        System.out.println();

        return bestNode.getAction();

    }

    private TreeNode selectPromisingNode(TreeNode rootNode) {
        sim.reset();
        TreeNode currentNode = rootNode;
        while (!currentNode.isLeafNode()) {
            currentNode = currentNode.selectPromisingChild(Solver.EXPLORATION_CONSTANT);
            sim.step(currentNode.getAction());
        }
        return currentNode;
    }

    private void backPropagation(TreeNode leafNode) {
        TreeNode currentNode = leafNode;
        while (currentNode != null) {
            currentNode.increaseVisitCount();
            double q = currentNode.getValue();
            TreeNode parentNode = currentNode.getParent();
            if (parentNode!=null) {
                parentNode.setValue((parentNode.getValue() * parentNode.getVisitCount() + q) / (parentNode.getVisitCount() + 1));
            }
            currentNode = parentNode;
        }
    }

    private double rollOut(RollOutSimulator rollSim, Action action) {
        rollSim.reset();
        rollSim.step(action);

        State currentState = rollSim.getCurrentState();
        int currentCarIndex = ps.getCarIndex(currentState.getCarType());
        while (rollSim.getSteps() <= remainingStep) {
            int currentTerrainIndex = ps.getTerrainIndex(ps.getEnvironmentMap()[currentState.getPos() - 1]);
            if (currentState.getFuel() < ps.getFuelUsage()[currentTerrainIndex][currentCarIndex]) {
                break;
            }
            if (rollSim.isGoalState(currentState)) {
                return ps.getN() - startPos + ProblemSpec.CAR_MAX_MOVE;
            }
            currentState = rollSim.step(new Action(ActionType.MOVE));
        }
        return currentState.getPos() - startPos;
    }

    private List<Action> generateActions(State currentState) {

        List<Action> actions = new ArrayList<>();

        if (currentState == null) {
            return actions;
        }

        int currentTerrainIndex = ps.getTerrainIndex(ps.getEnvironmentMap()[currentState.getPos() - 1]);
        int currentCarIndex = ps.getCarIndex(currentState.getCarType());

        if (currentState.getFuel() >= ps.getFuelUsage()[currentTerrainIndex][currentCarIndex]) {
            actions.add(new Action(ActionType.MOVE));
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR) && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER)) {
            for (String car : ps.getCarOrder()) {
                if (!car.equals(currentState.getCarType())) {
                    actions.add(new Action(ActionType.CHANGE_CAR, car));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_DRIVER) && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER)) {
            for (String driver : ps.getDriverOrder()) {
                if (!driver.equals(currentState.getDriver())) {
                    actions.add(new Action(ActionType.CHANGE_DRIVER, driver));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRES) && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE)) {
            for (Tire tire : ps.getTireOrder()) {
                if (!tire.equals(currentState.getTireModel())) {
                    actions.add(new Action(ActionType.CHANGE_TIRES, tire));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.ADD_FUEL) && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE)) {
            for (int i = 1; i <= (50 - currentState.getFuel()) / 10; i++) {
                actions.add(new Action(ActionType.ADD_FUEL, i * 10));
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_PRESSURE) && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE)) {
            for (TirePressure tirePressure : tirePressures) {
                if (!tirePressure.equals(currentState.getTirePressure())) {
                    actions.add(new Action(ActionType.CHANGE_PRESSURE, tirePressure));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER)) {
            for (String car : ps.getCarOrder()) {
                for (String driver : ps.getDriverOrder()) {
                    if (!(car.equals(currentState.getCarType()) && driver.equals(currentState.getDriver()))) {
                        actions.add(new Action(ActionType.CHANGE_CAR_AND_DRIVER, car, driver));
                    }
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE)) {
            for (Tire tire : ps.getTireOrder()) {
                for (TirePressure tirePressure : tirePressures) {
                    if (!(tire.equals(currentState.getTireModel()) && tirePressure.equals(currentState.getTirePressure()))) {
                        for (int i = 0; i <= (50 - currentState.getFuel()) / 10; i++) {
                            actions.add(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tire, i * 10, tirePressure));
                        }
                    }
                }
            }
        }

        return actions;
    }

}

