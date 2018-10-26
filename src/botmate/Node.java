package botmate;

import problem.Action;

import java.util.List;

public class Node {
    public Action action;
    public List<Node> children;
    public double value;
    public int visitCount;

    public Node(Action action) {
        this.action = action;
        this.value = 0;
        this.visitCount = 0;
    }


}
