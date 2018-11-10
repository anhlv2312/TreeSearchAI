package botmate;

import problem.*;
import simulator.State;

import java.util.*;

public class Agent {

    private ProblemSpec ps;
    private RollOutSimulator sim;
    private Random random;
    private int remainingStep;
    private List<TirePressure> tirePressures;

    Agent(ProblemSpec ps) {
        this.ps = ps;
        this.random = new Random();
        tirePressures = new ArrayList<>();
        tirePressures.add(TirePressure.ONE_HUNDRED_PERCENT);
        tirePressures.add(TirePressure.SEVENTY_FIVE_PERCENT);
        tirePressures.add(TirePressure.FIFTY_PERCENT);
    }

    public Action selectBestAction(State currentState, int remainingStep) {
        long startTime = System.currentTimeMillis();

        TreeNode rootNode = new TreeNode(null);
        this.remainingStep = remainingStep;
        this.sim = new RollOutSimulator(ps, currentState);

        System.out.print(currentState);

        while (System.currentTimeMillis() - startTime < Solver.EXPLORATION_TIMEOUT) {
            TreeNode promisingNode = selectPromisingNode(rootNode);
            promisingNode.expand(generateActions(sim.getCurrentState()));
            TreeNode nodeToExpand = promisingNode.getRandomChild();
            sim.step(nodeToExpand.getAction());
            nodeToExpand.setValue(rollOut());
            backPropagation(nodeToExpand);
        }


        for (TreeNode node : rootNode.getChildren()) {
            System.out.print("A" + node.getAction().getActionType().getActionNo() + "(" + node.getVisitCount() + "|" + node.getValue() + ") ");
        }
        System.out.println();
        TreeNode bestNode = rootNode.selectBestChild();
        System.out.println(bestNode.getAction().getActionType() + " (" + bestNode.getVisitCount() + "|" + bestNode.getValue() + ") ");
        System.out.println();

        return bestNode.getAction();

    }

    private TreeNode selectPromisingNode(TreeNode rootNode) {
        sim.reset();
        TreeNode currentNode = rootNode;
        while (!currentNode.isLeafNode()) {
            currentNode = currentNode.selectPromisingChild();
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

    private double rollOut() {
        double value = 0;
        int step = 0;
        while (sim.getSteps() < ps.getMaxT()) {
            step +=1;
            State currentState = sim.getCurrentState();
            int currentTerrainIndex = ps.getTerrainIndex(ps.getEnvironmentMap()[currentState.getPos() - 1]);
            int currentCarIndex = ps.getCarIndex(currentState.getCarType());

            if (currentState.getFuel() < ps.getFuelUsage()[currentTerrainIndex][currentCarIndex]) {
                sim.step(new Action(ActionType.ADD_FUEL, 50));
            }

            sim.step(new Action(ActionType.MOVE));
            if (sim.isGoalState(sim.getCurrentState())) {
                value = ps.getN() + ProblemSpec.CAR_MAX_MOVE;
                break;
            }


            value = sim.getCurrentState().getPos();


        }
//        System.out.println(value);
        return value;

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

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR))
        //        && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER))
        {
            for (String car : ps.getCarOrder()) {
                if (!car.equals(currentState.getCarType())) {
                    actions.add(new Action(ActionType.CHANGE_CAR, car));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_DRIVER))
                // && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER))
        {
            for (String driver : ps.getDriverOrder()) {
                if (!driver.equals(currentState.getDriver())) {
                    actions.add(new Action(ActionType.CHANGE_DRIVER, driver));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRES))
              //  && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE))
        {
            for (Tire tire : ps.getTireOrder()) {
                if (!tire.equals(currentState.getTireModel())) {
                    actions.add(new Action(ActionType.CHANGE_TIRES, tire));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.ADD_FUEL))
        //        && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE))
        {
            for (int i = 1; i < (50 - currentState.getFuel()) / 10; i++) {
                actions.add(new Action(ActionType.ADD_FUEL, i * 10));
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_PRESSURE))
        //        && !ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE))
        {
            for (TirePressure tirePressure : tirePressures) {
                if (!tirePressure.equals(currentState.getTirePressure())) {
                    actions.add(new Action(ActionType.CHANGE_PRESSURE, tirePressure));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER)) {
            for (String car : ps.getCarOrder()) {
                for (String driver : ps.getDriverOrder()) {
                    if (!car.equals(currentState.getCarType()) &&
                            !driver.equals(currentState.getDriver())) {
                        actions.add(new Action(ActionType.CHANGE_CAR_AND_DRIVER, car, driver));
                    }
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRE_FUEL_PRESSURE)) {
            for (Tire tire : ps.getTireOrder()) {
                for (TirePressure tirePressure : tirePressures) {
                    if (!tire.equals(currentState.getTireModel()) && !tirePressure.equals(currentState.getTirePressure())) {
                        for (int i = 1; i < (50 - currentState.getFuel()) / 10; i++) {
                            actions.add(new Action(ActionType.CHANGE_TIRE_FUEL_PRESSURE, tire, i * 10, tirePressure));
                        }
                    }
                }
            }
        }

        return actions;
    }

}

