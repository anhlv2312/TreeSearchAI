package botmate;

import problem.Action;
import simulator.State;

import java.util.*;

public class Node {
    public State state;
    public double value;
    public int visitCount;
    public List<Node> children;
    public Map<Action, Integer> actionCount;

    public Node(State state) {
        this.state = state;
        this.value = 0.0;
        this.visitCount = 0;
        this.children = new ArrayList<>();
        this.actionCount = new HashMap<>();
    }

}
