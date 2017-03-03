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
public class Cost extends MarkovCost {

    @Override
    public double calculateReward(MarkovState preState, MarkovAction action, MarkovState transitState) {
        State pre = (State) preState;
        State transit = (State) transitState;
        Action act = (Action) action;

        if (pre.name == 0) {
            if (act.action == 0) {
                if (transit.name == 0) {
                    return 2.0;
                } else {
                    return -1.0;
                }
            } else {
                if (transit.name == 0) {
                    return 1.0;
                } else {
                    return 2.0;
                }
            }
        } else {
            if (act.action == 0) {
                if (transit.name == 0) {
                    return -2.0;
                } else {
                    return -1.0;
                }
            } else {
                if (transit.name == 0) {
                    return -3.0;
                } else {
                    return -1.0;
                }
            }
        }
    }

}
