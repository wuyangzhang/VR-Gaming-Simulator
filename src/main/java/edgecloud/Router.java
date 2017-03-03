package edgecloud;

import topology.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wuyang on 12/2/16.
 */
public class Router {
    private CentralCloud centralCloud;
    private Map<Node, EdgeCloud> edgeClouds = new HashMap<>();

    public Router(CentralCloud centralCloud){
        this.centralCloud = centralCloud;
    }

    public void addEdgeCloud(EdgeCloud edgeCloud){
        this.edgeClouds.put(edgeCloud.getLocation(), edgeCloud);
    }

    public EdgeCloud getEdgeCloud(Node edgeCloudLocation){
        return edgeClouds.get(edgeCloudLocation);
    }

    public CentralCloud getCentralCloud(){
        return this.centralCloud;
    }
}
