package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class ClientHandler implements IClientHandler, AutoCloseable, Runnable {

    private Socket s;
    private BufferedReader bf;
    private PrintWriter pw;
    private LinkedHashSet<String> contents;

    public ClientHandler(ServerSocket ss, LinkedHashSet<String> contents) throws Exception {
        System.out.println("ClientHandler konstruktor");
        s = ss.accept();
        bf = new BufferedReader(new InputStreamReader(s.getInputStream()));
        pw = new PrintWriter(s.getOutputStream(), true);
        this.contents = contents;
    }

    @Override
    public void close() throws Exception {
        System.out.println("ClientHandler close");
        pw.close();
        bf.close();
        s.close();
    }

    @Override
    public void handleDownloadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException {
        System.out.println("ClientHandler letoltes");
        toClient.println("Give a doc name:");
        String fileName = fromClient.readLine();
        synchronized (contents) {
            if (contents.contains(fileName)) {
                try (
                        Scanner scFile = new Scanner(new File(fileName));
                ) {
                    while (scFile.hasNextLine()) {
                        toClient.println(scFile.nextLine());
                    }
                    toClient.println("END_OF_DOCUMENT");
                }
            } else {
                toClient.println("NOT_FOUND");
            }
        }
    }

    @Override
    public void handleUploadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException {
        System.out.println("ClientHandler feltoltes");
        toClient.println("Give a new doc name:");
        String fileName = fromClient.readLine();
        ArrayList<String> fileText = new ArrayList<>();

        //Dokumentum szerkesztése
        if (contents.contains(fileName)) {
            synchronized (contents) {
                String line;
                while ((line = fromClient.readLine()) != null) {
                    if (!line.equals("END_OF_DOCUMENT")) {
                        fileText.add(line);
                    } else {
                        break;
                    }
                }
                FileWriter fileWriter = new FileWriter(fileName);
                PrintWriter printWriter = new PrintWriter(fileWriter, true);
                for (String l : fileText) {
                    printWriter.print(l);
                }
            }
        } else { //Új dokumentum
            String line;
            while ((line = fromClient.readLine()) != null) {
                if (!line.equals("END_OF_DOCUMENT")) {
                    fileText.add(line);
                } else {
                    break;
                }
            }
        }
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter, true);
        for (String l : fileText) {
            printWriter.print(l);
        }
        contents.add(fileName);
        toClient.println("END_OF_DOCUMENT");
    }

    @Override
    public void handleListDocuments(PrintWriter toClient) {
        System.out.println("ClientHandler lista");
        for (String name : contents) {
            toClient.println(name);
        }
        toClient.println("END_OF_LIST");
    }

    @Override
    public void handleUnknownRequest(PrintWriter toClient) throws IOException {
        toClient.close();
        //s.close();
    }

    @Override
    public void run() {
        //System.out.println("ClientHandler run");

        try {
            while (true) {
                //String line;
                //while ((line = bf.readLine()) != null) {

                System.out.println("ClientHandler run bejottem");

                String line = bf.readLine();
                System.out.println(line + "rgerwge");
                switch (line) {
                    case "DOWNLOAD_DOCUMENT":
                        System.out.println("ClientHandler DOWNLOAD bejottem");
                        handleDownloadDocument(bf, pw);
                        break;
                    case "UPLOAD_DOCUMENT":
                        System.out.println("ClientHandler UPLOAD bejottem");
                        handleUploadDocument(bf, pw);
                        break;
                    case "LIST_DOCUMENTS":
                        handleListDocuments(pw);
                        break;
                    default:
                        System.out.println(line + "-t nyomta meg kliens");
                        handleUnknownRequest(pw);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
