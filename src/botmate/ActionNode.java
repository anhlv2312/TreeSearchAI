package botmate;

import problem.Action;
import problem.ProblemSpec;

public class ActionNode extends Node {

    public Action action;

    public ActionNode(ProblemSpec ps, Action action) {
        super(ps);
        this.action = action;
    }

}