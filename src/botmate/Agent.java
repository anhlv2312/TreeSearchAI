package botmate;

import problem.*;
import simulator.State;

import java.util.*;

public class Agent {

    private static final double EXP_CONST = 1;
    private static final double EPSILON = 1e-6;
    private static final double GAMMA = 0.95;
    private final static int SAMPLE_COUNT = 10000;
    private Random random = new Random();
    private ProblemSpec ps;
    private MCTSSimulator sim;
    private List<StateNode> nodes;
    private State rootState;
    private StateNode rootNode;
    private int visitCount;

    Agent(ProblemSpec ps) {
        this.ps = ps;
    }

    public Action selectBestAction(State currentState) {
        rootState = currentState;
        visitCount = 0;
        rootNode = new StateNode(null);
        expand(rootNode, rootState);
        while (true) {

        }


        nodes = new ArrayList<>();

        while (visitCount < SAMPLE_COUNT){
            StateNode node = this.getBestNode();
            runSimulation(node, currentState);
            visitCount++;
        }
        return this.getBestNode().getAction();
    }


    private void expand(StateNode currentNode, State currentState) {
        for (Action action: generateActions(currentState)) {
            nodes.add(new StateNode(action));
        }
    }

    private StateNode getBestNode() {
        StateNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (StateNode node : nodes) {
            double UCT = node.getValue() + EXP_CONST * Math.sqrt(Math.log(visitCount+1)/(node.getVisitCount()+ EPSILON));
            if (UCT > bestValue) {
                selected = node;
                bestValue = UCT;
            }
        }
        return selected;
    }

    private Action selectRandomAction(State state) {
        List<Action> actions = generateActions(state);
        return actions.get(random.nextInt(actions.size()));
    }

    private void runSimulation(StateNode node, State state) {
        int depth = 30;
        this.sim = new MCTSSimulator(ps);
        this.sim.setCurrentState(state);
        State currentState = state;
        State nextState = sim.step(node.getAction());
        node.updateValue(nextState.getPos() - state.getPos());
        double totalValue = 0.0;

        for (int i = 0; i <= depth; i ++) {
            int previousPos = currentState.getPos();
            currentState = sim.step(selectRandomAction(currentState));
            totalValue += (currentState.getPos() - previousPos) * Math.pow(GAMMA, depth);
        }
        node.updateValue((node.getValue() * node.getVisitCount() + totalValue) / (node.getVisitCount() + 1));
        node.increaseVisitCount();
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
            if (state!=null && state.getFuel() < 10) {
                actions = new ArrayList<>();
                for (int i = 1; i < (50 - state.getFuel()) / 10; i++) {
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
                if (state!=null && !tirePressure.equals(state.getTirePressure())) {
                    actions.add(new Action(ActionType.CHANGE_PRESSURE, tirePressure));
                }
            }

        }

//        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR_AND_DRIVER)) {
//            for (String car : ps.getCarOrder()) {
//                for (String driver : ps.getDriverOrder()) {
//                    actions.add(new Action(ActionType.CHANGE_CAR_AND_DRIVER, car, driver));
//                }
//            }
//        }
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

//    private  double backTracking(StateNode node, int depth){
//        double totalValue = 0.0;
//        StateNode currentNode = node ;
//
//        try {
//            while (depth >= 0) {
//                currentNode = currentNode.parent;
//                if (currentNode.children != null) {
//                    totalValue += currentNode.child.value + (getReward(currentNode.state) * Math.pow(GAMMA, depth));
//                } else {
//                    totalValue += (getReward(currentNode.state) * Math.pow(GAMMA, depth));
//                }
//                currentNode.value = (currentNode.value * currentNode.visitCount + totalValue) / (currentNode.visitCount + 1);
//
//                depth--;
//            }
//        }catch (Exception ex){
//            System.out.println(ex.toString());
//        }
//
//        return currentNode.value;
//    }

    private int getReward(State state) {
        if (sim.isGoalState(state)) {
            return 2 * ps.getN();
        } else {
            return state.getPos();
        }
    }

}

