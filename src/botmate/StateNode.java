package botmate;

import problem.Action;

import java.util.ArrayList;
import java.util.List;

public class StateNode {
    private StateNode parent;
    private List<StateNode> children;
    private Action action;
    private double value;
    private int visitCount;

    StateNode(Action action) {
        this.action = action;
        this.value = 0;
        this.visitCount = 0;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public void setParent(StateNode node) {
        this.parent = node;
    }

    public void addChild(StateNode node){
        this.children.add(node);
    }

    public Action getAction() {
        return action;
    }

    public double getValue() {
        return value;
    }

    public List<StateNode> getChildren() {
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