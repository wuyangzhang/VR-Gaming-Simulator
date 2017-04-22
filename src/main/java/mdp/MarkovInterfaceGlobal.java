package mdp;

import topology.Node;
import user.User;

import java.util.*;

/**
 * Created by Wuyang on 12/1/16.
 */
public final class MarkovInterfaceGlobal extends MarkovInterface {
    public interface EdgeCloudFilter {
        boolean IsFit(User user, Node edgeCandidate);
    }

    private Collection<User> users;
    private EdgeCloudFilter filter;

    public MarkovInterfaceGlobal(Collection<User> users, Collection<Node> edgeClouds, EdgeCloudFilter filter) {
        //initiate allStates && allActions
        this.users = users;
        this.filter = filter;

        ArrayList<MarkovInterfaceLocal> localInterfaces = new ArrayList<>();
        for(User u : users) {
            ArrayList<Node> edgeCloudsForUser = new ArrayList<>();
            for (Node n : edgeClouds){
                if (filter.IsFit(u, n)) {
                    edgeCloudsForUser.add(n);
                }
            }
            //localInterfaces.add(new MarkovInterfaceLocal(u, edgeCloudsForUser));
        }

        calActionRecursive(localInterfaces, 0, new ArrayList<MarkovActionLocal>());
        calStateRecursive(localInterfaces, 0, new ArrayList<MarkovStateLocal>());
    }

    private void calActionRecursive(ArrayList<MarkovInterfaceLocal> localInterfaces, int userId,
                               ArrayList<MarkovActionLocal> selectedActions) {
        if (userId == localInterfaces.size() - 1) {
            for (MarkovAction action : localInterfaces.get(userId).allPossibleActions) {
                selectedActions.add((MarkovActionLocal) action);
                allPossibleActions.add(new MarkovActionGlobal(users, selectedActions));
                selectedActions.remove(selectedActions.size() - 1);
            }
        } else {
            for (MarkovAction action : localInterfaces.get(userId).allPossibleActions) {
                selectedActions.add((MarkovActionLocal) action);
                calActionRecursive(localInterfaces, userId + 1, selectedActions);
                selectedActions.remove(selectedActions.size() - 1);
            }
        }
    }

    private void calStateRecursive(ArrayList<MarkovInterfaceLocal> localInterfaces, int userId,
                                    ArrayList<MarkovStateLocal> selectedStates) {
        if (userId == localInterfaces.size() - 1) {
            for (MarkovState state : localInterfaces.get(userId).allPossibleStates) {
                selectedStates.add((MarkovStateLocal) state);
                allPossibleStates.add(new MarkovStateGlobal(selectedStates));
                selectedStates.remove(selectedStates.size() - 1);
            }
        } else {
            for (MarkovState state : localInterfaces.get(userId).allPossibleStates) {
                selectedStates.add((MarkovStateLocal) state);
                calStateRecursive(localInterfaces, userId + 1, selectedStates);
                selectedStates.remove(selectedStates.size() - 1);
            }
        }
    }

    public MarkovState getCurrentGlobalState(){
        ArrayList<MarkovStateLocal> localStates = new ArrayList<>();
        for(User user : this.users){
            MarkovStateLocal stateLocal = new MarkovStateLocal(user, user.getCurrentLocation(), user.getEdgeCloudLocation(), user.getCentralCloudLocation());
            localStates.add(stateLocal);
        }
        return new MarkovStateGlobal(localStates);
    }

}
