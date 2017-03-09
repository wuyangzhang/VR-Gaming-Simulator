/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgecloud.mdp;

import user.User;

import java.util.*;

/**
 * @author ubuntu
 */
public class MarkovProcess {

    private final Collection<MarkovState> allPossibleStates;
    private final Collection<MarkovAction> allPossibleActions;
    private final MarkovCost costFunction;
    private final double gamma;
    private final double threshold;
    private Map<MarkovState, Double> utilityMap = new HashMap<>();

    public MarkovState queryState = null;

    private final class StateParameter{
        double pr;
        double reward;
        StateParameter(double pr, double reward){
            this.pr = pr;
            this.reward = reward;
        }
    }

    private Map<MarkovState, Map<MarkovAction,Map<MarkovState, StateParameter>>> stateParaMap = new HashMap<>();

    public static class Builder{
        private Collection<MarkovState> allPossibleStates;
        private Collection<MarkovAction> allPossibleActions;
        private MarkovCost costFunction;
        private double gamma;
        private double epsilon;

        public Builder(MarkovInterface markovInterface, MarkovCost costFunction){
            this.allPossibleStates = markovInterface.allPossibleStates;
            this.allPossibleActions = markovInterface.allPossibleActions;
            this.costFunction = costFunction;
        }

        public Builder gamma(double gamma){
            this.gamma = gamma;
            return this;
        }

        public Builder epsilon(double epsilon){
            this.epsilon = epsilon;
            return this;
        }

        public MarkovProcess build(){
            return new MarkovProcess(this);
        }
    }

    public MarkovProcess(Builder builder){
        this.allPossibleStates = builder.allPossibleStates;
        this.allPossibleActions = builder.allPossibleActions;
        this.costFunction = builder.costFunction;

        //init the utility for all states
        for (MarkovState state : this.allPossibleStates) {
            this.utilityMap.put(state, 0.0);
        }

        this.gamma = builder.gamma;
        threshold = gamma == 1 ? builder.epsilon : (builder.epsilon * (1 - gamma) / gamma);


        this.init();
        this.valueIteration();
    }


    /*
        avoid the duplicated computation of finding transit states & calculating reward
     */
    private void init(){
        for (MarkovState state : allPossibleStates) {
            Map<MarkovAction, Map<MarkovState, StateParameter>> s2 = new HashMap<>();
            for (MarkovAction action : allPossibleActions) {
                Map<MarkovState,Double> transitStates = action.getActionResults(state);
                Map<MarkovState, StateParameter> s3 = new HashMap<>();
                for (Map.Entry<MarkovState, Double> entry : transitStates.entrySet()) {
                    MarkovState reState = entry.getKey();
                    double pr = entry.getValue();
                    double reward = costFunction.calculateReward(state, action, reState);
                    StateParameter sp = new StateParameter(pr,reward);
                    s3.put(reState, sp);
                }
                s2.put(action, s3);
            }
            this.stateParaMap.put(state,s2);
        }
    }

    private void valueIteration(){
        //@para max utility difference of state between two continuous loops
        int round = 0;
        double maxDiff;
        do{
            round++;
            maxDiff = Double.NEGATIVE_INFINITY;
            //loop all states to calculate their optimal actions which take the max utility
            for(MarkovState state: allPossibleStates){
                //@para maxUtility, currently max utility of a state based on all possible actions
                //@para currUtility, currently recorded utility of a state
                double maxUtility = Double.NEGATIVE_INFINITY;
                double currUtility = utilityMap.get(state);
                //loop all actions to find the optimal action that take the max utility for this state

                //try this version in the global optimization

                /*
                HashMap<MarkovAction, Double>maxU = new HashMap<>();
                allPossibleActions.parallelStream().forEach((action) -> {
                    double nextUtil = 0;

                    for(Map.Entry<MarkovState, StateParameter> entry : this.stateParaMap.get(state).get(action).entrySet()){
                        MarkovState reState = entry.getKey();
                        StateParameter sp = entry.getValue();
                        double pr = sp.pr;
                        double reward = sp.reward;
                        nextUtil += pr * (reward + gamma * utilityMap.get(reState));
                    }

                    maxU.put(action, nextUtil);

                });
                for(Map.Entry<MarkovAction, Double> entry: maxU.entrySet()){
                    if(entry.getValue() > maxUtility){
                        maxUtility = entry.getValue();
                        state.setTempUtility(maxUtility);
                        state.setOptimalAction(entry.getKey());
                    }
                }
                */

                for(MarkovAction action : allPossibleActions){
                    double nextUtil = 0;
                    //loop all finally transit states of this state based on this action
                    for(Map.Entry<MarkovState, StateParameter> entry : this.stateParaMap.get(state).get(action).entrySet()){
                        MarkovState reState = entry.getKey();
                        StateParameter sp = entry.getValue();
                        double pr = sp.pr;
                        double reward = sp.reward;
                        nextUtil += pr * (reward + gamma * utilityMap.get(reState));
                    }

                    if(nextUtil > maxUtility){
                        maxUtility = nextUtil;
                        state.setTempUtility(nextUtil);
                        state.setOptimalAction(action);
                    }
                }

                double currDiff = Math.abs(maxUtility - currUtility);
                if(currDiff > maxDiff){
                    maxDiff = currDiff;
                }
            }//finish one round state value iteration
            //update the utility computed in this round
            for(MarkovState state : allPossibleStates){
                utilityMap.put(state, state.getTempUtility());
            }
        }while(maxDiff > threshold);
        //System.out.println(String.format("total round%d", round));
    }
    /*
    public MarkovAction getOptimalAction(MarkovState state) {
        for(MarkovState state1 : allPossibleStates) {
            if (state1.equals(state)) {
                return state1.getOptimalAction();
            }
        }
        throw new IllegalArgumentException("Invalid State");
    }
    */
    /*
    public void setUtility(MarkovState state, double utility){
        state.utility = utility;
    }

    public void setAction(MarkovState state, MarkovAction action, double tempUtility){
        state.setOptimalAction(action);
        state.tempUtility = tempUtility;
    }
    */
    /*
    public static class Decision {

        private MarkovAction action = null;

        private double utility = 0.0;
        private double tempUtility = Double.NEGATIVE_INFINITY;

        public void setUtility(double utility) {
            this.utility = utility;
        }

        public MarkovAction getAction() {
            return action;
        }

        public void setAction(MarkovAction action, double tempUtility) {
            this.action = action;
            this.tempUtility = tempUtility;
        }

        public double getUtility() {
            return utility;
        }

        public void copyTemp() { //System.out.printf(">>>%f,%f%n",tempUtility, utility);
            setUtility(tempUtility);
        }
    }
    */
    /*
    public static class TransitStatePr {

        private MarkovState state;
        private Double pr;

        public TransitStatePr(MarkovState state, Double pr) {
            this.state = state;
            this.pr = pr;
        }

        public Double getPr() {
            return pr;
        }

        public MarkovState getState() {
            return state;
        }
    }
    */
    /*
    public void calculateTransitPr() {
        //Initiate user mobility
        for (MarkovState state : allPossibleStates) {
            for (MarkovAction action : allPossibleActions) {
                MarkovInterface.StateAction stateAction = new MarkovInterface.StateAction(state, action);
                Collection<TransitStatePr> transitPr = new LinkedList<>();
                this.stateTransitPr.put(stateAction, transitPr);

                Map<MarkovState, Double> result = action.getActionResults(state);
                for (Map.Entry<MarkovState, Double> entry : result.entrySet()) {
                    MarkovState reState = entry.getKey();
                    Double pr = entry.getValue();
                    transitPr.add(new TransitStatePr(reState, pr));
                }
            }
        }
    }
    */
    /*
    public void valueIteration() {
        calculateTransitPr();
        double maxDiff;
        do {
            maxDiff = Double.NEGATIVE_INFINITY;
            for (MarkovState state : allPossibleStates) {
                double maxUtility = Double.NEGATIVE_INFINITY;
                double currUtility = state.utility;
                //double currUtility = this.stateDecision.get(state).getUtility();
                for (MarkovAction action : allPossibleActions) {
                    double nextUtil = 0;
                    for (TransitStatePr transitPr : this.stateTransitPr.get(new MarkovInterface.StateAction(state, action))) {
                        double rewardVal = costFunction.calculateReward(state, action, transitPr.getState());

                        nextUtil += transitPr.getPr() * (rewardVal + gamma * this.stateDecision.get(transitPr.getState()).getUtility());
                    }

                    if (nextUtil > maxUtility) {
                        maxUtility = nextUtil;
                        this.stateDecision.get(state).setAction(action, maxUtility);
                    }

                }
                double currentDiff = Math.abs(maxUtility - currUtility);
                if (currentDiff > maxDiff) {
                    maxDiff = currentDiff;
                }
            }//finish one round
            for (MarkovState state : allPossibleStates) {
                //this.stateDecision.get(state).copyTemp();
                state.utility = state.tempUtility;
            }

        } while (maxDiff > threshold);

    }
    */

}
