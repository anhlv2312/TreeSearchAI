package botmate;

import problem.Action;
import simulator.State;

import java.util.HashMap;

public class Node {
    private Node parentNode;
    private State state;
    private Double value;
    private int visitCount;
    private HashMap<Action, Integer> actionCount;

    public Node(Node parentNode, State state) {
        this.parentNode = parentNode;
        this.state = state;
        this.value = 0.0;
        this.visitCount = 0;
        this.actionCount = new HashMap<>();
    }

    public Node getParentNode() {
        return parentNode;
    }

    public State getState() {
        return state;
    }

    public Double getValue() {
        return value;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public HashMap<Action, Integer> getActionCount() {
        return actionCount;
    }


}
