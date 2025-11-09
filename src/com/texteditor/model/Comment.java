package com.texteditor.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a comment with metadata for document review and collaboration.
 */
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String author;
    private String text;
    private LocalDateTime timestamp;
    private int startPosition;
    private int endPosition;
    private String status; // "open", "resolved"

    // Constructors
    public Comment() {
        this.id = java.util.UUID.randomUUID().toString();
        this.author = System.getProperty("user.name");
        this.timestamp = LocalDateTime.now();
        this.status = "open";
    }

    public Comment(String text, int startPosition, int endPosition) {
        this();
        this.text = text;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    // Getters and Setters
    public String getId() { return id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public int getStartPosition() { return startPosition; }
    public void setStartPosition(int position) { this.startPosition = position; }

    public int getEndPosition() { return endPosition; }
    public void setEndPosition(int position) { this.endPosition = position; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Comment{" +
                "author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}
