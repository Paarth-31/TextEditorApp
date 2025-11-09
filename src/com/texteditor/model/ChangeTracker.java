package com.texteditor.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a tracked change in the document.
 */
public class ChangeTracker implements Serializable {
	private static final long serialVersionUID = 1L;
    
	public enum ChangeType {
        	INSERT, DELETE, FORMAT_CHANGE
	}
    
	private String id;
	private ChangeType type;
	private String author;
	private LocalDateTime timestamp;
	private String content;
	private int position;
	private String oldValue;
	private String newValue;
	private boolean accepted;
    
    // Constructor
	public ChangeTracker() {
        	this.id = java.util.UUID.randomUUID().toString();
	        this.author = System.getProperty("user.name");
        	this.timestamp = LocalDateTime.now();
	        this.accepted = false;
	}
    
	public ChangeTracker(ChangeType type, String content, int position) {
	        this();
        	this.type = type;
	        this.content = content;
        	this.position = position;
	}
    
    // Getters and Setters
	public String getId() { return id; }
	public ChangeType getType() { return type; }
	public String getAuthor() { return author; }
	public LocalDateTime getTimestamp() { return timestamp; }
	public String getContent() { return content; }
	public int getPosition() { return position; }
	public String getOldValue() { return oldValue; }
	public void setOldValue(String value) { this.oldValue = value; }
	public String getNewValue() { return newValue; }
	public void setNewValue(String value) { this.newValue = value; }
	public boolean isAccepted() { return accepted; }
	public void setAccepted(boolean accepted) { this.accepted = accepted; }
    
	@Override
	public String toString() {
        	return "ChangeTracker{" + "type=" + type + ", author='" + author + '\'' + ", timestamp=" + timestamp +  ", accepted=" + accepted + '}';
	}
}

