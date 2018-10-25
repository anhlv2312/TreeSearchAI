package botmate.mcts;

import simulator.State;

import java.util.List;


/**
 * An interface for classes representing states in an agent problem.
 *
 * Part of the solution code for COMP3702/7702 Tutorial 2.
 *
 * Created by Nicholas Collins on 8/08/2017.
 */
public interface TreeState {
    /**
     * Return the set of states which can be reached from this state by
     * performing a valid action.
     * @return list of successor states
     */
    List<StateCostPair> getSuccessors();

    /**
     * Return true if this state is the same as state s
     * @param s state to check equality with
     * @return true if this state is equal to state s
     */
    boolean equals(TreeState s);

    /**
     * Represent this state as a string
     * @return string representation of this state
     */

    State outputState();
}