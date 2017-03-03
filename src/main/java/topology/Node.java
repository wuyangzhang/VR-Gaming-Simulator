package topology;

import cost.Metric;
import cost.MetricBandwidth;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by Wuyang on 11/29/16.
 */
public class Node {

    private final String name;
    private HashMap<Node, Link> neighbors = new HashMap<Node, Link>();
    private LinkedList<Node> neighborNodes = new LinkedList<>();
    
    public Node(String nodeName) {
        this.name = nodeName;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /*
        Node equal function: if nodeA's name == nodeB's name
        default: nodes have unique names
    */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            return this.getName().equals(((Node) obj).getName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    public Link addLink(Node node, Metric metric) {
        Link link = new Link(this, node, metric);
        this.neighbors.put(node, link);
        this.neighborNodes.add(node);
        return link;
    }
    /*
        Return the link connected to another node
    */
    public Link getLink(Node node) {
        return this.neighbors.get(node);
    }
    
    public LinkedList<Node> getNeighborNodes(){
        return (LinkedList<Node>) this.neighborNodes.clone();
    }
    
    public Collection<Link> getAllLinks() {
        return neighbors.values();
    }

    public boolean linkToNode(Node node) {
        return this.neighbors.containsKey(node);
    }

    public void printAllLinks(PrintStream ps) {
        for (Link link : this.getAllLinks()) {
            ps.println(link);
        }
    }

    public static String get2DName(double x, double y) {
        return String.format("(%f,%f)", x, y);
    }

    public class Location2D{
        public double x, y;
        Location2D(String location){
            String values[] = location.split(",");
            String value1 = values[0].split("\\(")[1];
            String value2 = values[1].split("\\)")[0];
            x = Double.parseDouble(value1);
            y = Double.parseDouble(value2);
        }
    }

    public Location2D getLocation2D(){
        return new Location2D(this.name);
    }

    public static void main(String args[]) {
        Node nodeSet[] = new Node[10];
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 5; j++){
                nodeSet[i*5 + j] = new Node(get2DName(i,j));
            }
        }
        for(int i = 0; i < nodeSet.length - 1; i++){
            MetricBandwidth metric = new MetricBandwidth(5);
            nodeSet[i].addLink(nodeSet[i+1], metric);
        }
        
        for(int i = 0; i < nodeSet.length; i ++){
            nodeSet[i].printAllLinks(System.out);
        }
        
        //test LinkToNode()
        System.out.println(nodeSet[0].linkToNode(nodeSet[1]));
        System.out.println(nodeSet[0].linkToNode(nodeSet[2]));
        //test getLink();

    }

}
