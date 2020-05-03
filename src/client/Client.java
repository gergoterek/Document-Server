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
        while(true){
            client.run();
        }
    }
    public Client(InputStream userInput, OutputStream userOutput) {
//        PrintWriter writer = new PrintWriter(System.out);
//        writer.println("Method 2");
//        writer.flush();
//        writer.close();
        //System.out.println("Client ctor");

        bfClient = new BufferedReader(new InputStreamReader(userInput));
        pwClient = new PrintWriter(userOutput, true);
        try (
                Socket s = new Socket("localhost", 50000);
        ) {
            bfServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pwServer = new PrintWriter(s.getOutputStream(), true);

        } catch (Exception e) {
        }


    }

    public void close() throws Exception {
        bfClient.close();
        pwClient.close();
        pwServer.close();
        bfServer.close();
    }

    public void handleDownloadDocument() throws IOException {
        //string.getBytes(Charset.forName("UTF-8"))
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

    public void handleUploadDocument() throws IOException {
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
    public void handleListDocuments() throws IOException {
        pwServer.println("LIST_DOCUMENTS");
        String line;
        while ((line = bfServer.readLine()) != null && line.equals("END_OF_LIST")) {
            pwClient.println(line);
        }
    }

    public void run(){
        pwClient.println("Client run");
        printMenu();
        try {
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
                    pwClient.println("Rossz gomb");
            }
        } catch (IOException e){}
    }
    void printMenu(){
        pwClient.println("0 - DOWNLOAD_DOCUMENT");
        pwClient.println("1 - LIST_DOCUMENTS");
        pwClient.println("2 - UPLOAD_DOCUMENT");
    }
}
