package test;

import edgecloud.CentralCloud;
import edgecloud.EdgeCloud;
import edgecloud.Router;
import topology.Node;
import topology.Topology;
import user.RandomTrace;
import user.User;

/**
 * Created by Wuyang on 12/1/16.
 */

public class Test {
    public static void main(String args[]) {
        /*
            init topology
         */
        final int x = 3;
        final int y = (x - 1) / 2;
        Topology tp = Topology.getMByNMatrix(x, x);
        tp.calAllNodesDistance();

        /*
            init central controller & edge clouds & router
         */
        CentralCloud centralCloud = new CentralCloud(101, tp.getNode(Node.get2DName(y, y)), tp);
        //set gamma value
        centralCloud.getCentralController().setGamma(0);
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
        User user = new User(10001, rt, router);
        User.Request request;
        user.setGameWorldId(1);

        /*
            init server
         */
        request = User.Request.INITSERVICE;
        Node initServer = (Node)user.sendServiceRequest(null, request);
        user.setReserveEdgeCloud(initServer);
        //System.out.println("[User] initiate server completes at " + initServer + "\n");


        for(int stepCount = 0; stepCount < 20; stepCount++){
            System.out.printf("[TEST] round %d: user location %s%n", stepCount, user.getCurrentLocation());
            //connect to the server and get service
            request = User.Request.CONNECT;
            user.setEdgeCloudLocation(user.getReserveEdgeCloud());
            user.sendServiceRequest(user.getEdgeCloudLocation(), request);
            System.out.printf("[TEST] round %d: user connects to %s%n%n", stepCount, user.getEdgeCloudLocation());

            request = User.Request.GETSERVICE;
            user.sendServiceRequest(user.getEdgeCloudLocation(), request);

            //reserve on a migrate server
            request = User.Request.GETOPTIMALSERVER;
            Node migrateServer = (Node) user.sendServiceRequest(user.getEdgeCloudLocation(), request);
            user.setReserveEdgeCloud(migrateServer);

            request = User.Request.RESERVE;
            user.sendServiceRequest(migrateServer, request);

            //go to the next time slot
            user.moveToNextStep();

            //disconnects from the original one & connects to the new one
            request = User.Request.DISCONNECT;
            user.sendServiceRequest(user.getEdgeCloudLocation(), request);

            request = User.Request.RESERVECANCEL;
            user.sendServiceRequest(migrateServer, request);
        }
    }

}
