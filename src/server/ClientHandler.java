package server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class ClientHandler implements IClientHandler, AutoCloseable, Runnable {

    private BufferedReader bf;
    private PrintWriter pw;
    private LinkedHashSet<String> contents;
    Socket s;

    public ClientHandler(ServerSocket ss, LinkedHashSet<String> contents) throws Exception {
        s = ss.accept();
        System.out.println("Client accepted: " + s);
        bf = new BufferedReader(new InputStreamReader(s.getInputStream()));
        pw = new PrintWriter(s.getOutputStream(), true);
        this.contents = contents;
    }

    @Override
    public void close() throws IOException {
        System.out.println("closeolja");
        bf.close();
        pw.close();
        s.close();
    }

    @Override
    public void handleDownloadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException {
        toClient.println("Give a doc name:");
        String fileName = fromClient.readLine();
        if (contents.contains(fileName)) {
            synchronized (fileName.intern()) {
                try (
                        Scanner scFile = new Scanner(new File(fileName));
                ) {
                    while (scFile.hasNextLine()) {
                        toClient.println(scFile.nextLine());
                    }
                    toClient.println("END_OF_DOCUMENT");
                }
            }
        } else {
            toClient.println("NOT_FOUND");
        }
    }

    @Override
    public void handleUploadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException {
        toClient.println("Give a doc name:");
        String fileName = fromClient.readLine();
        ArrayList<String> fileText = new ArrayList<>();

        //Dokumentum szerkesztése
        if (contents.contains(fileName)) {
            synchronized (fileName.intern()) {
                String line;
                toClient.println("Enter document content:");
                while ((line = fromClient.readLine()) != null) {
                    if (!line.equals("END_OF_DOCUMENT")) {
                        fileText.add(line);
                    } else {
                        break;
                    }
                }

                FileWriter fileWriter = new FileWriter(fileName, true);
                PrintWriter printWriter = new PrintWriter(fileWriter, true);
                for (String l : fileText) {
                    printWriter.print(l);
                }
            }
            toClient.println("END_OF_DOCUMENT");

        } else { //Új dokumentum
            String line;
            toClient.println("Enter document content:");
            while ((line = fromClient.readLine()) != null) {
                if (!line.equals("END_OF_DOCUMENT")) {
                    fileText.add(line);
                } else {
                    break;
                }
            }

            if (!fileText.isEmpty()) {
                FileWriter fileWriter = new FileWriter(fileName, true);
                PrintWriter printWriter = new PrintWriter(fileWriter, true);
                for (String l : fileText) {
                    printWriter.print(l);
                }
                synchronized (contents) {
                    contents.add(fileName);
                }
            }
            toClient.println("END_OF_DOCUMENT");
        }
    }

    @Override
    public void handleListDocuments(PrintWriter toClient) {
        for (String name : contents) {
            toClient.println(name);
        }
        toClient.println("END_OF_LIST");
    }

    @Override
    public void handleUnknownRequest(PrintWriter toClient) throws IOException {
        close();
    }

    @Override
    public void run(){
        try {
            String line = "";
            while (true) {
                while ((line = bf.readLine()) != null) {
                    switch (line) {
                        case ("DOWNLOAD_DOCUMENT"):
                            System.out.println("Function: Download");
                            handleDownloadDocument(bf, pw);
                            break;
                        case ("UPLOAD_DOCUMENT"):
                            System.out.println("Function: Upload");
                            handleUploadDocument(bf, pw);
                            break;
                        case ("LIST_DOCUMENTS"):
                            System.out.println("Function: list");
                            handleListDocuments(pw);
                            break;
                        case ("exit"):
                            handleUnknownRequest(pw);
                            break;
                        default:
                            handleUnknownRequest(pw);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
