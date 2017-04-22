package mdp;

import edgecloud.EdgeCloud;
import topology.Node;
import topology.Topology;

import java.util.Map;

/**
 * Created by Wuyang on 11/30/16.
 */
public abstract class MarkovCost {
     protected double a, b, c, d, e;
     protected Topology tp;
     protected Map<Node, EdgeCloud> edgeCloudMap;
     abstract double calculateReward(MarkovState preState, MarkovAction action, MarkovState transitState);


}
