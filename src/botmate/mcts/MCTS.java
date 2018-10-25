package botmate.mcts;


import simulator.State;

import java.util.*;

/**
 *
 * A generic implementation of MTCSs.
 *
 *
 */
public class MCTS implements SearchAgent {

    private Stack<SearchTreeNode> container;



    public MCTS() {
        container = new Stack<SearchTreeNode>();
    }


    public SearchTreeNode selection(){

        return null;
    }


    /* method to evaluate current position based on probability of position*/
    public static long evaluate(final State state){
        return 0;

    }


    /* Create Random Sample State */
    private static void createRandom(){

    }

    /**
     * Resets the search agent (clears instance variables to be ready for next
     * search request).
     */
    private void reset() {
        container.clear();

    }

    @Override
    public List<StateCostPair> search(TreeState initial, TreeState goal) {
        return null;
    }

    @Override
    public int totNodes() {
        return 0;
    }
}