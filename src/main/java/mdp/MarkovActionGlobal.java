package mdp;

import user.User;

import java.util.*;

/**
 * Created by Wuyang on 12/7/16.
 */
public class MarkovActionGlobal extends MarkovAction {
    private final HashMap<User, MarkovActionLocal> localActions = new HashMap<>();

    public MarkovActionGlobal(Collection<User> users, Collection<MarkovActionLocal> localActions) {
        if (users.size() != localActions.size()) {
            throw new IllegalArgumentException("the size of users and serverNodes is not equal!");
        }
        Iterator<User> iUser = users.iterator();
        Iterator<MarkovActionLocal> iAction = localActions.iterator();
        for (; iUser.hasNext(); ) {
            this.localActions.put(iUser.next(), iAction.next());
        }
    }

    public HashMap<User, MarkovActionLocal> getLocalActions() {
        return this.localActions;
    }

    @Override
    public Map<MarkovState, Double> getActionResults(MarkovState current) {
        MarkovStateGlobal state = (MarkovStateGlobal) current;
        HashMap<MarkovState, Double> globalStateTransit = new HashMap<>();

        ArrayList<Map<MarkovState, Double>> currentTransitStates = new ArrayList<>();//storing transit local state list
        for (MarkovStateLocal localState : state.getLocalStates().values()) {
            currentTransitStates.add(localActions.get(localState.getUser()).getActionResults(localState));
        }

        recursive(currentTransitStates, 0, new HashMap<MarkovState, Double>(), globalStateTransit);
        return globalStateTransit;
    }

    private void recursive(ArrayList<Map<MarkovState, Double>> currentTransitStates, int userIndex,
                           HashMap<MarkovState, Double> selectedStates,
                           HashMap<MarkovState, Double> ret) {
        Map<MarkovState, Double> transitStates = currentTransitStates.get(userIndex);

        if (userIndex == currentTransitStates.size() - 1) {
            ArrayList<MarkovStateLocal> selectedStatesTemp = new ArrayList<>();
            double probability = 1;
            //add previous states && probability
            for (Map.Entry<MarkovState, Double> selectedPair : selectedStates.entrySet()) {
                selectedStatesTemp.add((MarkovStateLocal) selectedPair.getKey());
                probability *= selectedPair.getValue();
            }

            //
            for (Map.Entry<MarkovState, Double> entry : transitStates.entrySet()) {
                selectedStatesTemp.add((MarkovStateLocal) entry.getKey());
                ret.put(new MarkovStateGlobal(selectedStatesTemp), probability * entry.getValue());
                selectedStatesTemp.remove(selectedStatesTemp.size() - 1);
            }
        } else {
            for (Map.Entry<MarkovState, Double> entry : transitStates.entrySet()) {
                selectedStates.put(entry.getKey(), entry.getValue());
                recursive(currentTransitStates, userIndex + 1, selectedStates, ret);
                selectedStates.remove(entry.getKey());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<User, MarkovActionLocal> entry : localActions.entrySet()) {
            sb.append(String.format("user: %s -> connects to %s", entry.getKey().getName(), entry.getValue()));
        }
        return sb.toString();
    }


    @Override
    public int hashCode() {
        int hash = 7;
        for (Map.Entry<User, MarkovActionLocal> entry : localActions.entrySet()) {
            hash = 11 * hash + Objects.hashCode(entry.getKey()) + Objects.hashCode(entry.getValue());
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MarkovActionGlobal)) {
            return false;
        }
        final MarkovActionGlobal other = (MarkovActionGlobal) obj;

        if(other.localActions.size() != this.localActions.size()){
            return false;
        }

        Object[] myActions = this.localActions.values().toArray();
        Object[] otherActions = other.localActions.values().toArray();
        for(int i = 0; i < myActions.length; i++){
            MarkovActionLocal myaction = (MarkovActionLocal)myActions[i];
            MarkovActionLocal otheraction = (MarkovActionLocal)otherActions[i];
            if(!myaction.equals(otheraction)){
                return false;
            }
        }
        return true;
    }

    public static void main(String args[]) {
        /*
        final int x = 7;
        final int y = (x - 1) / 2;
        Topology tp = Topology.getMByNMatrix(x, x);
        tp.calAllNodesDistance();
        //init central controller & edge clouds & router
        CentralCloud centralCloud = new CentralCloud("101", tp.getNode(Node.get2DName(y, y)), tp);
        Router router = new Router(centralCloud);

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < x; j++) {
                if (i == 0 || i == x - 1 || (j == 0 && i != 0 && i != x - 1) || (j == x - 1 && i != 0 && i != x - 1)) {
                    new EdgeCloud(String.format("%d,%d", i, j), tp.getNode(Node.get2DName(i, j)), centralCloud, router);
                }
            }
        }

        RandomTrace rt = new RandomTrace(tp, tp.getNode(Node.get2DName(1, 0)), RandomTrace.generateRingTrace(tp, x, x));
        User user1 = new User("10001", rt, router);
        User user2 = new User("10002", rt, router);
        User user3 = new User("10003", rt, router);
        user1.setEdgeCloudLocation(tp.getNode(Node.get2DName(2, 0)));
        user2.setEdgeCloudLocation(tp.getNode(Node.get2DName(3, 0)));
        user3.setEdgeCloudLocation(tp.getNode(Node.get2DName(4, 0)));

        MarkovStateLocal stateLocal1 = new MarkovStateLocal(user1, user1.getCurrentLocation(), user1.getEdgeCloudLocation(), user1.getCentralCloudLocation());
        MarkovStateLocal stateLocal2 = new MarkovStateLocal(user2, user2.getCurrentLocation(), user2.getEdgeCloudLocation(), user2.getCentralCloudLocation());
        MarkovStateLocal stateLocal3 = new MarkovStateLocal(user3, user3.getCurrentLocation(), user3.getEdgeCloudLocation(), user3.getCentralCloudLocation());
        MarkovActionLocal actionLocal1 = new MarkovActionLocal(tp.getNode(Node.get2DName(1, 1)));
        MarkovActionLocal actionLocal2 = new MarkovActionLocal(tp.getNode(Node.get2DName(2, 1)));
        MarkovActionLocal actionLocal3 = new MarkovActionLocal(tp.getNode(Node.get2DName(4, 1)));
        ArrayList<MarkovStateLocal> localStates = new ArrayList<>();
        ArrayList<MarkovActionLocal> localActions = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        localStates.add(stateLocal1);
        localStates.add(stateLocal2);
        localStates.add(stateLocal3);
        localActions.add(actionLocal1);
        localActions.add(actionLocal2);
        localActions.add(actionLocal3);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        MarkovStateGlobal globalState = new MarkovStateGlobal(localStates);
        MarkovActionGlobal globalAction = new MarkovActionGlobal(users, localActions);
        Map<MarkovState, Double> transitGlobalState = globalAction.getActionResults(globalState);

        for (Map.Entry<MarkovState, Double> entry : transitGlobalState.entrySet()) {
            System.out.println(entry.getKey() + " probability: " + entry.getValue());
        }
        */
    }

}
