/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.util.Map;
import topology.Link;
import topology.Node;

/**
 *
 * @author ubuntu
 */
public abstract class Trace {

    public abstract Node getCurrent();

    public abstract Node moveToNextStep();

    public abstract Map<Node, Map<Link, Double>> getTransitionProbability();
}
