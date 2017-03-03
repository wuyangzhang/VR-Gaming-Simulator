/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgecloud.mdp;

/**
 *
 * @author Wuyang
 */
public class Interface extends MarkovInterface {

    public Interface() {
        allPossibleStates.add(new State(0));
        allPossibleStates.add(new State(1));
        allPossibleActions.add(new Action(0));
        allPossibleActions.add(new Action(1));
    }
    
}
