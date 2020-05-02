package server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws Exception {
        LinkedHashSet<String> index = new LinkedHashSet<>();
        for (int i = 0; i < args.length; i++) {
            index.add(args[i]);
        }

        int port = 50000;
        //try (
        ServerSocket ss = new ServerSocket(port);

        ClientHandler client1 = new ClientHandler(ss, index);
        ClientHandler client2 = new ClientHandler(ss, index);

        while (true) {
            client1.run();
            client2.run();
        }


//                Socket s1 = ss.accept();
//                Scanner sc1 = new Scanner(s1.getInputStream());
//                PrintWriter pw1 = new PrintWriter(s1.getOutputStream());
//
//                Socket s2 = ss.accept();
//                Scanner sc2 = new Scanner(s2.getInputStream());
//                PrintWriter pw2 = new PrintWriter(s2.getOutputStream());
        //){
//            while (sc1.hasNextLine()) {
//                var line1 = sc1.nextLine();
//                pw2.println(line1);
//                pw2.flush();
//
//                if (!sc2.hasNextLine())   break;
//                var line2 = sc2.nextLine();
//                pw1.println(line2);
//                pw1.flush();
//            }
//            if (s1 != null) {
//                s1.close();
//            }
//            if (s2 != null) {
//                s2.close();
//            }
    }
}


