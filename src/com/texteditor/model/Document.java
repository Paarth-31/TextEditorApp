package com.texteditor.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a document with full metadata and content management.
 * Encapsulates all document-related information and operations.
 */
public class Document implements Serializable {
	private static final long serialVersionUID = 1L;
    
	private String id;
	private String content;
	private String filePath;
	private String author;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private String title;
	private Map<String, String> metadata;
	private List<Comment> comments;
	private List<String> changeHistory;
	private boolean isProtected;
	private String protectionPassword;
	private String currentStyle;
    
    // Constructors
	public Document() {
		this.id = UUID.randomUUID().toString();
	        this.content = "";
        	this.author = System.getProperty("user.name");
	        this.createdDate = LocalDateTime.now();
        	this.modifiedDate = LocalDateTime.now();
	        this.metadata = new HashMap<>();
        	this.comments = new ArrayList<>();
	        this.changeHistory = new ArrayList<>();
        	this.isProtected = false;
	        this.title = "Untitled Document";
	}
    
	public Document(String filePath) {
        	this();
	        this.filePath = filePath;
	}
    
    // Getters and Setters with encapsulation
	public String getId() { return id; }
	public String getContent() { return content; }
	
	public void setContent(String content) {
        	this.content = content;
	        this.modifiedDate = LocalDateTime.now();
	}
	public String getFilePath() { return filePath; }
	public void setFilePath(String filePath) { this.filePath = filePath; }
	public String getAuthor() { return author; }
	public void setAuthor(String author) { this.author = author; }
	public LocalDateTime getCreatedDate() { return createdDate; }
	public LocalDateTime getModifiedDate() { return modifiedDate; }
	public void setModifiedDate() { this.modifiedDate = LocalDateTime.now(); }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public Map<String, String> getMetadata() { return new HashMap<>(metadata); }
	
	public void addMetadata(String key, String value) {
        	metadata.put(key, value);
	        setModifiedDate();
	}
	public List<Comment> getComments() { return new ArrayList<>(comments); }
	public void addComment(Comment comment) { comments.add(comment);}
	public List<String> getChangeHistory() { return new ArrayList<>(changeHistory); }
	public void addToChangeHistory(String change) { changeHistory.add(change);}
	public boolean isProtected() { return isProtected; }
	public void setProtectionPassword(String password) {
        	this.isProtected = true;
	        this.protectionPassword = password;
	}
	public boolean validatePassword(String password) { return isProtected && protectionPassword.equals(password);}
	public String getCurrentStyle() { return currentStyle; }
	public void setCurrentStyle(String style) { this.currentStyle = style; }

	// Document Statistics
	public int getWordCount() { return content.trim().split("\\s+").length;}
	public int getCharacterCount() { return content.length();}
	public int getCharacterCountWithoutSpaces() { return content.replaceAll("\\s+", "").length();}
	public int getLineCount() { return content.split("\n").length;}

	public DocumentStats getStatistics() {
		return new DocumentStats(
				getWordCount(),
				getCharacterCount(),
				getCharacterCountWithoutSpaces(),
				getLineCount(),
				createdDate,
				modifiedDate,
				author
				);
	}
    
    // Inner class for statistics
	public static class DocumentStats {
        	public final int words;
	        public final int characters;
        	public final int charactersWithoutSpaces;
	        public final int lines;
        	public final LocalDateTime createdDate;
	        public final LocalDateTime modifiedDate;
        	public final String author;
        
		public DocumentStats(int words, int characters, int charactersWithoutSpaces, int lines, LocalDateTime createdDate, LocalDateTime modifiedDate, String author) {
			this.words = words;
			this.characters = characters;
			this.charactersWithoutSpaces = charactersWithoutSpaces;
			this.lines = lines;
			this.createdDate = createdDate;
			this.modifiedDate = modifiedDate;
			this.author = author;
		}
	}
}
