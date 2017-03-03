/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cost;

/**
 *
 * @author ubuntu
 */
public class MetricBandwidth extends Metric {

    private final double mbps;

    public MetricBandwidth(double mbps) {
        this.mbps = mbps;
    }

    public double getMbps() {
        return mbps;
    }

    @Override
    public String toString() {
        return String.format("%fMbps", mbps);
    }

    @Override
    public double toDouble(){
        return mbps;
    }
    
}
