package edgecloud.mdp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wuyang on 12/8/16.
 */
public class Action extends MarkovAction {

    public int action;

    public Action(int action) {
        this.action = action;
    }

    @Override
    public Map<MarkovState, Double> getActionResults(MarkovState current) {
        State a = new State(0), b = new State(1);

        State s = (State) current;
        Map<MarkovState, Double> ret = new HashMap<>();
        if (s.name == 0) {
            if (action == 0) {
                ret.put(a, .5);
                ret.put(b, .5);
            } else {
                ret.put(a, .5);
                ret.put(b, .5);
            }
        } else {
            if (action == 0) {
                ret.put(a, .0);
                ret.put(b, 1.0);
            } else {
                ret.put(a, .1);
                ret.put(b, .9);
            }
        }

        return ret;
    }

    @Override
    public String toString() {
        return "Action{" + "action=" + action + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.action;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Action other = (Action) obj;
        if (this.action != other.action) {
            return false;
        }
        return true;
    }
    
    

}
