package server;


import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedHashSet;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        LinkedHashSet<String> index = new LinkedHashSet<>();
        System.out.println("Doc list:");
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


//    Sajnos nem működik:
//    1. Server indítása után a list document folyton bezárja a socketet, és a kliens kilép.
//    2. A feltöltés nem működik, ugyanis az end_of_document-et nem küldi át. flush() ?
//      Egyébként EOF és nem END_OF_DOCUMENT kell a felhasználótól.
//
//    3. A kliens ne blokkolódjon, ha úgy listázzuk a fájlokat, hogy még egy dokumentum sincs regisztrálva
//    4. A feltöltéskor, minden szálnak meg kell várnia, amíg a teljes dokumentum feltöltése megtörtént(szöveggel)
//    5. Ha nincs fájl akkor is működjön, ne szalljon el

//hozzáír a fájhoz upload
//exit, kell e sys.exit
//hogy csinálták meg a close-t hogy meghívja állandóan
//hogyan lép ki a kliens





