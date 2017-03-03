package edgecloud.mdp;

import topology.Topology;

/**
 * Created by Wuyang on 11/30/16.
 */
public abstract class MarkovCost {
    protected double a, b, c, d, e;
    protected Topology tp;

    public abstract double calculateReward(MarkovState preState, MarkovAction action, MarkovState transitState);
}
