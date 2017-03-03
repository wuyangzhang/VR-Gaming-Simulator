package edgecloud.mdp;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Wuyang on 12/1/16.
 */
public abstract class  MarkovInterface {
    protected ArrayList<MarkovState> allPossibleStates = new ArrayList<>();
    protected ArrayList<MarkovAction> allPossibleActions = new ArrayList<>();

}
