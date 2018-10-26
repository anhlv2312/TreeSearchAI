package botmate;

import problem.*;
import simulator.Simulator;
import simulator.State;

import java.util.*;

public class Agent {
    private static final double EXP_CONST = Math.sqrt(2.0);
    private Random random = new Random();
    private ProblemSpec ps;
    private Simulator simulator;
    private Node rootNode;
    private State currentState;
    static double epsilon = 1e-6;

    public Agent(ProblemSpec ps) {
        this.ps = ps;
        this.simulator = new Simulator(ps);
        rootNode = new Node(null);
        currentState = State.getStartState(ps.getFirstCarType(), ps.getFirstDriver(), ps.getFirstTireModel());
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void runSimulation() {
        simulator.reset();
        List<Node> visited = new LinkedList<>();
        Node current = this.rootNode;
        visited.add(current);
        while (current.children != null) {
            current = select(current);
            visited.add(current);
            currentState = simulator.step(current.action);
        }
        expand(current);
        Node newNode = select(current);
        visited.add(newNode);
        double value = rollOut(newNode);
        for (Node node : visited) {
            updateStats(node, value);
        }
    }

    private void expand(Node node) {
//        System.out.println("Expanding " + node);
        node.children = new LinkedList<>();
        for (Action action: generateActions()) {
            node.children.add(new Node(action));
        }
    }

    public Node select(Node node) {
        Node selected = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Node child : node.children) {
            double UCT = child.value + EXP_CONST * Math.sqrt(Math.log(node.visitCount+1)/(child.visitCount+epsilon));
            if (UCT > bestValue) {
                selected = child;
                bestValue = UCT;
            }
        }
        return selected;
    }


    public double rollOut(Node node) {
        List<Action> actions = generateActions();
        Action action = actions.get(random.nextInt(actions.size()));
        simulator.step(action);
        State nextState = simulator.step(new Action(ActionType.MOVE));
        if (nextState == null) {
            return -10;
        } else {
            return nextState.getPos() - currentState.getPos();
        }
    }

    public void updateStats(Node node, double value) {
        node.visitCount++;
        node.value += value;
//        System.out.println("Updating " + node + " " + node.value + "/" + node.visitCount);
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

}
