package botmate.mcts;

import simulator.State;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CarState implements TreeState {
    public int roundSimulation;
    public long nodeScore;
    private List<StateCostPair> successors;
    private State state;


    public CarState(State state) {
        this.successors = new LinkedList<StateCostPair>();
        this.state = state;
    }


    @Override
    public List<StateCostPair> getSuccessors() {
        ArrayList<StateCostPair> temp = new ArrayList<StateCostPair>(successors);
        return temp;
    }

    @Override
    public boolean equals(TreeState s) {
        return false;
    }

    @Override
    public State outputState() {
        return this.state;
    }

    public void addSuccessor(TreeState state, double score) {
        successors.add(new StateCostPair(state, score));
    }


    public long simulation() {
        CarState carState = this;

        expand();
        return MCTS.evaluate(carState.state);
    }



    //expand node
    public void expand() {

    }

    // back propagation
    public void backpropagation(final long score){

    }

    public void randomSampleCarState(){

    }


}
