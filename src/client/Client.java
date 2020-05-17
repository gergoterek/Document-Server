package client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class Client implements IClient, AutoCloseable, Runnable {
    private BufferedReader bfClient;
    private PrintWriter pwClient;

    private BufferedReader bfServer;
    private PrintWriter pwServer;
    private BufferedWriter bw;
    Socket s;

    public static void main(String[] args) {
        Client client = new Client(System.in, System.out);
    }

    public Client(InputStream userInput, OutputStream userOutput) {

        bfClient = new BufferedReader(new InputStreamReader(userInput));
        pwClient = new PrintWriter(userOutput, true);

        try {
            s = new Socket("localhost", 50000);
            if (s.isConnected())
                System.out.println("Connected to server");
            bfServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pwServer = new PrintWriter(s.getOutputStream(), true);
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Client close");
        pwServer.println("EXIT");
        pwClient.close();
        bfClient.close();
        bfServer.close();
        pwServer.close();
        s.close();
    }

    @Override
    public void handleDownloadDocument() throws IOException {
        pwServer.println("DOWNLOAD_DOCUMENT");
        pwClient.println(bfServer.readLine());//Give a doc name

        String fileName = bfClient.readLine();
        pwServer.println(fileName);

        String line = "";
        while ((line = bfServer.readLine()) != null) {
            if (!line.equals("END_OF_DOCUMENT") && !line.equals("NOT_FOUND")) {
                pwClient.println(line);
            } else if (line.equals("NOT_FOUND")) {
                pwClient.println(line);
                break;
            } else {
                break;
            }
        }
        //System.out.println("Download has finished");
    }

    @Override
    public void handleUploadDocument() throws IOException {
        pwServer.println("UPLOAD_DOCUMENT");

        pwClient.println(bfServer.readLine());//Give a new doc name:
        pwServer.println(bfClient.readLine());//send filename
        pwClient.println(bfServer.readLine());//Enter doc content

        String line = bfClient.readLine();
        while (!line.equals("EOF")) {
            pwServer.println(line);
            line = bfClient.readLine();
        }
        pwServer.println("END_OF_DOCUMENT");
        //System.out.println("Upload has finished");
    }

    @Override
    public void handleListDocuments() throws IOException {
        pwServer.println("LIST_DOCUMENTS");
        String line = "";
        while ((line = bfServer.readLine()) != null && !line.equals("END_OF_LIST")) {
            pwClient.println(line);
        }
    }

    @Override
    public void run() {
        try {
            out:
            while (true) {
                printMenu();
                String line = bfClient.readLine();
                switch (line) {
                    case "0":
                        handleDownloadDocument();
                        break;
                    case "1":
                        handleListDocuments();
                        break;
                    case "2":
                        handleUploadDocument();
                        break;
                    case "3":
                        break out;
                    default:
                        pwClient.println("| Warning: Invalid option.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (s != null) {
                    close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    void printMenu() {
        //System.out.println("\nMENU:");
        pwClient.println("| 0 - download document");
        pwClient.println("| 1 - list documents");
        pwClient.println("| 2 - upload content");
        pwClient.println("| 3 - EXIT");
    }
}
