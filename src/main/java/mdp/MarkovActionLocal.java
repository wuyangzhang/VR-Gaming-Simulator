/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mdp;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import topology.Link;
import topology.Node;
import user.User;

/**
 *
 * @author ubuntu
 */
public class MarkovActionLocal extends MarkovAction {

    private final Node newServerLocation;

    public MarkovActionLocal(Node newServerLocation) {
        this.newServerLocation = newServerLocation;
    }

    public Node getNewServerLocation() {
        return newServerLocation;
    }

    @Override
    public Map<MarkovState, Double> getActionResults(MarkovState current) {
        MarkovStateLocal state = (MarkovStateLocal) current;
        User u = state.getUser();
        Map<Link, Double> transitionProbability = u.getTrace().getTransitionProbability().get(state.getUserLocation());
        Map<MarkovState, Double> ret = new HashMap<>();
        //long remaining = 1;
        for (Map.Entry<Link, Double> entry : transitionProbability.entrySet()) {
            Link key = entry.getKey();
            Double value = entry.getValue();
            //remaining -= value;
            ret.put(new MarkovStateLocal(u, key.getnTo(), newServerLocation, state.getCentralServerLocation()), value);
        }

        /*
        if (remaining < -0.00001) {
            throw new IllegalStateException(String.format("Transition function total > 1, user=%s, location=%s", u, state.getUserLocation()));
        }
        */
        return ret;
    }

    @Override
    public String toString(){
        return String.format("optimal edge : %s", this.newServerLocation);
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.newServerLocation);
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
        if (!(obj instanceof MarkovActionLocal)) {
            return false;
        }
        final MarkovActionLocal other = (MarkovActionLocal) obj;
        return Objects.equals(this.newServerLocation, other.newServerLocation);
    }

    public static void main(String args[]){
    }

}
