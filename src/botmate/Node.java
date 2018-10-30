package botmate;

import problem.ProblemSpec;

import java.util.List;

public abstract class Node {

    public Node parent;
    public List<Node> children;
    public double value;
    public int visitCount;
    public ProblemSpec ps;

    public Node(ProblemSpec ps) {
        this.ps = ps;

    }

}
