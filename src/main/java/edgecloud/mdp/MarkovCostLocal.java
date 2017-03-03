package edgecloud.mdp;

import topology.Node;
import topology.Topology;

/**
 * Created by Wuyang on 12/1/16.
 */
public final class MarkovCostLocal extends MarkovCost {

    public MarkovCostLocal(Topology tp, double a, double b, double c, double d, double e) {
        this.tp = tp;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    @Override
    public double calculateReward(MarkovState preState, MarkovAction action, MarkovState transitState) {
        //double responsePlus = a * (computeResponseTime(preState) - computeResponseTime(transitState));
        double responsePlus = a * computeResponseTime(preState, transitState);
        double migrationCost = b * computeMigrationCost(preState, transitState);
        return responsePlus - migrationCost;
    }

    /*
    private double computeResponseTime(MarkovState state) {
        Node userLocation = ((MarkovStateLocal) state).getUserLocation();
        Node edgeLocation = ((MarkovStateLocal) state).getEdgeCloudLocation();
        Node centralLocation = ((MarkovStateLocal) state).getCentralServerLocation();
        return 2 * tp.getRouteCost().get(userLocation).get(edgeLocation) + tp.getRouteCost().get(edgeLocation).get(centralLocation);
    }
    */
    private double computeResponseTime(MarkovState preState, MarkovState transitState){
        Node userLocation = ((MarkovStateLocal) transitState).getUserLocation();
        Node preEdge = ((MarkovStateLocal) preState).getEdgeCloudLocation();
        Node transitEdge = ((MarkovStateLocal) transitState).getEdgeCloudLocation();
        Node centralLocation = ((MarkovStateLocal) preState).getCentralServerLocation();
        double preResp = 2 * tp.getRouteCost().get(userLocation).get(preEdge) + tp.getRouteCost().get(preEdge).get(centralLocation);
        double transitResp = 2 * tp.getRouteCost().get(userLocation).get(transitEdge) + tp.getRouteCost().get(transitEdge).get(centralLocation);
        return preResp - transitResp;
    }

    private double computeMigrationCost(MarkovState preState, MarkovState transitState) {
        //check whether destination edge cloud has required game world
        //int requiredGWId = ((MarkovStateLocal) preState).getUser().getGameWorldId();
// TODO: add it back when edgeclouds logic is correct
//        if(edgeClouods.get(((MarkovStateLocal) transitState).getEdgeCloudLocation()).gameWorldList.containsKey(requiredGWId)){
//            return 0;
//        }

        Node preEdgeLocation = ((MarkovStateLocal) preState).getEdgeCloudLocation();
        Node transitEdgeLocation = ((MarkovStateLocal) transitState).getEdgeCloudLocation();
        double cost = tp.getRouteCost().get(preEdgeLocation).get(transitEdgeLocation);
        //return cost;

        if (cost == 0) {
            return 0;
        } else {
            return 0 + cost;
        }

    }


    private double computeDownTime(MarkovState preState) {
        return 0.0;
    }

    private double computeBW(MarkovState preState) {
        return 0.0;
    }

    private double computeMigrateBW(MarkovState preState) {
        return 0.0;
    }

}
