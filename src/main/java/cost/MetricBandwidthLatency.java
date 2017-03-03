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
public class MetricBandwidthLatency extends Metric{
    private final double bandwidth, latency;

    public double getBandwidth() {
        return bandwidth;
    }

    public double getLatency() {
        return latency;
    }

    public MetricBandwidthLatency(double bandwidth, double latency) {
        this.bandwidth = bandwidth;
        this.latency = latency;
    }

    @Override
    public double toDouble(){
        return this.latency;
    }
}
