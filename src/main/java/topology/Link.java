package topology;

import cost.Metric;
import cost.MetricBandwidth;
import java.util.Objects;

/**
 * Created by Wuyang on 11/29/16.
 */
public class Link {

    private final Node nFrom, nTo;
    private final Metric metric;

    public Metric getMetric() {
        return metric;
    }

    public Node getnTo() {

        return nTo;
    }

    public Node getnFrom() {

        return nFrom;
    }

    Link(Node nFrom, Node nTo, Metric metric) {
        this.nFrom = nFrom;
        this.nTo = nTo;
        this.metric = metric;
    }

    @Override
    public String toString() {
        return String.format("%s->%s(%s)", nFrom, nTo, metric);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Link) {
            return this.getnFrom().equals(((Link) obj).getnFrom()) && this.getnTo().equals(((Link) obj).getnTo());
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.nFrom);
        hash = 89 * hash + Objects.hashCode(this.nTo);
        hash = 89 * hash + Objects.hashCode(this.metric);
        return hash;
    }

    public static void main(String args[]) {
        Node n1 = new Node(Node.get2DName(1, 1));
        Node n2 = new Node(Node.get2DName(2, 2));
        MetricBandwidth bw = new MetricBandwidth(10);
        Link link = new Link(n1, n2, bw);
        System.out.print(link);
    }
}
