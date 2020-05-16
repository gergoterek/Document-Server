package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException{
        LinkedHashSet<String> index = new LinkedHashSet<>();
        LinkedList<ClientHandler> ch = new LinkedList<>();

        for (int i = 0; i < args.length; i++) {
            index.add(args[i]);
            System.out.println(args[i]);
        }
        int port = 50000;
        int j = 1;
        try (ServerSocket ss = new ServerSocket(port);) {
            while (true) {
                System.out.println(" Binding to port " + port + ", please wait  ...");
                System.out.println("Server started: " + ss);
                System.out.println("\nWaiting for the " + j + "th client ...");
                ClientHandler c1 = new ClientHandler(ss, index);
                ch.add(c1);

                Thread t1 = new Thread(c1);
                t1.start();
                ++j;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


//    Sajnos nem működik:
//    1. Server indítása után a list document folyton bezárja a socketet, és a kliens kilép.
//    2. A feltöltés nem működik, ugyanis az end_of_document-et nem küldi át. flush() ?
//      Egyébként EOF és nem END_OF_DOCUMENT kell a felhasználótól.
//
//    3. A kliens ne blokkolódjon, ha úgy listázzuk a fájlokat, hogy még egy dokumentum sincs regisztrálva
//    4. A feltöltéskor, minden szálnak meg kell várnia, amíg a teljes dokumentum feltöltése megtörtént(szöveggel)
//    5. Ha nincs fájl akkor is működjön, ne szalljon el





