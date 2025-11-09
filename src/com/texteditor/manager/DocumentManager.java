package com.texteditor.manager;

import com.texteditor.model.Document;
import com.texteditor.util.FileHandler;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages all document operations following the Manager pattern.
 * Encapsulates document lifecycle and multi-document management.
 */
public class DocumentManager {
	private List<Document> openDocuments;
	private Document currentDocument;
	private FileHandler fileHandler;
	private List<String> recentFiles;
	private Stack<Document> documentHistory;
    
    // Constructor with dependency injection
	public DocumentManager(FileHandler fileHandler) {
        	this.openDocuments = new CopyOnWriteArrayList<>();
	        this.fileHandler = fileHandler;
        	this.recentFiles = new ArrayList<>();
	        this.documentHistory = new Stack<>();
	}
    
    // Document creation and management
	public Document createNewDocument() {
        	Document doc = new Document();
	        openDocuments.add(doc);
        	currentDocument = doc;
	        documentHistory.push(doc);
        	return doc;
	}
    
	public Document openDocument(String filePath) {
		Document doc = fileHandler.loadDocument(filePath);
		if (doc != null) {
			openDocuments.add(doc);
			currentDocument = doc;
			addToRecentFiles(filePath);
			documentHistory.push(doc);
		}
		return doc;
	}
    
	public boolean saveDocument(Document document) {
        	boolean success = fileHandler.saveDocument(document);
	        if (success && document.getFilePath() != null) {
        		addToRecentFiles(document.getFilePath());
		}
		return success;
	}
    
	public boolean saveDocumentAs(Document document, String newPath) {
        	document.setFilePath(newPath);
	        boolean success = fileHandler.saveDocument(document);
        	if (success) {
		        addToRecentFiles(newPath);
        	}
		return success;
	}
    
	public void closeDocument(Document document) {
        	openDocuments.remove(document);
        	if (currentDocument == document && !openDocuments.isEmpty()) {
        		currentDocument = openDocuments.get(0);
		}
	}
    
	public void closeAllDocuments() {
        	openDocuments.clear();
	        currentDocument = null;
	}
    
    // Getters
	public Document getCurrentDocument() { return currentDocument; }
    
	public void setCurrentDocument(Document document) {
        	if (openDocuments.contains(document)) {
        		currentDocument = document;
		}
	}
    
	public List<Document> getOpenDocuments() { return new ArrayList<>(openDocuments);}
	public List<String> getRecentFiles() { return new ArrayList<>(recentFiles);}
    
	private void addToRecentFiles(String filePath) {
        	recentFiles.remove(filePath);
	        recentFiles.add(0, filePath);
        	if (recentFiles.size() > 10) {
			recentFiles.remove(recentFiles.size() - 1);
		}
	}
    
    // Document merging
	public Document mergeDocuments(List<Document> documentsToMerge) {
        	Document merged = createNewDocument();
	        StringBuilder content = new StringBuilder();
		for (Document doc : documentsToMerge) {
			content.append(doc.getContent()).append("\n\n");
		}
		merged.setContent(content.toString());
		return merged;
	}
}
