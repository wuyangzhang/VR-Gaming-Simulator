/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mdp;

/**
 *
 * @author ubuntu
 */
public abstract class MarkovState {

    private double tempUtility = 0;// = Double.NEGATIVE_INFINITY;

    public double getTempUtility(){
        return tempUtility;
    }

    public void setTempUtility(double temp){
        tempUtility = temp;
    }
    private MarkovAction optimalAction;

    public void setOptimalAction(MarkovAction action){
        this.optimalAction = action;
    }

    public MarkovAction getOptimalAction(){
        return this.optimalAction;
    }

}
