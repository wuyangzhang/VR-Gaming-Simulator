package edgecloud;

import edgecloud.mdp.*;
import topology.Node;
import topology.Topology;
import user.User;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    private static long total = 0;
    private static int count = 0;
    private Node computeMDPOptimalEdgeServer(User user){

        MarkovCostLocal markovCost = new MarkovCostLocal(this.topology, 1, 0.5, 1, 1, 1);

        //init markov interface that comprises all actions & states.
        MarkovInterface markovInterface = new MarkovInterfaceLocal(user, edgeCloudLocations, topology);

        //start to calculate MDP.

        MarkovProcess mdp = new MarkovProcess(markovInterface, markovCost, this.gamma, 1e-2);
        //return the optimal edge server.
        Node n =  ((MarkovActionLocal)mdp.getOptimalAction(new MarkovStateLocal(user, user.getCurrentLocation(),user.getEdgeCloudLocation(), user.getCentralCloudLocation()))).getNewServerLocation();
        System.out.println("location " + user.getCurrentLocation() + " server " + user.getEdgeCloudLocation() + " optimal server" + n);
        return n;
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
        MarkovProcess mdp = new MarkovProcess(markovInterface, markovCost, this.gamma, 1e-2);
        mdp.queryState = markovInterface.getCurrentGlobalState();
        //mdp.valueIteration();
        System.out.println("MDP calculation done!");
        MarkovActionGlobal action = (MarkovActionGlobal)mdp.getOptimalAction(markovInterface.getCurrentGlobalState());
        for(Map.Entry<User, MarkovActionLocal> entry : action.getLocalActions().entrySet()){
            globalOptimal.put(entry.getKey(), entry.getValue().getNewServerLocation());
        }
        users.clear();
    }

    public Node getGlobalOptimalEdgeServer(User user){
        return globalOptimal.get(user);
    }

    public void setGamma(double gamma){
        this.gamma = gamma;
    }
}
