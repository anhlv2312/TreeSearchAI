package botmate;

import problem.*;
import simulator.State;

import java.util.*;

public class LearningAgent {

    private final static int SAMPLE_COUNT = 100000;

    class Condition {
        private String car;
        private String driver;
        private Tire tire;
        private TirePressure tirePressure;

        public Condition(String car, String driver, Tire tire, TirePressure tirePressure) {
            this.car = car;
            this.driver = driver;
            this.tire = tire;
            this.tirePressure = tirePressure;
        }

        public List<Action> generateAction(Condition previousCondition) {
            List<Action> actions = new ArrayList<>();
            if (!previousCondition.car.equals(this.car)) {
                actions.add(new Action(ActionType.CHANGE_CAR, this.car));
            }
            if (!previousCondition.driver.equals(this.driver)) {
                actions.add(new Action(ActionType.CHANGE_DRIVER, this.driver));
            }
            if (!previousCondition.tire.equals(this.tire)) {
                actions.add(new Action(ActionType.CHANGE_TIRES, this.tire));
            }
            if (this.tirePressure != null && !previousCondition.tirePressure.equals(this.tirePressure)) {
                actions.add(new Action(ActionType.CHANGE_PRESSURE, this.tirePressure));
            }
            return actions;
        }
    }

    class ConditionNode implements Comparable<ConditionNode> {
        Condition condition;
        double value;
        int visitCount;

        ConditionNode(Condition condition) {
            this.condition = condition;
            this.value = Double.POSITIVE_INFINITY;
            this.visitCount = 0;
        }

        ConditionNode(String car, String driver, Tire tire, TirePressure tirePressure) {
            this(new Condition(car, driver, tire, tirePressure));
        }

        @Override
        public int compareTo(ConditionNode o) {
            if (this.value > o.value) {
                return -1;
            } else if (this.value > o.value) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private Map<Terrain, Condition> policy = new HashMap<>();
    private PriorityQueue<ConditionNode> conditionNodes = new PriorityQueue<>();
    private ProblemSpec ps;
    private ActionSimulator sim;

    public LearningAgent(ProblemSpec ps) {
        this.ps = ps;
        this.sim = new ActionSimulator(ps);
    }

    public List<Action> getActions(State state) {
        Condition currentCondition = this.getCurrentCondition(state);
        Terrain terrain = ps.getEnvironmentMap()[state.getPos()];
        if (policy.containsKey(terrain)) {
            return policy.get(terrain).generateAction(currentCondition);
        } else {
            Condition bestCondition = selectBestCondition(state);
            policy.put(terrain, bestCondition);
            return bestCondition.generateAction(currentCondition);
        }
    }

    private Condition getCurrentCondition(State state) {
        return new Condition(state.getCarType(), state.getDriver(), state.getTireModel(), state.getTirePressure());
    }

    private Condition selectBestCondition(State state) {
        conditionNodes = generatePossibleCondition(state);
        ConditionNode node = conditionNodes.poll();

        for (int i = 1; i < SAMPLE_COUNT; i++) {
            sim.setCurrentState(state);
            List<Action> actions = node.condition.generateAction(getCurrentCondition(state));
            for (Action action : actions) {
                sim.step(action);
            }
            int value = sim.sampleMoveDistance();
            if (value == ProblemSpec.SLIP || value == ProblemSpec.BREAKDOWN) {
                value = -10;
            }
            node.value = (node.value * node.visitCount + value) / (node.visitCount + 1);
            node.visitCount++;
        }
        System.out.println(node.visitCount);
        return conditionNodes.poll().condition;

    }

    private PriorityQueue<ConditionNode> generatePossibleCondition(State state) {
        PriorityQueue<ConditionNode> nodes = new PriorityQueue<>();
        for (String car : ps.getCarOrder()) {
            for (String driver : ps.getDriverOrder() ){
                for (Tire tire : ps.getTireOrder()) {
                    if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_PRESSURE)) {
                        nodes.add(new ConditionNode(car, driver, tire, TirePressure.ONE_HUNDRED_PERCENT));
                        nodes.add(new ConditionNode(car, driver, tire, TirePressure.SEVENTY_FIVE_PERCENT));
                        nodes.add(new ConditionNode(car, driver, tire, TirePressure.FIFTY_PERCENT));
                    } else {
                        nodes.add(new ConditionNode(car, driver, tire, null));
                    }
                }
            }
        }
        return nodes;
    }


}
