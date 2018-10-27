package botmate;

import problem.*;

import java.util.ArrayList;
import java.util.List;

public class Condition {
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
