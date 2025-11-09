package com.texteditor.util;
import com.texteditor.model.Document;
public interface FileHandler {
    Document loadDocument(String filePath);
    boolean saveDocument(Document document);
    boolean deleteFile(String filePath);
    boolean fileExists(String filePath);
}
