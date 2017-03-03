/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgecloud.mdp;

import java.util.Objects;
import topology.Node;
import user.User;

/**
 *
 * @author ubuntu
 */
public final class MarkovStateLocal extends MarkovState{

    private final User user;
    private final Node userLocation;
    private final Node edgeCloudLocation;
    private final Node centralServerLocation;



    public MarkovStateLocal(User user, Node userLocation, Node edgeCloudLocation, Node centralServerLocation) {
        this.user = user;
        this.edgeCloudLocation = edgeCloudLocation;
        this.centralServerLocation = centralServerLocation;
        this.userLocation = userLocation;
    }

    public User getUser() {
        return user;
    }

    public Node getUserLocation() {
        return userLocation;
    }

    public Node getEdgeCloudLocation() {
        return edgeCloudLocation;
    }

    public Node getCentralServerLocation() {
        return centralServerLocation;
    }

    @Override
    public String toString(){
        return String.format("edge: %s, client: %s", this.edgeCloudLocation, this.getUserLocation());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.user);
        hash = 11 * hash + Objects.hashCode(this.userLocation);
        hash = 11 * hash + Objects.hashCode(this.edgeCloudLocation);
        hash = 11 * hash + Objects.hashCode(this.centralServerLocation);
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
        if (!(obj instanceof MarkovStateLocal)) {
            return false;
        }
        final MarkovStateLocal other = (MarkovStateLocal) obj;
        return (Objects.equals(this.user, other.user)
                && Objects.equals(this.userLocation, other.userLocation)
                && Objects.equals(this.edgeCloudLocation, other.edgeCloudLocation)
                && Objects.equals(this.centralServerLocation, other.centralServerLocation));
    }

    public static void main(String args[]){

    }

}
