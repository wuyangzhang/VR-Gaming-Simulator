package edgecloud.mdp;

import java.util.*;

import user.User;

/**
 * @author ubuntu
 */
public final class MarkovStateGlobal extends MarkovState {

    private final HashMap<User, MarkovStateLocal> localStates = new HashMap<>();

    public MarkovStateGlobal(Collection<MarkovStateLocal> markovLocalStates) {
        for (MarkovStateLocal local : markovLocalStates) {
            localStates.put(local.getUser(), local);
        }
    }

    public HashMap<User, MarkovStateLocal> getLocalStates() {
        return this.localStates;
    }

    public Collection<User> getUsers(){
        return this.localStates.keySet();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<User, MarkovStateLocal> entry : localStates.entrySet()) {
            sb.append(String.format("user: %s -> connects to %s", entry.getKey().getName(), entry.getValue()));
        }
        return sb.toString();

    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (Map.Entry<User, MarkovStateLocal> entry : localStates.entrySet()) {
            hash = 11 * hash + Objects.hashCode(entry.getKey()) + Objects.hashCode(entry.getValue());
        }
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
        if (!(obj instanceof MarkovStateGlobal)) {
            return false;
        }
        final MarkovStateGlobal other = (MarkovStateGlobal) obj;

        if(other.localStates.size() != this.localStates.size()){
            return false;
        }

        Object[] myStates = this.localStates.values().toArray();
        Object[] otherStates = other.localStates.values().toArray();
        for(int i = 0; i < myStates.length; i++){
            MarkovStateLocal myState = (MarkovStateLocal)myStates[i];
            MarkovStateLocal otherState = (MarkovStateLocal)otherStates[i];
            if(!myState.equals(otherState)){
                return false;
            }
        }
        return true;
    }

}