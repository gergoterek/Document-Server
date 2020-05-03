package client;

import java.io.IOException;

public interface IClient  {
    public void handleUploadDocument() throws IOException ;
    public void handleDownloadDocument() throws IOException ;
    public void handleListDocuments() throws IOException ;
}