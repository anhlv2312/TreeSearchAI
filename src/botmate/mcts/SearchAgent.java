package botmate.mcts;


import java.util.List;

/**
 * An interface for classes which allow searching for a path from an initial
 * treeState to a goal treeState.
 *
 * Part of the solution code for COMP3702/7702 Tutorial 2.
 *
 * Created by Nicholas Collins on 8/08/2017.
 */
public interface SearchAgent {

    /**
     * Search for a path between a given initial treeState and goal treeState.
     *
     * @param initial the initial stage
     * @param goal    the goal treeState
     * @return a list of states and costs representing a path from the
     * initial treeState to the goal treeState.
     */
    List<StateCostPair> search(TreeState initial, TreeState goal);

    /**
     * @return total number of nodes in the tree
     */
    int totNodes();
}
