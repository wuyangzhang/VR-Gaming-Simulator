package mdp;

import edgecloud.EdgeCloud;
import topology.Node;
import topology.Topology;

import java.util.Map;

/**
 * Created by Wuyang on 12/1/16.
 */
public final class MarkovCostLocal extends MarkovCost {

    private final int sharingValue = 100;


    public MarkovCostLocal(Builder builder){
        this.a = builder.a;
        this.b = builder.b;
        this.c = builder.c;
        this.d = builder.d;
        this.e = builder.e;
        this.edgeCloudMap = builder.edgeCloudMap;
        this.tp = builder.tp;
    }

    public static class Builder{
        private double a;
        private double b;
        private double c;
        private double d;
        private double e;
        private Map<Node, EdgeCloud> edgeCloudMap;
        private Topology tp;

        public Builder(Topology tp){
            this.tp = tp;
        }

        public Builder set_a(double a){
            this.a = a;
            return this;
        }

        public Builder set_b(double b){
            this.b = b;
            return this;
        }

        public Builder set_c(double c){
            this.c = c;
            return this;
        }

        public Builder set_d(double d){
            this.d = d;
            return this;
        }

        public Builder set_e(double e){
            this.e = e;
            return this;
        }

        public Builder set_e(Map<Node, EdgeCloud> edgeCloudMap){
            this.edgeCloudMap = edgeCloudMap;
            return this;
        }

        public MarkovCostLocal build(){
            return new MarkovCostLocal(this);
        }


    }


    @Override
    public double calculateReward(MarkovState preState, MarkovAction action, MarkovState transitState) {
        //double responsePlus = a * (computeResponseTime(preState) - computeResponseTime(transitState));
        double responsePlus =  computeResponseTime(preState, transitState);
        double migrationCost = computeMigrationCost(preState, transitState);
        //return 1000*responsePlus -  migrationCost;
        return responsePlus;
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
        //double preResp = 10*tp.getRouteCost().get(userLocation).get(preEdge) + tp.getRouteCost().get(preEdge).get(centralLocation);
        double transitResp =  tp.getRouteCost().get(userLocation).get(transitEdge);// + tp.getRouteCost().get(transitEdge).get(centralLocation);
        //double preResp = tp.getRouteCost().get(userLocation).get(preEdge);
        //double transitResp = tp.getRouteCost().get(userLocation).get(transitEdge);
        return transitResp;
    }

    private double computeMigrationCost(MarkovState preState, MarkovState transitState) {

        double GWSharingPlus = 0;
        int currGWId = ((MarkovStateLocal) preState).getUser().getGameWorldId();
        /*
        if(edgeCloudMap.get(((MarkovStateLocal) transitState).getEdgeCloudLocation()).gameWorldList.containsKey(currGWId)){
            GWSharingPlus = sharingValue * edgeCloudMap.get(((MarkovStateLocal) transitState).getEdgeCloudLocation()).gameWorldList.get(currGWId);
        }
        */
        Node preEdgeLocation = ((MarkovStateLocal) preState).getEdgeCloudLocation();
        Node transitEdgeLocation = ((MarkovStateLocal) transitState).getEdgeCloudLocation();
        double cost = tp.getRouteCost().get(preEdgeLocation).get(transitEdgeLocation);
        //return cost - GWSharingPlus;
        //double cost = tp.getRouteCost().get(preEdgeLocation).get(transitEdgeLocation);
        return cost;
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
