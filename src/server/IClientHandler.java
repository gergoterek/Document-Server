package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface IClientHandler {
    public void handleDownloadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException;
    public void handleUploadDocument(BufferedReader fromClient, PrintWriter toClient) throws IOException ;
    public void handleListDocuments(PrintWriter toClient) throws IOException;
    public void handleUnknownRequest(PrintWriter toClient) throws IOException;
}