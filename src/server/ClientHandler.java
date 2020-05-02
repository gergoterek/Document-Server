package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class ClientHandler implements IClientHandler, AutoCloseable, Runnable{

    private Socket s;
    private BufferedReader bf;
    private PrintWriter pw;
    private LinkedHashSet<String> contents;

    public ClientHandler(ServerSocket ss, LinkedHashSet<String> contents) throws Exception{
        s = ss.accept();
        bf = new BufferedReader(new InputStreamReader(s.getInputStream()));
        pw = new PrintWriter(s.getOutputStream());
        this.contents = contents;
    }
    public void close() throws Exception {
        if (s != null) {
            pw.flush();
            s.close();
        }
    }
    public void handleDownloadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException {
        bf = fromClient;
        pw.println("Give a doc name:");
        String fileName = fromClient.readLine();
        synchronized (contents) {
            if (contents.contains(fileName)) {
                try (
                    Scanner scFile = new Scanner(new File(fileName));
                ) {
                    while (scFile.hasNextLine()) {
                        pw.println(scFile.nextLine());
                    }
                    pw.println("END_OF_DOCUMENT");
                }
            } else {
                toClient.println("NOT_FOUND");
            }
        }
        toClient.flush();
        fromClient.close();
    }
    public void handleUploadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException {
        pw.println("Give a new doc name:");
        String fileName = fromClient.readLine();
        ArrayList<String> fileText = new ArrayList<>();

        //Dokumentum szerkesztése
        if(contents.contains(fileName)){
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
                PrintWriter printWriter = new PrintWriter(fileWriter);
                for(String l: fileText) {
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
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(String l: fileText) {
            printWriter.print(l);
        }
        printWriter.close();
        contents.add(fileName);
        toClient.println("END_OF_DOCUMENT");
        fromClient.close();

    }
    public void handleListDocuments(PrintWriter toClient){
        for(String name: contents){
            toClient.println(name);
        }
        toClient.println("END_OF_LIST");
    }
    public void handleUnknownRequest(PrintWriter toClient) throws IOException{
        toClient.flush();
        s.close();
    }
    public void run(){
        try {
            String line;
            while ((line = this.bf.readLine()) != null) {
                switch (line) {
                    case "DOWNLOAD_DOCUMENT":
                        handleDownloadDocument(bf, pw);
                        break;
                    case "UPLOAD_DOCUMENT":
                        handleUploadDocument(bf, pw);
                        break;
                    case "LIST_DOCUMENTS":
                        handleListDocuments(pw);
                        break;
                    default:
                        handleUnknownRequest(pw);
                        break;
                }
            }
        } catch (IOException e){}
    }
}
