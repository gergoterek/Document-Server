package client;

import java.io.*;
import java.net.Socket;


public class Client implements IClient, AutoCloseable, Runnable {
    private BufferedReader bfClient;
    private PrintWriter pwClient;

    private BufferedReader bfServer;
    private PrintWriter pwServer;
    private BufferedWriter bw;


    public static void main(String[] args) {
        Client client = new Client(System.in, System.out);
        client.run();
    }

    public Client(InputStream userInput, OutputStream userOutput) {

        bfClient = new BufferedReader(new InputStreamReader(userInput));
        pwClient = new PrintWriter(userOutput, true);
        try (
                Socket s = new Socket("localhost", 50000);
        ) {
            if (s.isConnected())
                System.out.println("Conn");
            bfServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pwServer = new PrintWriter(s.getOutputStream(), true);
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Letrejott a client");
        pwServer.println("Hello szerver");

    }

    @Override
    public void close() throws IOException {
        System.out.println("Client close");
        bfClient.close();
        pwClient.close();
        pwServer.close();
        bfServer.close();
    }

    @Override
    public void handleDownloadDocument() throws IOException {
        System.out.println("Client download");
        pwServer.println("DOWNLOAD_DOCUMENT");
        pwClient.println("Enter document name:");
        String fileName = bfClient.readLine();
        pwServer.println(fileName);

        String line;
        while ((line = bfServer.readLine()) != null) {
            if (!line.equals("END_OF_DOCUMENT")) {
                pwClient.println(line);
            } else {
                break;
            }
        }
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
        printMenu();
        try {
            while (true) {
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void printMenu() {
        pwClient.println("0 - DOWNLOAD_DOCUMENT");
        pwClient.println("1 - LIST_DOCUMENTS");
        pwClient.println("2 - UPLOAD_DOCUMENT");
    }
}
