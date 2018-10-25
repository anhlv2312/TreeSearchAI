package botmate.mcts;

/**
 * A class storing a treeState and a cost to travel to it.
 *
 * Part of the solution code for COMP3702/7702 Tutorial 2.
 *
 * Created by Nicholas Collins on 8/08/2017.
 */
public class StateCostPair implements Comparable<StateCostPair> {

    public TreeState treeState;
    public double cost;

    /**
     * Construct a treeState score pair
     * @param treeState an agent problem treeState
     * @param score score to travel to the treeState
     */
    public StateCostPair(TreeState treeState, double score) {
        this.treeState = treeState;
        this.cost = score;
    }

    /**
     * Required to allow StateCostPair to be used in a priority queue. Refer to
     * comment in SearchTreeNode.java
     * @param s StateCostPair to compare to
     * @return -1 if this node has a lower travel cost than pair s
     *          0 if this node has the same travel cost as pair s
     *          1 if this node has a greater travel cost than pair s
     */
    public int compareTo(StateCostPair s) {
        return Double.compare(this.cost, s.cost);
    }
}
