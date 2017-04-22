package mdp;

import topology.Node;
import topology.Topology;
import user.User;

import java.util.Collection;

/**
 * Created by Wuyang on 12/1/16.
 */
public final class MarkovInterfaceLocal extends MarkovInterface {
    private final Topology tp;

    public boolean actionFilter(User user, Node edgeCloud, double metric){
        Node userLocation = user.getCurrentLocation();
        double distance = tp.getRouteCost().get(userLocation).get(edgeCloud);
        if(distance > metric)
            return false;
        else
            return true;
    }

    public MarkovInterfaceLocal(User user, Collection<Node> edgeClouds, Topology tp){
        this.tp = tp;
        for(Node edgeLocation : edgeClouds){
            /*
            if(actionFilter(user, edgeLocation, 2)){
                allPossibleActions.add(new MarkovActionLocal(edgeLocation));
            }
            */

            allPossibleActions.add(new MarkovActionLocal(edgeLocation));
            //@para user.getTrace().getTransitionProbability().keySet(), all locations a user can be in
            for(Node userLocation : user.getTrace().getTransitionProbability().keySet()){
                allPossibleStates.add(new MarkovStateLocal(user, userLocation, edgeLocation, user.getCentralCloudLocation()));
            }
        }
    }
}
