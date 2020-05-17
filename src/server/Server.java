package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedHashSet;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        LinkedHashSet<String> index = new LinkedHashSet<>();
        System.out.println("Initial doc list:");
        for (int i = 0; i < args.length; i++) {
            File f = new File(args[i]);
            if (!f.exists()) {
                f.createNewFile();
            }
            index.add(args[i]);
            System.out.print(args[i] + " ");
        }
        int port = 50000;
        try (ServerSocket ss = new ServerSocket(port);) {
            while (true) {
                ClientHandler c1 = new ClientHandler(ss, index);
                Thread t1 = new Thread(c1);
                t1.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





