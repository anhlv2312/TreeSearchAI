package botmate;

import problem.*;
import simulator.State;

import java.util.ArrayList;
import java.util.List;

public class StateNode extends Node {

    public State state;

    StateNode(ProblemSpec ps, State state) {
        super(ps);
        this.state = state;
    }

    public void expand() {
        for (Action action : generateActions()) {
            ActionNode actionNode = new ActionNode(ps, action);
            actionNode.parent = this;
            children.add(actionNode);

        }
    }

    private List<Action> generateActions() {

        //TODO: prune the invalid actions for example,
        //TODO: if the fuel tank is full, no need to add fuel, can't change to the same driver or tire

        List<Action> actions = new ArrayList<>();

        actions.add(new Action(ActionType.MOVE));

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_CAR)) {
            for (String car : ps.getCarOrder()) {
                if (state != null && !car.equals(state.getCarType())) {
                    actions.add(new Action(ActionType.CHANGE_CAR, car));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_DRIVER)) {
            for (String driver : ps.getDriverOrder()) {
                if (state != null && !driver.equals(state.getDriver())) {
                    actions.add(new Action(ActionType.CHANGE_DRIVER, driver));
                }
            }
        }

        if (ps.getLevel().isValidActionForLevel(ActionType.CHANGE_TIRES)) {
            for (Tire tire : ps.getTireOrder()) {
                if (state != null && !tire.equals(state.getTireModel())) {
                    actions.add(new Action(ActionType.CHANGE_TIRES, tire));
                }
            }
        }

        // TODO: Review this to prune as much as possible
        if (ps.getLevel().isValidActionForLevel(ActionType.ADD_FUEL)) {
            if (state != null && state.getFuel() < 10) {
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
                if (state != null && !tirePressure.equals(state.getTirePressure())) {
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
}