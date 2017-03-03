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
 * Created by Wuyang on 12/8/16.
 */
/*
public class TestGlobal {

    public User initUser(String name, String service, RandomTrace rt, Router router, int gwId, double xEdge, double yEdge) {
        User user = new User(name, rt, router);
        user.setGameWorldId(gwId);
        user.setEdgeCloudLocation(rt.getTopology().getNode((Node.get2DName(xEdge, yEdge))));
        User.Service service1 = user.new Service(service);
        return user;
    }

    public static void main(String args[]) {
        //init topology
        final int x = 5;
        final int y = (x - 1) / 2;
        Topology tp = Topology.getMByNMatrix(x, x);
        tp.calAllNodesDistance();
        //init central controller & edge clouds & router
        CentralCloud centralCloud = new CentralCloud("101", tp.getNode(Node.get2DName(y, y)), tp);
        Router router = new Router(centralCloud);

        //all rounds
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < x; j++) {
                if (i == 0 || i == x - 1 || (j == 0 && i != 0 && i != x - 1) || (j == x - 1 && i != 0 && i != x - 1)) {
                    new EdgeCloud(String.format("%d,%d", i, j), tp.getNode(Node.get2DName(i, j)), centralCloud, router);
                }
            }
        }


        //init client
        RandomTrace rt1 = new RandomTrace(tp, tp.getNode(Node.get2DName(4, 2)), RandomTrace.generateRingTrace(tp, x, x));
        RandomTrace rt2 = new RandomTrace(tp, tp.getNode(Node.get2DName(4, 4)), RandomTrace.generateRingTrace(tp, x, x));
        RandomTrace rt3 = new RandomTrace(tp, tp.getNode(Node.get2DName(0, 0)), RandomTrace.generateRingTrace(tp, x, x));

        TestGlobal test = new TestGlobal();
        ArrayList<User> users = new ArrayList<>();
        String service = "game";
        User user1 = test.initUser("10001", service, rt1, router, 1, 4, 4);
        User user2 = test.initUser("10002", service, rt2, router, 1, 4, 0);
        User user3 = test.initUser("10003", service, rt3, router, 3, 4, 0);
        users.add(user1);
        users.add(user2);
        //users.add(user3);

        //connect to init server
        for (User user : users) {
            user.getService(service).setRequestId("init");
            System.out.println(user1.getService("game"));
            user.sendServiceRequest(user.getEdgeCloudLocation(), user.getService(service));

            user.getService(service).setRequestId("connect");
            user.sendServiceRequest(user.getEdgeCloudLocation(), user.getService(service));
        }

        for (int timeSlot = 1; timeSlot < 100; timeSlot++) {
            for (User user : users) {
                user.getService(service).setRequestId("report");
                user.sendServiceRequest(user.getEdgeCloudLocation(), user.getService(service));
            }

            //open global interface. to calculate the optimal servers for all users.
            System.out.println(String.format("timeSlot %d start compute global optimization", timeSlot));
            centralCloud.getCentralController().computeGlobalOptimalEdgeServer();
            System.out.println(String.format("timeSlot %d end compute global optimization", timeSlot));
            for (User user : users) {
                user.getService(service).setRequestId("globalOptimal");
                Node migrateServer = (Node) user.sendServiceRequest(user.getEdgeCloudLocation(), user.getService(service));
                System.out.printf("[%d] >>>>> user %s migrate server: %s%n", timeSlot, user.getName(), migrateServer);

                user.getService(service).setRequestId("disconnect");
                user.sendServiceRequest(user.getEdgeCloudLocation(), user.getService(service));

                user.getService(service).setRequestId("connect");
                user.sendServiceRequest(user.getEdgeCloudLocation(), user.getService(service));
                user.setEdgeCloudLocation(migrateServer);

                System.out.printf("[%d] user %s next step: %s%n", timeSlot, user.getName(), user.moveToNextStep());
            }
        }

        }
    }
*/