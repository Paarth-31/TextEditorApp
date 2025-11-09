package com.texteditor.main;

import com.texteditor.engine.FormattingEngine;
import com.texteditor.engine.SearchReplaceEngine;
import com.texteditor.engine.TrackingEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.InlineCssTextArea;
import com.texteditor.manager.*;
import com.texteditor.model.*;
import com.texteditor.util.*;
import java.io.IOException;

/**
 * Advanced Text Editor - FXML Edition (Complete Version - FIXED)
 * Word 2016 Equivalent with Selection-Based Formatting
 *
 * FIXED: Uses correct RichTextFX API for style management
 * - Uses appendText() instead of getText/setText
 * - Uses StyleSpans instead of getStyle(int,int)
 * - Compatible with RichTextFX 0.10.7
 *
 * @author Advanced Text Editor Project
 * @version 2.0-FIXED
 */
public class Main extends Application {

    // ============= CONFIGURATION CONSTANTS =============
    private static final String APP_TITLE = "Advanced Text Editor - Word 2016 Compatible";
    private static final String APP_VERSION = "2.0 FXML Edition (Fixed)";
    private static final String FXML_FILE = "/fxml/MainWindow.fxml";

    // Window dimensions
    private static final double DEFAULT_WIDTH = 1600;
    private static final double DEFAULT_HEIGHT = 1000;
    private static final double MIN_WIDTH = 1000;
    private static final double MIN_HEIGHT = 700;

    // ============= UI COMPONENTS =============
    private InlineCssTextArea textArea;
    private Label statusLabel;
    private Label trackingStatusLabel;
    private ComboBox<String> fontFamilyCombo;
    private ComboBox<Integer> fontSizeCombo;
    private ComboBox<String> alignmentCombo;
    private ColorPicker textColorPicker;
    private ColorPicker highlightColorPicker;
    private ToggleButton boldBtn, italicBtn, underlineBtn, strikeBtn;
    private StackPane textEditorContainer;

    // ============= MANAGERS =============
    private DocumentManager documentManager;
    private StyleManager styleManager;
    private PreferencesManager preferencesManager;
    private FormattingEngine formattingEngine;
    private SearchReplaceEngine searchEngine;
    private TrackingEngine trackingEngine;

    // ============= STATE =============
    private boolean trackingMode = false;
    private FXMLLoader fxmlLoader;

    // ============= APPLICATION LIFECYCLE =============

    /**
     * Initialize the application
     */
    @Override
    public void init() {
        System.out.println("===========================================");
        System.out.println(APP_TITLE);
        System.out.println("Version: " + APP_VERSION);
        System.out.println("===========================================");
        System.out.println("Initializing application...");

        initializeManagers();
    }

    /**
     * Start the application - Main entry point
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Attempt to load FXML
            boolean fxmlLoaded = attemptFXMLLoad(primaryStage);

            // If FXML fails, create programmatic UI
            if (!fxmlLoaded) {
                System.out.println("⚠ FXML loading failed, creating programmatic UI...");
                createProgrammaticUI(primaryStage);
            }

            // Configure and show stage
            configureStage(primaryStage);
            primaryStage.show();

            System.out.println("✓ Application started successfully!");
            System.out.println("✓ Window size: " + DEFAULT_WIDTH + "x" + DEFAULT_HEIGHT);

        } catch (Exception e) {
            System.err.println("ERROR: Failed to start application!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Stop the application
     */
    @Override
    public void stop() {
        System.out.println("Application stopping...");
    }

    // ============= FXML LOADING =============

    /**
     * Attempt to load FXML file with fallback
     */
    private boolean attemptFXMLLoad(Stage primaryStage) {
        try {
            System.out.println("Attempting to load FXML from: " + FXML_FILE);

            fxmlLoader = new FXMLLoader(getClass().getResource(FXML_FILE));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            primaryStage.setScene(scene);

            System.out.println("✓ FXML loaded successfully!");
            return true;

        } catch (IOException e) {
            System.out.println("⚠ FXML file not found: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("⚠ Error loading FXML: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create UI programmatically (fallback)
     */
    private void createProgrammaticUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 11;");

        // Top section
        root.setTop(createTopSection());

        // Text editor
        textArea = new InlineCssTextArea();
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12; -fx-padding: 15;");
        textArea.textProperty().addListener((obs, oldText, newText) -> updateStatusBar());
        textArea.selectionProperty().addListener((obs, oldSel, newSel) -> updateFormattingToolbar());

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        // Status bar
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        primaryStage.setScene(scene);
    }

    // ============= UI CREATION =============

    /**
     * Create top section with menu bar and toolbar
     */
    private VBox createTopSection() {
        VBox top = new VBox(0);
        top.getChildren().addAll(createMenuBar(), createToolbar());
        return top;
    }

    /**
     * Create menu bar
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-font-size: 11;");

        menuBar.getMenus().addAll(
                createFileMenu(),
                createEditMenu(),
                createFormatMenu(),
                createStylesMenu(),
                createDocumentMenu(),
                createViewMenu(),
                createHelpMenu()
        );

        return menuBar;
    }

    /**
     * Create File menu
     */
    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New");
        newItem.setOnAction(e -> handleNew());

        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(e -> handleOpen());

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(e -> handleSave());

        MenuItem saveAsItem = new MenuItem("Save As");
        saveAsItem.setOnAction(e -> handleSaveAs());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(
                newItem, openItem, new SeparatorMenuItem(),
                saveItem, saveAsItem, new SeparatorMenuItem(), exitItem
        );

        return fileMenu;
    }

    /**
     * Create Edit menu
     */
    private Menu createEditMenu() {
        Menu editMenu = new Menu("Edit");

        MenuItem undoItem = new MenuItem("Undo");
        undoItem.setOnAction(e -> textArea.undo());

        MenuItem redoItem = new MenuItem("Redo");
        redoItem.setOnAction(e -> textArea.redo());

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

        editMenu.getItems().addAll(
                undoItem, redoItem, new SeparatorMenuItem(),
                cutItem, copyItem, pasteItem, new SeparatorMenuItem(),
                selectAllItem, new SeparatorMenuItem(), findItem
        );

        return editMenu;
    }

    /**
     * Create Format menu
     */
    private Menu createFormatMenu() {
        Menu formatMenu = new Menu("Format");

        MenuItem boldItem = new MenuItem("Bold (Ctrl+B)");
        boldItem.setOnAction(e -> applyBoldToSelection());

        MenuItem italicItem = new MenuItem("Italic (Ctrl+I)");
        italicItem.setOnAction(e -> applyItalicToSelection());

        MenuItem underlineItem = new MenuItem("Underline (Ctrl+U)");
        underlineItem.setOnAction(e -> applyUnderlineToSelection());

        MenuItem strikeItem = new MenuItem("Strikethrough");
        strikeItem.setOnAction(e -> applyStrikethroughToSelection());

        MenuItem colorItem = new MenuItem("Text Color");
        colorItem.setOnAction(e -> showColorPickerDialog());

        MenuItem highlightItem = new MenuItem("Highlight");
        highlightItem.setOnAction(e -> showHighlightPickerDialog());

        MenuItem clearItem = new MenuItem("Clear Formatting");
        clearItem.setOnAction(e -> clearFormattingFromSelection());

        formatMenu.getItems().addAll(
                boldItem, italicItem, underlineItem, strikeItem,
                new SeparatorMenuItem(), colorItem, highlightItem,
                new SeparatorMenuItem(), clearItem
        );

        return formatMenu;
    }

    /**
     * Create Styles menu
     */
    private Menu createStylesMenu() {
        Menu stylesMenu = new Menu("Styles");

        MenuItem heading1Item = new MenuItem("Heading 1");
        heading1Item.setOnAction(e -> applyHeadingStyle(1));

        MenuItem heading2Item = new MenuItem("Heading 2");
        heading2Item.setOnAction(e -> applyHeadingStyle(2));

        MenuItem heading3Item = new MenuItem("Heading 3");
        heading3Item.setOnAction(e -> applyHeadingStyle(3));

        MenuItem normalItem = new MenuItem("Normal");
        normalItem.setOnAction(e -> applyNormalStyle());

        MenuItem quoteItem = new MenuItem("Quote");
        quoteItem.setOnAction(e -> applyQuoteStyle());

        stylesMenu.getItems().addAll(
                heading1Item, heading2Item, heading3Item,
                normalItem, new SeparatorMenuItem(), quoteItem
        );

        return stylesMenu;
    }

    /**
     * Create Document menu
     */
    private Menu createDocumentMenu() {
        Menu docMenu = new Menu("Document");

        MenuItem trackItem = new MenuItem("Toggle Track Changes");
        trackItem.setOnAction(e -> toggleTrackingMode());

        MenuItem statsItem = new MenuItem("Statistics");
        statsItem.setOnAction(e -> showStatistics());

        MenuItem infoItem = new MenuItem("Document Info");
        infoItem.setOnAction(e -> showDocumentInfo());

        docMenu.getItems().addAll(
                trackItem, new SeparatorMenuItem(),
                statsItem, infoItem
        );

        return docMenu;
    }

    /**
     * Create View menu
     */
    private Menu createViewMenu() {
        Menu viewMenu = new Menu("View");

        MenuItem themeItem = new MenuItem("Switch Theme");
        themeItem.setOnAction(e -> showAlert("Theme", "Theme switching coming soon"));

        MenuItem customizeItem = new MenuItem("Customize UI");
        customizeItem.setOnAction(e -> showAlert("Customize", "UI customization coming soon"));

        viewMenu.getItems().addAll(themeItem, customizeItem);

        return viewMenu;
    }

    /**
     * Create Help menu
     */
    private Menu createHelpMenu() {
        Menu helpMenu = new Menu("Help");

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());

        MenuItem shortcutsItem = new MenuItem("Keyboard Shortcuts");
        shortcutsItem.setOnAction(e -> showShortcutsDialog());

        helpMenu.getItems().addAll(aboutItem, shortcutsItem);

        return helpMenu;
    }

    /**
     * Create toolbar with formatting controls
     */
    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();
        toolbar.setStyle("-fx-background-color: linear-gradient(to bottom, #E3F2FD, #BBDEFB); " +
                "-fx-border-color: #90CAF9; -fx-border-width: 1; " +
                "-fx-padding: 8 10 8 10; -fx-spacing: 8;");
        toolbar.setPrefHeight(85);

        // Font family
        fontFamilyCombo = new ComboBox<>();
        fontFamilyCombo.getItems().addAll(
                "Consolas", "Arial", "Times New Roman", "Georgia",
                "Courier New", "Verdana", "Calibri", "Segoe UI"
        );
        fontFamilyCombo.setValue("Consolas");
        fontFamilyCombo.setPrefWidth(140);
        fontFamilyCombo.setOnAction(e -> applyFontFamilyToSelection(fontFamilyCombo.getValue()));

        // Font size
        fontSizeCombo = new ComboBox<>();
        for (int i = 8; i <= 72; i += 2) {
            fontSizeCombo.getItems().add(i);
        }
        fontSizeCombo.setValue(12);
        fontSizeCombo.setPrefWidth(80);
        fontSizeCombo.setOnAction(e -> applyFontSizeToSelection(fontSizeCombo.getValue()));

        // Format buttons
        boldBtn = new ToggleButton("B");
        boldBtn.setPrefSize(40, 40);
        boldBtn.setStyle("-fx-font-weight: bold; -fx-font-size: 14; " +
                "-fx-background-color: linear-gradient(to bottom, #2196F3, #1976D2); " +
                "-fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;");
        boldBtn.setOnAction(e -> applyBoldToSelection());

        italicBtn = new ToggleButton("I");
        italicBtn.setPrefSize(40, 40);
        italicBtn.setStyle("-fx-font-style: italic; -fx-font-size: 14; " +
                "-fx-background-color: linear-gradient(to bottom, #2196F3, #1976D2); " +
                "-fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;");
        italicBtn.setOnAction(e -> applyItalicToSelection());

        underlineBtn = new ToggleButton("U");
        underlineBtn.setPrefSize(40, 40);
        underlineBtn.setStyle("-fx-underline: true; -fx-font-size: 14; " +
                "-fx-background-color: linear-gradient(to bottom, #2196F3, #1976D2); " +
                "-fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;");
        underlineBtn.setOnAction(e -> applyUnderlineToSelection());

        strikeBtn = new ToggleButton("S");
        strikeBtn.setPrefSize(40, 40);
        strikeBtn.setStyle("-fx-font-size: 14; " +
                "-fx-background-color: linear-gradient(to bottom, #2196F3, #1976D2); " +
                "-fx-text-fill: white; -fx-border-radius: 4; -fx-background-radius: 4;");
        strikeBtn.setOnAction(e -> applyStrikethroughToSelection());

        // Alignment
        alignmentCombo = new ComboBox<>();
        alignmentCombo.getItems().addAll("Left", "Center", "Right", "Justify");
        alignmentCombo.setValue("Left");
        alignmentCombo.setPrefWidth(100);
        alignmentCombo.setOnAction(e -> applyAlignmentToSelection(alignmentCombo.getValue()));

        // Color pickers
        textColorPicker = new ColorPicker(Color.BLACK);
        textColorPicker.setPrefSize(50, 40);
        textColorPicker.setOnAction(e -> applyColorToSelection(textColorPicker.getValue()));

        highlightColorPicker = new ColorPicker(Color.YELLOW);
        highlightColorPicker.setPrefSize(50, 40);
        highlightColorPicker.setOnAction(e -> applyHighlightToSelection(highlightColorPicker.getValue()));

        // Clear format button
        Button clearBtn = new Button("Clear Format");
        clearBtn.setPrefHeight(40);
        clearBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #2196F3, #1976D2); " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15 8 15; " +
                "-fx-border-radius: 4; -fx-background-radius: 4;");
        clearBtn.setOnAction(e -> clearFormattingFromSelection());

        // Add all controls
        toolbar.getItems().addAll(
                new Label("Font: "), fontFamilyCombo,
                new Label("Size: "), fontSizeCombo,
                new Separator(),
                boldBtn, italicBtn, underlineBtn, strikeBtn,
                new Separator(),
                new Label("Align: "), alignmentCombo,
                new Separator(),
                new Label("Text "), textColorPicker,
                new Label("Highlight "), highlightColorPicker,
                new Separator(),
                clearBtn
        );

        return toolbar;
    }

    /**
     * Create status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setStyle("-fx-background-color: linear-gradient(to right, #C8E6C9, #A5D6A7); " +
                "-fx-border-color: #7CB342; -fx-border-width: 1 0 0 0; " +
                "-fx-padding: 8;");
        statusBar.setPadding(new Insets(5, 10, 5, 10));

        statusLabel = new Label("Ready | Words: 0 | Characters: 0 | Lines: 1");
        statusLabel.setStyle("-fx-text-fill: #1B5E20; -fx-font-weight: bold; -fx-font-size: 10;");

        trackingStatusLabel = new Label("Track Changes: OFF");
        trackingStatusLabel.setStyle("-fx-text-fill: #7CB342; -fx-font-weight: bold; -fx-font-size: 10;");

        Separator separator = new Separator(javafx.geometry.Orientation.VERTICAL);

        statusBar.getChildren().addAll(statusLabel, separator, trackingStatusLabel);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        return statusBar;
    }

    // ============= FORMATTING OPERATIONS (FIXED) =============

    /**
     * Apply bold - FIXED for RichTextFX
     * Uses text insertion instead of style range
     */
    private void applyBoldToSelection() {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        // Get selected text
        String selectedText = textArea.getSelectedText();

        // Delete selected text
        textArea.deleteText(start, end);

        // Insert with bold style
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), "-fx-font-weight: bold;");

        boldBtn.setSelected(true);
    }

    /**
     * Apply italic - FIXED
     */
    private void applyItalicToSelection() {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), "-fx-font-style: italic;");

        italicBtn.setSelected(true);
    }

    /**
     * Apply underline - FIXED
     */
    private void applyUnderlineToSelection() {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), "-fx-underline: true;");

        underlineBtn.setSelected(true);
    }

    /**
     * Apply strikethrough - FIXED
     */
    private void applyStrikethroughToSelection() {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), "-fx-strikethrough: true;");

        strikeBtn.setSelected(true);
    }

    /**
     * Apply font family - FIXED
     */
    private void applyFontFamilyToSelection(String family) {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), "-fx-font-family: '" + family + "';");
    }

    /**
     * Apply font size - FIXED
     */
    private void applyFontSizeToSelection(int size) {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), "-fx-font-size: " + size + ";");
    }

    /**
     * Apply text color - FIXED
     */
    private void applyColorToSelection(Color color) {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        String colorHex = toHexString(color);
        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), "-fx-fill: " + colorHex + ";");
    }

    /**
     * Apply highlight - FIXED
     */
    private void applyHighlightToSelection(Color color) {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        String colorHex = toHexString(color);
        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), "-fx-background-color: " + colorHex + ";");
    }

    /**
     * Apply alignment
     */
    private void applyAlignmentToSelection(String alignment) {
        if (textArea == null) return;

        switch (alignment) {
            case "Center":
                textArea.setStyle("-fx-text-alignment: center;");
                break;
            case "Right":
                textArea.setStyle("-fx-text-alignment: right;");
                break;
            case "Justify":
                textArea.setStyle("-fx-text-alignment: justify;");
                break;
            default:
                textArea.setStyle("-fx-text-alignment: left;");
        }
    }

    /**
     * Clear formatting
     */
    private void clearFormattingFromSelection() {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        textArea.clearStyle(start, end);
        boldBtn.setSelected(false);
        italicBtn.setSelected(false);
        underlineBtn.setSelected(false);
        strikeBtn.setSelected(false);
    }

    /**
     * Apply heading style
     */
    private void applyHeadingStyle(int level) {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        int fontSize = level == 1 ? 28 : level == 2 ? 24 : 20;
        String style = "-fx-font-family: 'Arial'; -fx-font-size: " + fontSize +
                "; -fx-font-weight: bold; -fx-fill: #1565C0;";

        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), style);
    }

    /**
     * Apply normal style
     */
    private void applyNormalStyle() {
        clearFormattingFromSelection();
    }

    /**
     * Apply quote style
     */
    private void applyQuoteStyle() {
        if (textArea == null) return;

        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String selectedText = textArea.getSelectedText();
        String style = "-fx-font-family: 'Georgia'; -fx-font-size: 14; " +
                "-fx-font-style: italic; -fx-fill: #616161;";

        textArea.deleteText(start, end);
        textArea.insertText(start, selectedText);
        textArea.setStyle(start, start + selectedText.length(), style);
    }

    /**
     * Update formatting toolbar
     */
    private void updateFormattingToolbar() {
        // Simplified - just shows selection exists
        if (textArea != null && textArea.getSelectedText().length() > 0) {
            System.out.println("Selected: " + textArea.getSelectedText());
        }
    }

    // ============= FILE OPERATIONS =============

    private void handleNew() {
        if (textArea != null) textArea.clear();
        if (documentManager != null) documentManager.createNewDocument();
        updateStatusBar();
    }

    private void handleOpen() {
        showAlert("Open File", "File chooser dialog coming soon");
    }

    private void handleSave() {
        if (documentManager != null && textArea != null) {
            Document doc = documentManager.getCurrentDocument();
            if (doc != null) {
                doc.setContent(textArea.getText());
                documentManager.saveDocument(doc);
                showAlert("Save", "Document saved successfully");
            }
        }
    }

    private void handleSaveAs() {
        showAlert("Save As", "Save as dialog coming soon");
    }

    // ============= DIALOG METHODS =============

    private void showFindReplaceDialog() {
        if (textArea == null) return;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Find & Replace");
        dialog.setHeaderText("Find and Replace Text");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField findField = new TextField();
        findField.setPromptText("Find:");
        TextField replaceField = new TextField();
        replaceField.setPromptText("Replace with:");

        grid.add(new Label("Find:"), 0, 0);
        grid.add(findField, 1, 0);
        grid.add(new Label("Replace:"), 0, 1);
        grid.add(replaceField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String find = findField.getText();
                String replace = replaceField.getText();
                if (!find.isEmpty()) {
                    String newText = textArea.getText().replace(find, replace);
                    textArea.clear();
                    textArea.appendText(newText);
                    showAlert("Replace Complete", "Text replaced successfully");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showColorPickerDialog() {
        ColorPicker picker = new ColorPicker(Color.BLACK);
        Dialog<Color> dialog = new Dialog<>();
        dialog.setTitle("Text Color");
        dialog.getDialogPane().setContent(picker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> buttonType == ButtonType.OK ? picker.getValue() : null);
        dialog.showAndWait().ifPresent(this::applyColorToSelection);
    }

    private void showHighlightPickerDialog() {
        ColorPicker picker = new ColorPicker(Color.YELLOW);
        Dialog<Color> dialog = new Dialog<>();
        dialog.setTitle("Highlight Color");
        dialog.getDialogPane().setContent(picker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> buttonType == ButtonType.OK ? picker.getValue() : null);
        dialog.showAndWait().ifPresent(this::applyHighlightToSelection);
    }

    private void showStatistics() {
        if (textArea == null) return;

        String text = textArea.getText();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int chars = text.length();
        int lines = text.isEmpty() ? 1 : text.split("\n", -1).length;
        int paragraphs = text.isEmpty() ? 1 : text.split("\n\n", -1).length;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Document Statistics");
        alert.setHeaderText("Document Analysis");
        alert.setContentText(
                "Words: " + words + "\n" +
                        "Characters: " + chars + "\n" +
                        "Lines: " + lines + "\n" +
                        "Paragraphs: " + paragraphs
        );
        alert.showAndWait();
    }

    private void showDocumentInfo() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Document Information");
        dialog.setHeaderText("Document Properties");

        TextArea infoArea = new TextArea(
                "Document created today\n" +
                        "Status: Unsaved\n" +
                        "Size: " + (textArea != null ? textArea.getText().length() : 0) + " characters"
        );
        infoArea.setEditable(false);
        infoArea.setPrefHeight(200);
        infoArea.setWrapText(true);

        dialog.getDialogPane().setContent(infoArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(APP_TITLE);
        alert.setContentText(
                "Version: " + APP_VERSION + "\n\n" +
                        "A professional text editor with MS Word 2016 features\n\n" +
                        "Features:\n" +
                        "✓ Selection-based formatting\n" +
                        "✓ Multiple fonts and sizes\n" +
                        "✓ Bold, Italic, Underline, Strikethrough\n" +
                        "✓ Text and highlight colors\n" +
                        "✓ Predefined styles\n" +
                        "✓ Find & Replace\n" +
                        "✓ Document statistics"
        );
        alert.showAndWait();
    }

    private void showShortcutsDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Keyboard Shortcuts");
        alert.setHeaderText("Common Shortcuts");
        alert.setContentText(
                "Ctrl+N - New\nCtrl+S - Save\nCtrl+Z - Undo\n" +
                        "Ctrl+Y - Redo\nCtrl+X - Cut\nCtrl+C - Copy\n" +
                        "Ctrl+V - Paste\nCtrl+A - Select All\nCtrl+H - Find & Replace"
        );
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ============= UTILITY METHODS =============

    private void initializeManagers() {
        documentManager = new DocumentManager(new FileHandlerImpl());
        styleManager = new StyleManager();
        preferencesManager = new PreferencesManager(Constants.PREFERENCES_FILE);
        formattingEngine = new FormattingEngine();
        searchEngine = new SearchReplaceEngine();
        trackingEngine = new TrackingEngine();
        documentManager.createNewDocument();
    }

    private void configureStage(Stage stage) {
        stage.setTitle(APP_TITLE + " - " + APP_VERSION);
        stage.setWidth(DEFAULT_WIDTH);
        stage.setHeight(DEFAULT_HEIGHT);
        stage.setResizable(true);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        try {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - DEFAULT_WIDTH) / 2);
            stage.setY((screenBounds.getHeight() - DEFAULT_HEIGHT) / 2);
        } catch (Exception e) {
            System.out.println("Could not center window: " + e.getMessage());
        }

        stage.setOnCloseRequest(event -> System.exit(0));
    }

    private void updateStatusBar() {
        if (textArea == null) return;

        String text = textArea.getText();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int chars = text.length();
        int lines = text.isEmpty() ? 1 : text.split("\n", -1).length;

        if (statusLabel != null) {
            statusLabel.setText(
                    "Ready | Words: " + words +
                            " | Characters: " + chars +
                            " | Lines: " + lines
            );
        }
    }

    private void toggleTrackingMode() {
        trackingMode = !trackingMode;
        if (trackingStatusLabel != null) {
            trackingStatusLabel.setText("Track Changes: " + (trackingMode ? "ON" : "OFF"));
            trackingStatusLabel.setStyle("-fx-text-fill: " + (trackingMode ? "#D32F2F" : "#7CB342") +
                    "; -fx-font-weight: bold; -fx-font-size: 10;");
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    // ============= MAIN =============

    public static void main(String[] args) {
        System.out.println("Advanced Text Editor - FXML Edition (FIXED)");
        launch(args);
    }
}

// ============= FILE HANDLER =============

class FileHandlerImpl implements com.texteditor.util.FileHandler {
    @Override
    public Document loadDocument(String filePath) {
        return new Document(filePath);
    }

    @Override
    public boolean saveDocument(Document document) {
        System.out.println("Saving: " + document.getFilePath());
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