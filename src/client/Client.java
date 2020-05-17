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
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Client close");
        bfClient.close();
        pwClient.close();
        pwServer.close();
        bfServer.close();
        s.close();
        System.exit(0);
    }

    @Override
    public void handleDownloadDocument() throws IOException {
        pwServer.println("DOWNLOAD_DOCUMENT");
        pwClient.println(bfServer.readLine());

        String fileName = bfClient.readLine();
        pwServer.println(fileName);


        String line = "";
        while ((line = bfServer.readLine()) != null && !bfServer.readLine().equals("NOT_FOUND")) {
            if (!line.equals("END_OF_DOCUMENT")) {
                pwClient.println(line);
            } else {
                break;
            }
        }
        System.out.println("Vege a letoltesnek");
    }

    @Override
    public void handleUploadDocument() throws IOException {
        pwServer.println("UPLOAD_DOCUMENT");
        //pwClient.println("Enter document name:");

        pwClient.println(bfServer.readLine());//Give a new doc name:
        pwServer.println(bfClient.readLine());//send filename
        pwClient.println(bfServer.readLine());//Enter doc content

        String line;
        while ((line = bfClient.readLine()) != null && line.length() != 0 && !line.equals("EOF")) {
            pwServer.println(line);
        }
        pwServer.println("END_OF_DOCUMENT");
        System.out.println("Vege a feltoltesnek");
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
                    default:
                        pwClient.println("Warning: Invalid option.");
                        close();
                        //pwServer.println("exit");
                        //close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void printMenu() {
        System.out.println("MENU:");
        pwClient.println("0 - DOWNLOAD_DOCUMENT");
        pwClient.println("1 - LIST_DOCUMENTS");
        pwClient.println("2 - UPLOAD_DOCUMENT");
    }
}
