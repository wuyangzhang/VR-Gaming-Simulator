package edgecloud;

import mdp.*;
import topology.Node;
import topology.Topology;
import user.User;

import java.util.*;

/**
 * Created by Wuyang on 12/2/16.
 */
public class CentralController {

    private Map<Node, EdgeCloud> edgeClouds = new HashMap<>();
    private Topology topology;
    private Collection<Node> edgeCloudLocations = new LinkedList<>();
    private Collection<Node> userLocations = new LinkedList<>();
    private LinkedList<User> users = new LinkedList<>();
    private HashMap<User, Node> globalOptimal = new HashMap<>();
    public HashMap<User, MarkovInterface> interfaceMap = new HashMap<>();

    private double gamma;

    public enum Request{
        INITSERVICE,
        GETOPTIMALSERVER,
        REPORTSTATE,
        GETGLOBALOPTIMAL,
    }

    public CentralController(Topology topology){
        this.topology = topology;
        this.userLocations = topology.getAllNodes();
    }

    public void addEdgeCloud(EdgeCloud edgeCloud){
        this.edgeClouds.put(edgeCloud.getLocation(), edgeCloud);
        this.edgeCloudLocations.add(edgeCloud.getLocation());
    }

    //public void getEdgeInfo(String edgeName, String serviceName, EdgeCloud.EdgeInfo edgeInfo){}
    public Node getRequest(User user, Request request){
        switch (request){
            case INITSERVICE:
                return this.request_initConnect(user);
            case GETOPTIMALSERVER:
                return this.request_getOptimalEdgeServer(user);
            case REPORTSTATE:
                this.receiveUserReport(user);
                return null;
            case GETGLOBALOPTIMAL:
                return this.getGlobalOptimalEdgeServer(user);
            default:
                throw new IllegalStateException("Unrecognized request");
        }
    }

    private Node request_initConnect(User user){
        MarkovCostLocal markovCost = new MarkovCostLocal.Builder(this.topology).set_a(1).set_b(0.5).set_c(1).set_d(1).set_e(1).build();
        //init markov interface that comprises all actions & states.
        MarkovInterface markovInterface = new MarkovInterfaceLocal(user, edgeCloudLocations, topology);
        //start to calculate MDP.
        new MarkovProcess.Builder(markovInterface, markovCost).gamma(this.gamma).epsilon(1e-2).build();
        //return the optimal edge server.
        this.interfaceMap.put(user,markovInterface);

        return computeNearestEdgeServer(user.getCurrentLocation());
    }

    private Node computeNearestEdgeServer(Node userNode){

        double nearestDistance = Double.MAX_VALUE;
        Node nearestEdgeServer = null;
        for(EdgeCloud edgeCloud : this.edgeClouds.values()){
            double distance = topology.getRouteCost().get(userNode).get(edgeCloud.getLocation());
            if( distance < nearestDistance){
                nearestDistance = distance;
                nearestEdgeServer = edgeCloud.getLocation();
            }

        }
        return nearestEdgeServer;
    }

    private Node request_getOptimalEdgeServer(User user){

        return computeMDPOptimalEdgeServer(user);
    }

    private void receiveUserReport(User user){
        this.users.add(user);
    }

    private Node computeMDPOptimalEdgeServer(User user){
        MarkovState userState = new MarkovStateLocal(user, user.getCurrentLocation(), user.getEdgeCloudLocation(), user.getCentralCloudLocation());
        return ((MarkovActionLocal)this.interfaceMap.get(user).findOptimalAction(userState)).getNewServerLocation();
    }


    public void computeGlobalOptimalEdgeServer(){
        MarkovCost markovCost = new MarkovCostGlobal(topology, 1, 1, 1, 1, 1);
        MarkovInterfaceGlobal.EdgeCloudFilter filter = new MarkovInterfaceGlobal.EdgeCloudFilter() {
            @Override
            public boolean IsFit(User user, Node edgeCandidate) {
                if(topology.getRouteCost().get(user.getCurrentLocation()).get(edgeCandidate) > 4){
                    return false;
                }else{
                    return true;
                }
            }
        };
        MarkovInterfaceGlobal markovInterface = new MarkovInterfaceGlobal(users, edgeCloudLocations, filter);
        System.out.println("Prepare Interface done!");
        //MarkovProcess mdp = new MarkovProcess(markovInterface, markovCost, this.gamma, 1e-2);
        //mdp.queryState = markovInterface.getCurrentGlobalState();
        //mdp.valueIteration();
        System.out.println("MDP calculation done!");
        /*
        MarkovActionGlobal action = (MarkovActionGlobal)mdp.getOptimalAction(markovInterface.getCurrentGlobalState());
        for(Map.Entry<User, MarkovActionLocal> entry : action.getLocalActions().entrySet()){
            globalOptimal.put(entry.getKey(), entry.getValue().getNewServerLocation());
        }
        */
        users.clear();
    }

    public Node getGlobalOptimalEdgeServer(User user){
        return globalOptimal.get(user);
    }

    public void setGamma(double gamma){
        this.gamma = gamma;
    }
}
