package botmate;

import problem.Action;

import java.util.ArrayList;
import java.util.List;

public class ActionNode {
    private ActionNode parent;
    private List<ActionNode> children;
    private Action action;
    private double value;
    private int visitCount;

    ActionNode(Action action) {
        this.action = action;
        this.value = 0;
        this.visitCount = 0;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public void setParent(ActionNode node) {
        this.parent = node;
    }

    public void addChild(ActionNode node){
        this.children.add(node);
    }

    public Action getAction() {
        return action;
    }

    public double getValue() {
        return value;
    }

    public List<ActionNode> getChildren() {
        return children;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void increaseVisitCount() {
        visitCount++;
    }

    public void updateValue(double value) {
        this.value = value;
    }

}