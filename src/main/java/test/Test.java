package test;

import edgecloud.CentralCloud;
import edgecloud.EdgeCloud;
import edgecloud.Router;
import topology.Node;
import topology.Topology;
import user.RandomTrace;
import user.User;

import java.util.ArrayList;

/**
 * Created by Wuyang on 12/1/16.
 */

public class Test {
    public static void main(String args[]) {
        /*
            init topology
         */
        final int x = 5;
        final int y = (x - 1) / 2;
        Topology tp = Topology.getMByNMatrix(x, x);
        tp.calAllNodesDistance();

        /*
            init central controller & edge clouds & router
         */
        CentralCloud centralCloud = new CentralCloud(101, tp.getNode(Node.get2DName(y, y)), tp);
        //set gamma value
        centralCloud.getCentralController().setGamma(0.5);
        Router router = new Router(centralCloud);

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < x; j++) {
                if (i == 0 || i == x - 1 || (j == 0 && i != 0 && i != x - 1) || (j == x - 1 && i != 0 && i != x - 1)) {
                    new EdgeCloud(tp.getNode(Node.get2DName(i, j)), centralCloud, router);
                }
            }
        }

        /*
            init client
            add topology, start position, trace.
         */
        RandomTrace rt = new RandomTrace(tp, tp.getNode(Node.get2DName(0, 0)), RandomTrace.generateRingTrace(tp, x, x));

        ArrayList<User> userList = new ArrayList<>();

        User user = new User.Builder(10001, rt, router).
                gameWorldId(1).build();

        User user2 = new User.Builder(10002, rt, router).
                gameWorldId(1).
                currentLocation(tp.getNode(Node.get2DName(x-1, 0))).build();

        User user3 = new User.Builder(10003, rt, router).
                gameWorldId(3).
                currentLocation(tp.getNode(Node.get2DName(0, x-1))).build();

        userList.add(user);
        //userList.add(user2);
        //userList.add(user3);

        User.Request request;
        /*
           @ purposely set one edge cloud to have a sharing game world id.
            intrinsically all services should be migrated to that edge cloud
           @Test Pass!
        */

        //centralCloud.getCentralController().getEdgeClouds().get(tp.getNode(Node.get2DName(2,0))).gameWorldList.put(1, 2);

        /*
            init server
         */
        for(User u : userList){
            System.out.println(u.getName());
            request = User.Request.INITSERVICE;
            Node initServer = (Node)u.sendServiceRequest(null, request);
            u.setReserveEdgeCloud(initServer);
        }

        for(int stepCount = 0; stepCount < 20; stepCount++){
            //time slot starts: all users connect to new server & get service
            System.out.printf("[TEST]*****************************Round %d*******************************%n", stepCount);
            for(User u : userList) {
                //connect to the server and get service
                request = User.Request.CONNECT;
                u.setEdgeCloudLocation(u.getReserveEdgeCloud());
                u.sendServiceRequest(u.getEdgeCloudLocation(), request);
                //System.out.printf("[TEST] round %d: user %d connects to %s%n%n", stepCount, u.getName(), u.getEdgeCloudLocation());
                System.out.printf("[TEST] user %d location %s, connects to %s%n%n", u.getName(), u.getCurrentLocation(), u.getEdgeCloudLocation());

                request = User.Request.GETSERVICE;
                u.sendServiceRequest(u.getEdgeCloudLocation(), request);
            }

            //time slot inside : computes the optimal servers for all users
            for(User u : userList) {
                //reserve on a migrate server
                request = User.Request.GETOPTIMALSERVER;
                Node migrateServer = (Node) u.sendServiceRequest(u.getEdgeCloudLocation(), request);
                u.setReserveEdgeCloud(migrateServer);

                request = User.Request.RESERVE;
                u.sendServiceRequest(migrateServer, request);
            }

            // time slot ends.
            for(User u: userList){
                u.moveToNextStep();
                //disconnects from the original one & connects to the new one
                request = User.Request.DISCONNECT;
                u.sendServiceRequest(u.getEdgeCloudLocation(), request);

                request = User.Request.RESERVECANCEL;
                u.sendServiceRequest(u.getReserveEdgeCloud(), request);

            }



        }
    }

}
