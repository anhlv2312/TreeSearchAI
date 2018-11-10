package botmate;

import problem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreeNode {

    private Random random;
    private Action action;
    private TreeNode parent;
    private List<TreeNode> children;
    private double value;
    private int visitCount;

    public TreeNode(Action action) {
        this.random = new Random();
        this.action = action;
        this.children = new ArrayList<>();
    }

    public Action getAction() {
        return action;
    }

    public TreeNode getParent() {
        return parent;
    }

    public TreeNode getRandomChild() {
        return children.get(random.nextInt(children.size()));
    }

    public List<TreeNode> getChildren() {
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
            TreeNode newNode = new TreeNode(action);
            newNode.parent = this;
            children.add(newNode);
        }
    }

    public TreeNode selectPromisingChild(double constant) {
        TreeNode selected = children.get(0);
        double bestValue = children.get(0).getValue();
        for (TreeNode childNode : children) {
            if (childNode.visitCount == 0) {
                return childNode;
            }
            double utcValue = childNode.value + constant * Math.sqrt(Math.log(this.visitCount) / (childNode.visitCount));
            if (utcValue >= bestValue) {
                selected = childNode;
                bestValue = utcValue;
            }
        }
        return selected;
    }

    public TreeNode selectBestChild() {
        TreeNode selected = children.get(0);
        TreeNode moveNode = null;
        double total = 0;
        int count = 0;
        double bestValue = children.get(0).getValue();
        for (TreeNode childNode : children) {
            count += 1;
            total += childNode.getValue();
            if (childNode.getAction().getActionType().equals(ActionType.MOVE)) {
                moveNode = childNode;
            }
            if (childNode.getValue() > bestValue) {
                selected = childNode;
                bestValue = childNode.getValue();
            }
        }

        double averageValue = total/count;
        if (moveNode != null && (bestValue/averageValue < Solver.MOVING_BIAS) && moveNode.getValue() > averageValue) {
            return moveNode;
        } else {
            return selected;
        }
    }

}
