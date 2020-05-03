package server;

import java.net.ServerSocket;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Server {
    public static void main(String[] args) {
        LinkedHashSet<String> index = new LinkedHashSet<>();
        LinkedList<ClientHandler> ch = new LinkedList<>();

        for (int i = 0; i < args.length; i++) {
            index.add(args[i]);
        }
        try (ServerSocket ss = new ServerSocket(50000);) {
            while (true) {
                ClientHandler c1 = new ClientHandler(ss, index);
                ch.add(c1);

                Thread t1 = new Thread(c1);
                t1.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

        //int port = 50000;
//        try {
//            ss = new ServerSocket(port);
//            ClientHandler client1 = new ClientHandler(ss, index);
//            client1.run();
//
//        } catch (Exception ee) {
//            if (ss != null && !ss.isClosed()) {
//                try {
//                    ss.close();
//                } catch (IOException e) {
//                    e.printStackTrace(System.err);
//                }
//            }

            //ClientHandler client2 = new ClientHandler(ss, index);

//        Thread t1 = new Thread(() -> {
//            while (true) {
//                client1.run();
//                //client2.run();
//            }
//        });
//        t1.start();
            //t1.join());




