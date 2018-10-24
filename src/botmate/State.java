package botmate;

import problem.Tire;
import problem.TirePressure;

public class State extends simulator.State {

    public State(int pos, boolean slip, int slipTimeLeft, boolean breakdown,
                 int breakdownTimeLeft, String carType, int fuel,
                 TirePressure tirePressure, String driver, Tire tireModel) {
        super(pos, slip, slipTimeLeft, breakdown, breakdownTimeLeft, carType, fuel, tirePressure, driver, tireModel);
    }

    public String toString() {
        String output = "";
        output += ("(");
        output += (this.getPos() + ",");
        output += (this.isInSlipCondition()?1:0 + ",");
        output += (this.isInBreakdownCondition()?1:0 + ",");
        output += (this.getCarType() + ",");
        output += (this.getDriver() + ",");
        output += (this.getTireModel() + ",");
        output += (this.getFuel() + ",");
        output += (this.getTirePressure());
        output += (")");
        return output;
    }
}
