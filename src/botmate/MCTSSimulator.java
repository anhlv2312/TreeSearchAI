package botmate;

import problem.*;
import simulator.State;

public class MCTSSimulator {

    private ProblemSpec ps;
    private State currentState;
    private int steps;

    public MCTSSimulator(ProblemSpec ps) {
        this.ps = ps;
        reset();
    }

    public void setCurrentState(State state) {
        this.currentState = state;
    }


    private State reset() {
        steps = 0;
        currentState = State.getStartState(ps.getFirstCarType(),
                ps.getFirstDriver(), ps.getFirstTireModel());
        return currentState.copyState();
    }

    public State step(Action a) throws IllegalArgumentException {
        State nextState;
        if (!actionValidForLevel(a)) {
            throw new IllegalArgumentException("ActionType A"
                    + a.getActionType().getActionNo()
                    + " is an invalid action for problem level "
                    + ps.getLevel());
        }

        switch(a.getActionType().getActionNo()) {
            case 1:
                nextState = performA1();
                break;
            case 2:
                nextState = performA2(a);
                break;
            case 3:
                nextState = performA3(a);
                break;
            case 4:
                nextState = performA4(a);
                break;
            case 5:
                nextState = performA5(a);
                break;
            case 6:
                nextState = performA6(a);
                break;
            case 7:
                nextState = performA7(a);
                break;
            default:
                nextState = performA8(a);
        }

        // handle slip and breakdown cases, we do this now so we can generate
        // correct output format
        if (nextState.isInSlipCondition()) {
            // remain in same state but certain number of steps pass
            // -1 since we add 1 later
            steps += ps.getSlipRecoveryTime() - 1;
            nextState = nextState.changeSlipCondition(false);
        } else if (nextState.isInBreakdownCondition()) {
            steps += ps.getRepairTime() - 1;
            nextState = nextState.changeBreakdownCondition(false);
        }

        steps += 1;
        currentState = nextState.copyState();

        return nextState;
    }

    private boolean actionValidForLevel(Action a) {
        return ps.getLevel().isValidActionForLevel(a.getActionType());
    }

    private State performA1() {

        State nextState;

        // check there is enough fuel to make move in current state
        int fuelRequired = getFuelConsumption();
        int currentFuel = currentState.getFuel();
        if (fuelRequired > currentFuel) {
            return currentState;
        }

        // Sample move distance
        int moveDistance = sampleMoveDistance();

        // handle slip and breakdown cases, addition of steps handled in step method
        if (moveDistance == ProblemSpec.SLIP) {
            nextState = currentState.changeSlipCondition(true);
        } else if (moveDistance == ProblemSpec.BREAKDOWN) {
            nextState = currentState.changeBreakdownCondition(true);
        } else {
            nextState = currentState.changePosition(moveDistance, ps.getN());
        }

        // handle fuel usage for level 2 and above
        if (ps.getLevel().getLevelNumber() > 1) {
            nextState = nextState.consumeFuel(fuelRequired);
        }

        return nextState;
    }

    public int sampleMoveDistance() {

        double[] moveProbs = getMoveProbs();

        double p = Math.random();
        double pSum = 0;
        int move = 0;
        for (int k = 0; k < ProblemSpec.CAR_MOVE_RANGE; k++) {
            pSum += moveProbs[k];
            if (p <= pSum) {
                move = ps.convertIndexIntoMove(k);
                break;
            }
        }
        return move;
    }

    private double[] getMoveProbs() {

        // get parameters of current state
        Terrain terrain = ps.getEnvironmentMap()[currentState.getPos() - 1];
        int terrainIndex = ps.getTerrainIndex(terrain);
        String car = currentState.getCarType();
        String driver = currentState.getDriver();
        Tire tire = currentState.getTireModel();

        // calculate priors
        double priorK = 1.0 / ProblemSpec.CAR_MOVE_RANGE;
        double priorCar = 1.0 / ps.getCT();
        double priorDriver = 1.0 / ps.getDT();
        double priorTire = 1.0 / ProblemSpec.NUM_TYRE_MODELS;
        double priorTerrain = 1.0 / ps.getNT();
        double priorPressure = 1.0 / ProblemSpec.TIRE_PRESSURE_LEVELS;

        // get probabilities of k given parameter
        double[] pKGivenCar = ps.getCarMoveProbability().get(car);
        double[] pKGivenDriver = ps.getDriverMoveProbability().get(driver);
        double[] pKGivenTire = ps.getTireModelMoveProbability().get(tire);
        double pSlipGivenTerrain = ps.getSlipProbability()[terrainIndex];
        double[] pKGivenPressureTerrain = convertSlipProbs(pSlipGivenTerrain);

        // use bayes rule to get probability of parameter given k
        double[] pCarGivenK = bayesRule(pKGivenCar, priorCar, priorK);
        double[] pDriverGivenK = bayesRule(pKGivenDriver, priorDriver, priorK);
        double[] pTireGivenK = bayesRule(pKGivenTire, priorTire, priorK);
        double[] pPressureTerrainGivenK = bayesRule(pKGivenPressureTerrain,
                (priorTerrain * priorPressure), priorK);

        // use conditional probability formula on assignment sheet to get what
        // we want (but what is it that we want....)
        double[] kProbs = new double[ProblemSpec.CAR_MOVE_RANGE];
        double kProbsSum = 0;
        double kProb;
        for (int k = 0; k < ProblemSpec.CAR_MOVE_RANGE; k++) {
            kProb = magicFormula(pCarGivenK[k], pDriverGivenK[k],
                    pTireGivenK[k], pPressureTerrainGivenK[k], priorK);
            kProbsSum += kProb;
            kProbs[k] = kProb;
        }

        // Normalize
        for (int k = 0; k < ProblemSpec.CAR_MOVE_RANGE; k++) {
            kProbs[k] /= kProbsSum;
        }

        return kProbs;
    }

    private double[] convertSlipProbs(double slipProb) {

        // Adjust slip probability based on tire pressure
        TirePressure pressure = currentState.getTirePressure();
        if (pressure == TirePressure.SEVENTY_FIVE_PERCENT) {
            slipProb *= 2;
        } else if (pressure == TirePressure.ONE_HUNDRED_PERCENT) {
            slipProb *= 3;
        }
        // Make sure new probability is not above max
        if (slipProb > ProblemSpec.MAX_SLIP_PROBABILITY) {
            slipProb = ProblemSpec.MAX_SLIP_PROBABILITY;
        }

        // for each terrain, all other action probabilities are uniform over
        // remaining probability
        double[] kProbs = new double[ProblemSpec.CAR_MOVE_RANGE];
        double leftOver = 1 - slipProb;
        double otherProb = leftOver / (ProblemSpec.CAR_MOVE_RANGE - 1);
        for (int i = 0; i < ProblemSpec.CAR_MOVE_RANGE; i++) {
            if (i == ps.getIndexOfMove(ProblemSpec.SLIP)) {
                kProbs[i] = slipProb;
            } else {
                kProbs[i] = otherProb;
            }
        }

        return kProbs;
    }

    private double[] bayesRule(double[] condProb, double priorA, double priorB) {

        double[] swappedProb = new double[condProb.length];

        for (int i = 0; i < condProb.length; i++) {
            swappedProb[i] = (condProb[i] * priorA) / priorB;
        }
        return swappedProb;
    }

    private double magicFormula(double pA, double pB, double pC, double pD,
                               double priorE) {
        return pA * pB * pC * pD * priorE;
    }

    private int getFuelConsumption() {

        // get parameters of current state
        Terrain terrain = ps.getEnvironmentMap()[currentState.getPos() - 1];
        String car = currentState.getCarType();
        TirePressure pressure = currentState.getTirePressure();

        // get fuel consumption
        int terrainIndex = ps.getTerrainIndex(terrain);
        int carIndex = ps.getCarIndex(car);
        int fuelConsumption = ps.getFuelUsage()[terrainIndex][carIndex];

        if (pressure == TirePressure.FIFTY_PERCENT) {
            fuelConsumption *= 3;
        } else if (pressure == TirePressure.SEVENTY_FIVE_PERCENT) {
            fuelConsumption *= 2;
        }
        return fuelConsumption;
    }

    private State performA2(Action a) {

        if (currentState.getCarType().equals(a.getCarType())) {
            // changing to same car type does not change state but still costs a step
            // no cheap refill here, muhahaha
            return currentState;
        }

        return currentState.changeCarType(a.getCarType());
    }

    private State performA3(Action a) { return currentState.changeDriver(a.getDriverType()); }

    private State performA4(Action a) {
        return currentState.changeTires(a.getTireModel());
    }

    private State performA5(Action a) {
        // calculate number of steps used for refueling (minus 1 since we add
        // 1 in main function
        int stepsRequired = (int) Math.ceil(a.getFuel() / (float) 10);
        steps += (stepsRequired - 1);
        return currentState.addFuel(a.getFuel());
    }

    private State performA6(Action a) {
        return currentState.changeTirePressure(a.getTirePressure());
    }

    private State performA7(Action a) {
        if (currentState.getCarType().equals(a.getCarType())) {
            // if car the same, only change driver so no sneaky fuel exploit
            return currentState.changeDriver(a.getDriverType());
        }
        return currentState.changeCarAndDriver(a.getCarType(),
                a.getDriverType());
    }

    private State performA8(Action a) {
        return currentState.changeTireFuelAndTirePressure(a.getTireModel(),
                a.getFuel(), a.getTirePressure());
    }


    public Boolean isGoalState(State s) {
        if (s == null) {
            return false;
        }
        return s.getPos() >= ps.getN();
    }

    public int getSteps() {
        return steps;
    }


}
