package edgecloud;

import topology.Node;
import topology.Topology;

/**
 * Created by Wuyang on 12/2/16.
 */
public class CentralCloud {
    private final int id;
    private final Node location;

    private CentralController centralController;

    public CentralController getCentralController() {
        return centralController;
    }

    public Node getLocation() {
        return location;
    }

    public CentralCloud(int id, Node location, Topology tp){
        this.id = id;
        this.location = location;
        this.centralController = new CentralController(tp);
    }

}
