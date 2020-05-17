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
        //System.out.println("\nClient accepted: " + s + "\n");
        bf = new BufferedReader(new InputStreamReader(s.getInputStream()));
        pw = new PrintWriter(s.getOutputStream(), true);
        this.contents = contents;
    }

    @Override
    public void close() throws IOException {
        System.out.println("\nclient is down\n");
        bf.close();
        pw.close();
        s.close();
    }

    @Override
    public void handleDownloadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException {
        toClient.println("| Enter document name:");
        String fileName = fromClient.readLine();
        if (contents.contains(fileName)) {
            synchronized (fileName.intern()) {
                try (
                        Scanner scFile = new Scanner(new File(fileName));
                ) {
                    while (scFile.hasNextLine()) {
                        toClient.println("| " + scFile.nextLine());
                    }
                    toClient.println("END_OF_DOCUMENT");
                }
            }
        } else {
            toClient.println("NOT_FOUND");
            System.out.println("not found");
        }
    }

    @Override
    public void handleUploadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException {
        toClient.println("| Enter document name:");
        String fileName = fromClient.readLine();
        ArrayList<String> fileText = new ArrayList<>();

        //Dokumentum szerkesztése
        if (contents.contains(fileName)) {
            uploadContent(fileName, fileText, fromClient, toClient, false);
        } else { //Új dokumentum
            uploadContent(fileName, fileText, fromClient, toClient, true);
        }
    }

    public void uploadContent(String fileName, ArrayList<String> fileText, BufferedReader fromClient, PrintWriter toClient, boolean isNew) throws IOException {
        synchronized (fileName.intern()) {
            if(isNew){
                synchronized (contents) {
                    contents.add(fileName);
                }
            }
            String line;
            toClient.println("| Enter document content:");
            while (!(line = fromClient.readLine()).equals("END_OF_DOCUMENT")) {
                fileText.add(line);
            }
            FileWriter fileWriter = new FileWriter(fileName, false);
            PrintWriter printWriter = new PrintWriter(fileWriter, true);
            for (String l : fileText) {
                printWriter.println(l);
            }
            fileWriter.close();
            printWriter.close();
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
    public void run() {
        try {
            String line = "";
            out:
            while (true) {
                while ((line = bf.readLine()) != null) {
                    switch (line) {
                        case ("DOWNLOAD_DOCUMENT"):
                            System.out.println("Used function: Download");
                            handleDownloadDocument(bf, pw);
                            break;
                        case ("UPLOAD_DOCUMENT"):
                            System.out.println("Used function: Upload");
                            handleUploadDocument(bf, pw);
                            break;
                        case ("LIST_DOCUMENTS"):
                            System.out.println("Used function: list");
                            handleListDocuments(pw);
                            break;
                        default:
                            handleUnknownRequest(pw);
                            break out;
                    }
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
}
