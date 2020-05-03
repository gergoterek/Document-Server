package client;

import java.io.*;
import java.net.Socket;


public class Client implements IClient, AutoCloseable, Runnable {
    private BufferedReader bfClient;
    private PrintWriter pwClient;

    private BufferedReader bfServer;
    private PrintWriter pwServer;


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
            bfServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pwServer = new PrintWriter(s.getOutputStream(), true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Letrejott a client");
    }

    @Override
    public void close() throws Exception {
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
        //System.out.println("Client upload");
        pwServer.println("UPLOAD_DOCUMENT");
        pwClient.println("Enter document name:");
        String fileName = bfClient.readLine();
        pwServer.println(fileName);
        pwClient.println("Enter document content");

        String line;
        while ((line = bfClient.readLine()) != null && line.length() != 0) {
            pwServer.println(line);
            pwServer.println(line);
        }
        pwServer.println("END_OF_DOCUMENT");
    }

    @Override
    public void handleListDocuments() throws IOException {
        //System.out.println("Client doc list");
        pwServer.println("LIST_DOCUMENTS");
        String line;
        while ((line = bfServer.readLine()) != null && line.equals("END_OF_LIST")) {
            pwClient.println(line);
        }
    }

    @Override
    public void run() {
        //pwClient.println("Client run");
        printMenu();
        try {
            while (true) {
                String line = bfClient.readLine();
                int num = Integer.parseInt(line);
                switch (num) {
                    case 0:
                        handleDownloadDocument();
                        break;
                    case 1:
                        handleListDocuments();
                        break;
                    case 2:
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
