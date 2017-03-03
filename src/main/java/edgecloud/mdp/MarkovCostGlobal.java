package edgecloud.mdp;

import topology.Node;
import topology.Topology;
import user.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wuyang on 12/7/16.
 */
public final class MarkovCostGlobal extends MarkovCost {

    public MarkovCostGlobal(Topology tp, double a, double b, double c, double d, double e) {
        this.tp = tp;
        this.a = a; // latency
        this.b = b; // migration cost
        this.c = c;
        this.d = d;
        this.e = e; // game sharing reward
    }

    @Override
    public double calculateReward(MarkovState preState, MarkovAction action, MarkovState transitState) {
        //double responseMinus = a * (computeResponseTime(preState) - computeResponseTime(transitState));
        //double migrationCost = b * computeMigrationCost(preState, transitState);
        double gwSharingPlus = e * ((countGameWorldSharing((MarkovStateGlobal) transitState)) - countGameWorldSharing((MarkovStateGlobal) preState));
        //return responseMinus - migrationCost + gwSharingPlus;
        return gwSharingPlus;

    }

    private HashMap<Integer, HashMap<Node, Integer>> calGameWorldSharing(MarkovStateGlobal state) {
        HashMap<Integer, HashMap<Node, Integer>> gwSharing = new HashMap<>();
        for (Map.Entry<User, MarkovStateLocal> entry : state.getLocalStates().entrySet()) {
            int gwId = (entry.getKey()).getGameWorldId();
            if (gwSharing.containsKey(gwId)) {
                Node edgeServer = entry.getValue().getEdgeCloudLocation();
                if ((gwSharing.get(gwId)).containsKey(edgeServer)) {
                    gwSharing.get(gwId).put(edgeServer, gwSharing.get(gwId).get(edgeServer) + 1);
                } else {
                    gwSharing.get(gwId).put(edgeServer, 0);
                }
            } else {
                HashMap<Node, Integer> shareCount = new HashMap<>();
                shareCount.put(entry.getValue().getEdgeCloudLocation(), 0);
                gwSharing.put(gwId, shareCount);
            }
        }
        return gwSharing;
    }

    private int countGameWorldSharing(MarkovStateGlobal state) {
        int count = 0;
        for (HashMap<Node, Integer> sharing : calGameWorldSharing(state).values()) {
            for (Integer i : sharing.values()) {
                count += i;
            }
        }
        return count;
    }

    private double computeResponseTime(MarkovState state) {
        MarkovStateGlobal globalState = (MarkovStateGlobal) state;
        double totalResp = 0;
        for (MarkovStateLocal stateLocal : globalState.getLocalStates().values()) {
            totalResp += 2 * tp.getRouteCost().get(stateLocal.getUserLocation()).get(stateLocal.getEdgeCloudLocation())
                    + tp.getRouteCost().get(stateLocal.getEdgeCloudLocation()).get(stateLocal.getCentralServerLocation());
        }

        return totalResp;
    }

    private double computeMigrationCost(MarkovState preState, MarkovState transitState) {
        MarkovStateGlobal preState1 = (MarkovStateGlobal) preState;
        MarkovStateGlobal transitState1 = (MarkovStateGlobal) transitState;
        double totalMigrationCost = 0;
        for (Map.Entry<User, MarkovStateLocal> stateLocalEntry : preState1.getLocalStates().entrySet()) {
            Node preEdgeLocation = stateLocalEntry.getValue().getEdgeCloudLocation();
            Node transitEdgeLocation = transitState1.getLocalStates().get(stateLocalEntry.getKey()).getEdgeCloudLocation();
            double cost = tp.getRouteCost().get(preEdgeLocation).get(transitEdgeLocation);
            if (cost != 0) {
                totalMigrationCost += 10 + cost;
            }
        }
        return totalMigrationCost;
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

    private double computeGameSharing(MarkovState preState) {
        return 0.0;
    }


}
