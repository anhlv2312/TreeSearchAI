package botmate;

import problem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActionNode {

    private Random random;
    private Action action;
    private ActionNode parent;
    private List<ActionNode> children;
    private double value;
    private int visitCount;

    public ActionNode(Action action) {
        this.random = new Random();
        this.action = action;
        this.children = new ArrayList<>();
    }

    public Action getAction() {
        return action;
    }

    public ActionNode getParent() {
        return parent;
    }

    public ActionNode getRandomChild() {
        return children.get(random.nextInt(children.size()));
    }

    public List<ActionNode> getChildren() {
        return children;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isLeafNode() {
        return children.isEmpty();
    }

    public void increaseVisitCount() {
        visitCount++;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void expand(List<Action> actions) {
        for (Action action: actions) {
            ActionNode newNode = new ActionNode(action);
            newNode.parent = this;
            children.add(newNode);
        }
    }

    public ActionNode selectBestNode() {
        ActionNode selected = new ActionNode(null);
        double bestValue = -Double.MAX_VALUE;
        for (ActionNode childNode : children) {
            if (childNode.visitCount == 0) {
                return childNode;
            }
            double utcValue = childNode.value + Agent.EXPLORATION_CONST * Math.sqrt(Math.log(this.visitCount) / (childNode.visitCount));
            if (utcValue > bestValue) {
                selected = childNode;
                bestValue = utcValue;
            }
        }
        return selected;
    }

}
