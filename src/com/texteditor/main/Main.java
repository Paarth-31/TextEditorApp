package com.texteditor.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import com.texteditor.manager.*;
import com.texteditor.model.*;
import com.texteditor.engine.*;
import com.texteditor.util.*;

public class Main extends Application {
    
    private DocumentManager documentManager;
    private StyleManager styleManager;
    private PreferencesManager preferencesManager;
    private FormattingEngine formattingEngine;
    private SearchReplaceEngine searchEngine;
    private TrackingEngine trackingEngine;
    
    private TextArea textArea;
    private Label statusLabel;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize managers and engines
        initializeComponents();
        
        // Create UI
        BorderPane root = new BorderPane();
        
        // Menu Bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        
        // Text Area
        textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12pt;");
        textArea.textProperty().addListener((obs, oldText, newText) -> {
            updateStatusBar();
        });
        root.setCenter(textArea);
        
        // Status Bar
        statusLabel = new Label("Ready | Words: 0 | Characters: 0 | Lines: 1");
        statusLabel.setPadding(new Insets(5));
        statusLabel.setStyle("-fx-background-color: #f0f0f0;");
        root.setBottom(statusLabel);
        
        // Create Scene
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Advanced Text Editor");
        primaryStage.show();
    }
    
    private void initializeComponents() {
        // Initialize file handler
        FileHandler fileHandler = new FileHandlerImpl();
        
        // Initialize managers
        documentManager = new DocumentManager(fileHandler);
        styleManager = new StyleManager();
        preferencesManager = new PreferencesManager(Constants.PREFERENCES_FILE);
        
        // Initialize engines
        formattingEngine = new FormattingEngine();
        searchEngine = new SearchReplaceEngine();
        trackingEngine = new TrackingEngine();
        
        // Create new document
        documentManager.createNewDocument();
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        newItem.setOnAction(e -> handleNew());
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(e -> handleOpen());
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(e -> handleSave());
        MenuItem saveAsItem = new MenuItem("Save As...");
        saveAsItem.setOnAction(e -> handleSaveAs());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> handleExit());
        
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, 
                                   new SeparatorMenuItem(), exitItem);
        
        // Edit Menu
        Menu editMenu = new Menu("Edit");
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setOnAction(e -> textArea.cut());
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> textArea.copy());
        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setOnAction(e -> textArea.paste());
        MenuItem selectAllItem = new MenuItem("Select All");
        selectAllItem.setOnAction(e -> textArea.selectAll());
        MenuItem findItem = new MenuItem("Find & Replace");
        findItem.setOnAction(e -> showFindReplaceDialog());
        
        editMenu.getItems().addAll(undoItem, redoItem, new SeparatorMenuItem(),
                                   cutItem, copyItem, pasteItem, selectAllItem,
                                   new SeparatorMenuItem(), findItem);
        
        // Format Menu
        Menu formatMenu = new Menu("Format");
        MenuItem fontItem = new MenuItem("Font...");
        fontItem.setOnAction(e -> showFontDialog());
        
        formatMenu.getItems().add(fontItem);
        
        // View Menu
        Menu viewMenu = new Menu("View");
        MenuItem statsItem = new MenuItem("Document Statistics");
        statsItem.setOnAction(e -> showStatistics());
        
        viewMenu.getItems().add(statsItem);
        
        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, formatMenu, viewMenu, helpMenu);
        return menuBar;
    }
    
    // Handler methods
    private void handleNew() {
        textArea.clear();
        documentManager.createNewDocument();
        updateStatusBar();
    }
    
    private void handleOpen() {
        // Implement file chooser and open logic
        System.out.println("Open file dialog");
    }
    
    private void handleSave() {
        Document doc = documentManager.getCurrentDocument();
        if (doc != null) {
            doc.setContent(textArea.getText());
            documentManager.saveDocument(doc);
        }
    }
    
    private void handleSaveAs() {
        // Implement save as dialog
        System.out.println("Save as dialog");
    }
    
    private void handleExit() {
        System.exit(0);
    }
    
    private void showFindReplaceDialog() {
        // Implement find/replace dialog
        System.out.println("Find/Replace dialog");
    }
    
    private void showFontDialog() {
        // Implement font selection dialog
        System.out.println("Font dialog");
    }
    
    private void showStatistics() {
        Document doc = documentManager.getCurrentDocument();
        if (doc != null) {
            doc.setContent(textArea.getText());
            Document.DocumentStats stats = doc.getStatistics();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Document Statistics");
            alert.setHeaderText("Current Document Statistics");
            alert.setContentText(
                "Words: " + stats.words + "\n" +
                "Characters: " + stats.characters + "\n" +
                "Characters (no spaces): " + stats.charactersWithoutSpaces + "\n" +
                "Lines: " + stats.lines + "\n" +
                "Author: " + stats.author + "\n" +
                "Created: " + stats.createdDate + "\n" +
                "Modified: " + stats.modifiedDate
            );
            alert.showAndWait();
        }
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Advanced Text Editor");
        alert.setContentText(
            "Version: 1.0.0\n" +
            "A feature-rich text editor with advanced formatting capabilities\n" +
            "Built with JavaFX and following OOP principles"
        );
        alert.showAndWait();
    }
    
    private void updateStatusBar() {
        String text = textArea.getText();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int chars = text.length();
        int lines = text.isEmpty() ? 1 : text.split("\n", -1).length;
        
        statusLabel.setText(
            "Ready | Words: " + words + 
            " | Characters: " + chars + 
            " | Lines: " + lines
        );
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

// Simple FileHandler implementation
class FileHandlerImpl implements FileHandler {
    @Override
    public Document loadDocument(String filePath) {
        // Implement file loading
        return new Document(filePath);
    }
    
    @Override
    public boolean saveDocument(Document document) {
        // Implement file saving
        System.out.println("Saving document: " + document.getFilePath());
        return true;
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        return false;
    }
    
    @Override
    public boolean fileExists(String filePath) {
        return new java.io.File(filePath).exists();
    }
}

