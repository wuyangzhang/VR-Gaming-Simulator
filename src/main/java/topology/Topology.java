package topology;

import cost.Metric;
import cost.MetricBandwidth;
import cost.MetricBandwidthLatency;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wuyang on 11/29/16.
 */
public class Topology {

    private final HashMap<String, Node> allNodes = new HashMap<>();
    
    private HashMap<Node, HashMap<Node, Double>> routeCost = new HashMap<>();

    public HashMap<Node, HashMap<Node, Double>> getRouteCost(){
        return (HashMap<Node, HashMap<Node, Double>>) routeCost.clone();
    }

    public Topology() {
    }

    public Node addNode(String name) {
        Node ret = new Node(name);
        allNodes.put(name, ret);
        return ret;
    }

    public Node getNode(String name) {
        return this.allNodes.get(name);
    }

    public Collection<Node> getAllNodes() {
        return allNodes.values();
    }

    public Link addLink(Node nFrom, Node nTo, Metric metric) {
        return nFrom.addLink(nTo, metric);
    }

    public Link getLink(Node nFrom, Node nTo) {
        return nFrom.getLink(nTo);
    }

    //find the shortest path(only distance) on the basis of the current link metric
    private HashMap<Node, Double> calculateDijkstra(Node nFrom) {
        HashMap<Node, Double> distance = new HashMap<>();
        HashMap<Node, Boolean> visited = new HashMap<>();

        /*
            init all visited nodes to false
            init the distances to all nodes as MAX_VALUE
        */
        for(Node node : this.getAllNodes()){
            visited.put(node, false);
            distance.put(node, Double.MAX_VALUE);
        }
        distance.put(nFrom, 0.0); //reset the distance to itself as 0

        //calculate
       for(int i =0 ; i < this.getAllNodes().size(); i++){
            Node minNode = findNearestNode(distance, visited);
            visited.put(minNode, true);
            for(Node neighbor: minNode.getNeighborNodes()){
                double dis = distance.get(minNode) + (getLink(minNode,neighbor).getMetric()).toDouble();
                if(distance.get(neighbor) > dis){
                    distance.put(neighbor, dis);
                }
            }
        }
       
        this.routeCost.put(nFrom, distance);
        return distance;
    }

    private static Node findNearestNode(Map<Node, Double> distance, HashMap<Node, Boolean> visited){
        Double minDistance = Double.MAX_VALUE;
        Node minNode = null;
        for(Node node : distance.keySet()){
            if (!visited.get(node) && distance.get(node) < minDistance){
                minDistance = distance.get(node);
                minNode = node;
            }
        }
        return minNode;
    }

    /*
        calculate the shortest path between all nodes.
    */
    public HashMap<Node, HashMap<Node, Double>> calAllNodesDistance(){
        for(Node node : getAllNodes()){
            this.calculateDijkstra(node);
        }
        return this.routeCost;
    }

    public void printAllLinks(PrintStream ps) {
        int count = 0;
        for (Node from : getAllNodes()) {
            for (Link l : from.getAllLinks()) {
                ps.println(l);
                count++;
            }
        }
        ps.printf("Print all %d links%n", count);
    }

    public void printAllNodes(PrintStream ps) {
        //this.allNodes.values().forEach(ps::println);
        ps.print("print all " + this.allNodes.size() + " nodes\n");
    }

    public static Topology getMByNMatrix(int width, int height) {
        Topology tp = new Topology();
        Node[][] myNodes = new Node[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                myNodes[i][j] = tp.addNode(Node.get2DName(i, j));
            }
        }

        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height; j++) {
                Node nFrom = myNodes[i][j];
                Node nTo = myNodes[i + 1][j];
                tp.addLink(nFrom, nTo, new MetricBandwidthLatency(10, 1));
                tp.addLink(nTo, nFrom, new MetricBandwidthLatency(10, 1));
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height - 1; j++) {
                Node nFrom = myNodes[i][j];
                Node nTo = myNodes[i][j + 1];
                tp.addLink(nFrom, nTo, new MetricBandwidthLatency(10, 1));
                tp.addLink(nTo, nFrom, new MetricBandwidthLatency(10, 1));
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Node nFrom = myNodes[i][j];
                Node nTo = myNodes[i][j];
                tp.addLink(nFrom, nTo, new MetricBandwidthLatency(10, 1));
            }
        }

        return tp;
    }
    
    public static void main(String args[]) {
        //test topology & dijkstra algorithm
        Topology tp = new Topology();
        tp.addNode("A");
        tp.addNode("B");
        tp.addNode("C");
        tp.addNode("D");
        tp.addNode("E");
        tp.addNode("F");
        
        tp.addLink(tp.getNode("A"), tp.getNode("B"), new MetricBandwidth(6));
        tp.addLink(tp.getNode("B"), tp.getNode("A"), new MetricBandwidth(6));
        tp.addLink(tp.getNode("A"), tp.getNode("C"), new MetricBandwidth(3));
        tp.addLink(tp.getNode("C"), tp.getNode("A"), new MetricBandwidth(3));
        tp.addLink(tp.getNode("B"), tp.getNode("C"), new MetricBandwidth(2));
        tp.addLink(tp.getNode("C"), tp.getNode("B"), new MetricBandwidth(2));
        tp.addLink(tp.getNode("B"), tp.getNode("D"), new MetricBandwidth(5));
        tp.addLink(tp.getNode("D"), tp.getNode("B"), new MetricBandwidth(5));
        tp.addLink(tp.getNode("C"), tp.getNode("D"), new MetricBandwidth(3));
        tp.addLink(tp.getNode("D"), tp.getNode("C"), new MetricBandwidth(3));
        tp.addLink(tp.getNode("C"), tp.getNode("E"), new MetricBandwidth(4));
        tp.addLink(tp.getNode("E"), tp.getNode("C"), new MetricBandwidth(4));
        tp.addLink(tp.getNode("D"), tp.getNode("E"), new MetricBandwidth(2));
        tp.addLink(tp.getNode("E"), tp.getNode("D"), new MetricBandwidth(2));
        tp.addLink(tp.getNode("D"), tp.getNode("F"), new MetricBandwidth(3));
        tp.addLink(tp.getNode("F"), tp.getNode("D"), new MetricBandwidth(3));
        tp.addLink(tp.getNode("E"), tp.getNode("F"), new MetricBandwidth(5));
        tp.addLink(tp.getNode("F"), tp.getNode("E"), new MetricBandwidth(5));

        tp.calAllNodesDistance();
        System.out.println(tp.getRouteCost().get(tp.getNode("A")).get(tp.getNode("B")));
        System.out.println(tp.getRouteCost().get(tp.getNode("A")).get(tp.getNode("D")));
        System.out.println(tp.getRouteCost().get(tp.getNode("A")).get(tp.getNode("E")));
        System.out.println(tp.getRouteCost().get(tp.getNode("A")).get(tp.getNode("F")));
        System.out.println(tp.getRouteCost().get(tp.getNode("E")).get(tp.getNode("B")));

    }
}