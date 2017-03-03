/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgecloud;

import topology.Node;
import user.User;

import java.util.*;

/**
 * @author ubuntu
 */
public class EdgeCloud {
    private final Node location;
    private final CentralCloud centralCloud;
    private final Router router;
    public final HashMap<Integer, Integer> gameWorldList = new HashMap<>(); //game world Id + user count

    private int totalUser = 0;
    private int totalReserve = 0;
    private Collection<Double> computeTime = new ArrayList<>();
    private Collection<User> users = new LinkedList<>();
    private Collection<User> reserveUsers = new LinkedList<>();


    public EdgeCloud( Node location, CentralCloud centralCloud, Router router) {
        this.location = location;
        this.centralCloud = centralCloud;
        this.router = router;
        this.centralCloud.getCentralController().addEdgeCloud(this);
        this.router.addEdgeCloud(this);
    }


    public Node getLocation() {
        return location;
    }

    public boolean containGameWorld(int gameWorldId){
        return this.gameWorldList.containsKey(gameWorldId);
    }


    /*

    public void updateEdgeInfo_totalUser(String name, int totalUsere) {
        this.edgeInfo.get(name).totalUser = totalUsere;
    }

    public void updateEdgeInfo_resp(String name, double resp) {
        this.edgeInfo.get(name).computeTime.add(resp);
    }

    public void sendEdgeInfo(String serviceId) {
        centralCloud.getCentralController().getEdgeInfo(this.edgeId, serviceId, this.edgeInfo.get(serviceId));
    }
    */
    public enum Request{
        CONNECT,
        DISCONNECT,
        GETSERVICE,
        RESERVE,
        RESERVECANCEL

    }

    public void getRequest(User user, Request request) {
        switch (request) {
            case CONNECT:
                this.request_connect(user);
                break;
            case DISCONNECT:
                this.request_disconnect(user);
                break;
            case GETSERVICE:
                this.request_service(user);
                break;
            case RESERVE:
                this.request_reserve(user);
                break;
            case RESERVECANCEL:
                this.request_reserveC(user);
                break;
            default:
                break;
        }
    }

    /*
        connection events:
        add one to total user number
        add user to user list
        record game world storage
     */
    private void request_connect(User user) {
        this.totalUser++;

        if(!this.users.contains(user)) {
            this.users.add(user);
        }

        int gameId = user.getGameWorldId();
        if (gameWorldList.containsKey(gameId)) {
            int updateGameIdCount = gameWorldList.get(gameId) + 1;
            gameWorldList.put(gameId, updateGameIdCount);
        }else{
            gameWorldList.put(gameId, 1);
        }
    }

    /*
           connection event:
           remove one to total user number
           record game world storage
     */
    private void request_disconnect(User user) {
        this.totalUser--;

        if(this.users.contains(user)) {
            this.users.remove(user);
        }

        int gameId = user.getGameWorldId();
        int count = gameWorldList.get(gameId);
        if(count > 1){
            int updateGameIdCount = gameWorldList.get(gameId) - 1;
            gameWorldList.put(gameId, updateGameIdCount);
        }else if( count == 1){
            gameWorldList.remove(gameId);
        }
    }

    private void request_service(User user) {
        // TODO compute response time & add it into the computed time list
    }

    private void request_reserve(User user) {
        this.totalReserve++;

        if (!this.reserveUsers.contains(user)) {
            this.reserveUsers.add(user);
        }
    }

    private void request_reserveC(User user) {
        this.totalReserve--;

        if(this.reserveUsers.contains(user)){
            this.reserveUsers.remove(user);
        }
    }

    public static void main(String[] args) {
        // TODO code application logic here
    }

}
