/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import edgecloud.EdgeCloud;
import edgecloud.Router;
import topology.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ubuntu
 */
public class User {

    private final int name;
    private final Trace trace;
    private Node currentLocation;
    private Node edgeCloudLocation;
    private Node centralCloudLocation;
    private Node reserveEdgeCloud;
    private Router router;

    //private int requestId;
    private int gameWorldId;

    public enum Request{
        CONNECT,
        DISCONNECT,
        GETSERVICE,
        INITSERVICE,
        GETOPTIMALSERVER,
        REPORTSTATE,
        GETGLOBALOPTIMAL,
        RESERVE,
        RESERVECANCEL

    }

    public User(int name, Trace trace, Router router) {
        this.name = name;
        this.trace = trace;
        this.router = router;
        this.centralCloudLocation = router.getCentralCloud().getLocation();
        this.currentLocation = trace.getCurrent();
    }

    public int getName() {
        return name;
    }

    public Trace getTrace() {
        return trace;
    }

    public Node getCurrentLocation() {
        return currentLocation;
    }

    public void setEdgeCloudLocation(Node edgeCloudLocation) {
        this.edgeCloudLocation = edgeCloudLocation;
    }

    public Node getEdgeCloudLocation() {
        return edgeCloudLocation;
    }

    public Node getCentralCloudLocation() {
        return centralCloudLocation;
    }

    public void setCurrentLocation(double x, double y) {
        this.currentLocation = new Node(Node.get2DName(x, y));
    }

    public void setGameWorldId(int gameWorldId) {
        this.gameWorldId = gameWorldId;
    }

    public int getGameWorldId() {
        return gameWorldId;
    }

    public Node getReserveEdgeCloud() {
        return reserveEdgeCloud;
    }
    public void setReserveEdgeCloud(Node reserveEdgeCloud){
        this.reserveEdgeCloud = reserveEdgeCloud;
    }

    @Override
    public String toString() {
        return String.format("{0}@{1}", name, trace.getCurrent());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.name);
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
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.name, other.name);
    }

    public Node moveToNextStep() {
        return this.currentLocation = this.trace.moveToNextStep();
    }

    public Object sendServiceRequest(Node edgeCloudLocation, Request request) {
        switch (request) {
            case CONNECT:
                this.request_connect(edgeCloudLocation);
                break;
            case DISCONNECT:
                this.request_disconnect(edgeCloudLocation);
                break;
            case GETSERVICE:
                this.request_service(edgeCloudLocation);
                break;
            case INITSERVICE:
                return this.request_initConnect();
            case GETOPTIMALSERVER:
                return this.request_optimalServer();
            case REPORTSTATE:
                this.reportState();
                break;
            case GETGLOBALOPTIMAL:
                return this.request_globalOptimalServer();
            case RESERVE: //update this client will reserve this edge cloud
                this.request_reserveEdgeReserve(edgeCloudLocation);
                break;
            case RESERVECANCEL: //update this client will cancel reserve this edge cloud
                this.request_reserveCEdgeReserve(edgeCloudLocation);
                break;
            default:
                throw new IllegalStateException("Unrecognized service request");
        }
        return null;
    }

    private Node request_initConnect() {
        edgecloud.CentralController.Request request = edgecloud.CentralController.Request.INITSERVICE;
        return this.router.getCentralCloud().getCentralController().getRequest(this, request);
    }

    private void request_connect(Node edgeCloudLocation) {
        edgecloud.EdgeCloud.Request request = edgecloud.EdgeCloud.Request.CONNECT;
        this.router.getEdgeCloud(edgeCloudLocation).getRequest(this, request);
    }

    private void request_disconnect(Node edgeCloudLocation) {
        edgecloud.EdgeCloud.Request request = edgecloud.EdgeCloud.Request.DISCONNECT;
        this.router.getEdgeCloud(edgeCloudLocation).getRequest(this, request);
    }

    private void request_service(Node edgeCloudLocation) {
        edgecloud.EdgeCloud.Request request = edgecloud.EdgeCloud.Request.GETSERVICE;
        this.router.getEdgeCloud(edgeCloudLocation).getRequest(this, request);
    }

    private Node request_optimalServer() {
        edgecloud.CentralController.Request request = edgecloud.CentralController.Request.GETOPTIMALSERVER;
        return this.router.getCentralCloud().getCentralController().getRequest(this, request);
    }

    private void reportState() {
        edgecloud.CentralController.Request request = edgecloud.CentralController.Request.REPORTSTATE;
        this.router.getCentralCloud().getCentralController().getRequest(this, request);
    }

    private Node request_globalOptimalServer() {
        edgecloud.CentralController.Request request = edgecloud.CentralController.Request.GETGLOBALOPTIMAL;
        return this.router.getCentralCloud().getCentralController().getRequest(this, request);
    }

    private void request_reserveEdgeReserve(Node edgeCloudLocation){
        this.reserveEdgeCloud = edgeCloudLocation;
        edgecloud.EdgeCloud.Request request = edgecloud.EdgeCloud.Request.RESERVE;
        this.router.getEdgeCloud(edgeCloudLocation).getRequest(this, request);
    }

    private void request_reserveCEdgeReserve(Node edgeCloudLocation){
        edgecloud.EdgeCloud.Request request = edgecloud.EdgeCloud.Request.RESERVECANCEL;
        this.router.getEdgeCloud(edgeCloudLocation).getRequest(this, request);
    }

}
