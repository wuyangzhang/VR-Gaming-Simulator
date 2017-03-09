package edgecloud.mdp;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Wuyang on 12/1/16.
 */
public abstract class  MarkovInterface {
    protected ArrayList<MarkovState> allPossibleStates = new ArrayList<>();
    protected ArrayList<MarkovAction> allPossibleActions = new ArrayList<>();


    public MarkovAction findOptimalAction(MarkovState queryState){
        for(MarkovState state : allPossibleStates){
            if(state.equals(queryState)){
                return state.getOptimalAction();
            }
        }
        throw new IllegalArgumentException("cannot find this state!");
    }

    public void printUtility(){
        for(MarkovState state : allPossibleStates){
            System.out.println(String.format("state %s, utility %f%n", state, state.getTempUtility()));
        }
    }
}
