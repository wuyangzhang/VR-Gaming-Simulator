/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.util.*;

import topology.Link;
import topology.Node;
import topology.Topology;
import static topology.Topology.getMByNMatrix;

/**
 *
 * @author ubuntu
 */
public class RandomTrace extends Trace {

    private static final Random DEFAULT_RANDOM = new Random(0);

    private final Topology topology;
    private final Map<Node, Map<Link, Double>> transitionProbability;
    private Node current;

    public RandomTrace(Topology topology, Node current, Map<Node, Map<Link, Double>> transitionProbability) {
        this.topology = topology;
        this.current = current;
        this.transitionProbability = transitionProbability;
    }

    public Topology getTopology() {
        return topology;
    }

    public void setCurrent(Node node) {
        current = node;
    }

    @Override
    public Node getCurrent() {
        return current;
    }

    @Override
    public Map<Node, Map<Link, Double>> getTransitionProbability() {
        return transitionProbability;
    }

    @Override
    public Node moveToNextStep() {
        Map<Link, Double> probability = transitionProbability.get(current);
        double val = DEFAULT_RANDOM.nextDouble();
        for (Map.Entry<Link, Double> entry : probability.entrySet()) {
            Link key = entry.getKey();
            Double value = entry.getValue();
            val -= value;
            if (val < 0) {
                current = key.getnTo();
                break;
            }
        }
        return current;
    }

    public Map<Link, Double> getPossibleNextSteps() {
        return transitionProbability.get(current);
    }

    public static void setTransitionProbability(Topology topology, Map<Node, Map<Link, Double>> transitionProbability, String n1, String n2, Double d) {
        Node n = topology.getNode(n1);
        HashMap<Link, Double> tmp = (HashMap<Link, Double>) transitionProbability.get(n);
        if (tmp == null) {
            transitionProbability.put(n, tmp = new HashMap<>());
        }
        tmp.put(n.getLink(topology.getNode(n2)), d);

    }

    public static Map<Node, Map<Link, Double>> generateRingTrace(Topology tp, int x, int y) {
        Map<Node, Map<Link, Double>> transitionProbability = new HashMap<>();
        for (int i = 0; i < x - 1; i++) {
            setTransitionProbability(tp, transitionProbability, Node.get2DName(i, 0), Node.get2DName(i + 1, 0), 1.0);
        }
        for (int i = 0; i < y - 1; i++) {
            setTransitionProbability(tp, transitionProbability, Node.get2DName(x - 1, i), Node.get2DName(x - 1, i + 1), 1.0);
        }
        for (int i = x - 1; i > 0; i--) {
            setTransitionProbability(tp, transitionProbability, Node.get2DName(i, y - 1), Node.get2DName(i - 1, y - 1), 1.0);
        }
        for (int i = y - 1; i > 0; i--) {
            setTransitionProbability(tp, transitionProbability, Node.get2DName(0, i), Node.get2DName(0, i - 1), 1.0);
        }

        //random probability
        /*
        setTransitionProbability(tp, transitionProbability, Node.get2DName(1, 0), Node.get2DName(0, 0), 0.9);
        setTransitionProbability(tp, transitionProbability, Node.get2DName(1, 0), Node.get2DName(2, 0), 0.1);

        setTransitionProbability(tp, transitionProbability, Node.get2DName(x - 2, y - 1), Node.get2DName(x - 1, y - 1), 0.9);
        setTransitionProbability(tp, transitionProbability, Node.get2DName(x - 2, y - 1), Node.get2DName(x - 3, y - 1), 0.1);
        */
        return transitionProbability;
    }

    public Node generateRandomStartNode() {

        List<Node> keys = new ArrayList<Node>(this.transitionProbability.keySet());
        return keys.get(DEFAULT_RANDOM.nextInt(keys.size()));
    }

    public void generateRandomTrace(int totalStep) {
        for (int i = 0; i < totalStep; i++) {
            this.moveToNextStep();
        }
    }

    public static void main(String[] args) {
        final int n = 3;
        Topology tp = Topology.getMByNMatrix(n, n);
        RandomTrace rt = new RandomTrace(tp, tp.getNode(Node.get2DName(0, 0)), generateRingTrace(tp, n, n));

        for (int i = 0; i < 100; i++) {
            System.out.println(rt.moveToNextStep());
        }
    }

}
