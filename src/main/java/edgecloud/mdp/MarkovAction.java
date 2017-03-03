/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgecloud.mdp;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author ubuntu
 */
public abstract class MarkovAction {
    /**
     * Gets the result states and the probability of reaching each state
     * 
     * @param current The starting state
     * @return the result states and the corresponding probabilities
     */
    public abstract Map<MarkovState, Double> getActionResults(MarkovState current);


}
