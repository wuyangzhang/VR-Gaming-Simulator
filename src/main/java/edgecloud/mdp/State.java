package edgecloud.mdp;

import java.util.HashMap;

/**
 * Created by Wuyang on 12/8/16.
 */
public class State extends MarkovState {

    public int name;

    public State(int name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name == 0 ? "A" : "B";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) return false;
        return ((State)obj).name == name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.name;
        return hash;
    }

}
