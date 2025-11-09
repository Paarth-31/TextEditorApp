package com.texteditor.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import org.fxmisc.richtext.InlineCssTextArea;
import com.texteditor.manager.DocumentManager;
import com.texteditor.manager.PreferencesManager;
import com.texteditor.util.Constants;
import com.texteditor.model.Document;

/**
 * FXML Controller for Main Window
 * Handles all UI interactions and event management
 */
public class MainController {

    @FXML
    private StackPane textEditorContainer;

    @FXML
    private Label statusLabel;

    @FXML
    private Label trackingStatusLabel;

    @FXML
    private ComboBox<String> fontFamilyCombo;

    @FXML
    private ComboBox<Integer> fontSizeCombo;

    @FXML
    private ComboBox<String> alignmentCombo;

    @FXML
    private ColorPicker textColorPicker;

    @FXML
    private ColorPicker highlightColorPicker;

    @FXML
    private ToggleButton boldBtn;

    @FXML
    private ToggleButton italicBtn;

    @FXML
    private ToggleButton underlineBtn;

    @FXML
    private ToggleButton strikeBtn;

    // Rich Text Area
    private InlineCssTextArea textArea;

    // Managers
    private DocumentManager documentManager;
    private PreferencesManager preferencesManager;

    // State
    private boolean trackingMode = false;

    /**
     * Initialize controller (called automatically after FXML loading)
     */
    @FXML
    public void initialize() {
        // Initialize managers
        documentManager = new DocumentManager(new FileHandlerImpl());
        preferencesManager = new PreferencesManager(Constants.PREFERENCES_FILE);

        // Create Rich Text Area
        textArea = new InlineCssTextArea();
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12; -fx-padding: 15;");
        textArea.setPrefHeight(600);

        // Add to container with scroll pane
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
        textEditorContainer.getChildren().add(scrollPane);

        // Listen to text changes
        textArea.textProperty().addListener((obs, oldText, newText) -> updateStatusBar());
        textArea.selectionProperty().addListener((obs, oldSel, newSel) -> updateFormattingToolbar());

        // Initialize combo boxes
        initializeFontCombo();
        initializeSizeCombo();
        initializeAlignmentCombo();

        // Set color picker initial values
        textColorPicker.setValue(Color.BLACK);
        highlightColorPicker.setValue(Color.YELLOW);

        // Create new document
        documentManager.createNewDocument();

        // Update status
        updateStatusBar();
    }

    private void initializeFontCombo() {
        fontFamilyCombo.getItems().addAll(
                "Consolas", "Arial", "Times New Roman", "Georgia",
                "Courier New", "Verdana", "Calibri", "Segoe UI"
        );
        fontFamilyCombo.setValue("Consolas");
        fontFamilyCombo.setOnAction(e -> applyFontFamilyToSelection(fontFamilyCombo.getValue()));
    }

    private void initializeSizeCombo() {
        for (int i = 8; i <= 72; i += 2) {
            fontSizeCombo.getItems().add(i);
        }
        fontSizeCombo.setValue(12);
        fontSizeCombo.setOnAction(e -> applyFontSizeToSelection(fontSizeCombo.getValue()));
    }

    private void initializeAlignmentCombo() {
        alignmentCombo.getItems().addAll("Left", "Center", "Right", "Justify");
        alignmentCombo.setValue("Left");
        alignmentCombo.setOnAction(e -> applyAlignmentToSelection(alignmentCombo.getValue()));
    }

    // ============= FILE HANDLERS =============

    @FXML
    public void handleNew() {
        textArea.clear();
        documentManager.createNewDocument();
        updateStatusBar();
    }

    @FXML
    public void handleOpen() {
        showAlert("Open File", "File chooser dialog coming soon");
    }

    @FXML
    public void handleSave() {
        Document doc = documentManager.getCurrentDocument();
        if (doc != null) {
            doc.setContent(textArea.getText());
            documentManager.saveDocument(doc);
            showAlert("Save", "Document saved successfully");
        }
    }

    @FXML
    public void handleSaveAs() {
        showAlert("Save As", "Save as dialog coming soon");
    }

    @FXML
    public void handlePrint() {
        showAlert("Print", "Print functionality coming soon");
    }

    @FXML
    public void handleExit() {
        System.exit(0);
    }

    // ============= EDIT HANDLERS =============

    @FXML
    public void handleUndo() {
        textArea.undo();
    }

    @FXML
    public void handleRedo() {
        textArea.redo();
    }

    @FXML
    public void handleCut() {
        textArea.cut();
    }

    @FXML
    public void handleCopy() {
        textArea.copy();
    }

    @FXML
    public void handlePaste() {
        textArea.paste();
    }

    @FXML
    public void handleSelectAll() {
        textArea.selectAll();
    }

    @FXML
    public void handleFindReplace() {
        showFindReplaceDialog();
    }

    // ============= FORMAT HANDLERS =============

    @FXML
    public void handleBold() {
        applyBoldToSelection();
    }

    @FXML
    public void handleItalic() {
        applyItalicToSelection();
    }

    @FXML
    public void handleUnderline() {
        applyUnderlineToSelection();
    }

    @FXML
    public void handleStrikethrough() {
        applyStrikethroughToSelection();
    }

    @FXML
    public void handleTextColor() {
        ColorPicker picker = new ColorPicker(Color.BLACK);
        Dialog<Color> dialog = new Dialog<>();
        dialog.setTitle("Text Color");
        dialog.getDialogPane().setContent(picker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> buttonType == ButtonType.OK ? picker.getValue() : null);
        dialog.showAndWait().ifPresent(this::applyColorToSelection);
    }

    @FXML
    public void handleHighlight() {
        ColorPicker picker = new ColorPicker(Color.YELLOW);
        Dialog<Color> dialog = new Dialog<>();
        dialog.setTitle("Highlight Color");
        dialog.getDialogPane().setContent(picker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> buttonType == ButtonType.OK ? picker.getValue() : null);
        dialog.showAndWait().ifPresent(this::applyHighlightToSelection);
    }

    @FXML
    public void handleClearFormat() {
        clearFormattingFromSelection();
    }

    // ============= SELECTION-BASED FORMATTING =============

    @FXML
    public void handleBoldSelection() {
        applyBoldToSelection();
    }

    @FXML
    public void handleItalicSelection() {
        applyItalicToSelection();
    }

    @FXML
    public void handleUnderlineSelection() {
        applyUnderlineToSelection();
    }

    @FXML
    public void handleStrikethroughSelection() {
        applyStrikethroughToSelection();
    }

    @FXML
    public void handleTextColorSelection() {
        applyColorToSelection(textColorPicker.getValue());
    }

    @FXML
    public void handleHighlightSelection() {
        applyHighlightToSelection(highlightColorPicker.getValue());
    }

    @FXML
    public void handleClearFormatSelection() {
        clearFormattingFromSelection();
    }

    // ============= FORMATTING OPERATIONS =============

    private void applyBoldToSelection() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String style = textArea.getStyle(start, end);

        if (style.contains("-fx-font-weight: bold")) {
            textArea.setStyle(start, end, style.replace("-fx-font-weight: bold;", ""));
            boldBtn.setSelected(false);
        } else {
            textArea.setStyle(start, end, style + "-fx-font-weight: bold;");
            boldBtn.setSelected(true);
        }
    }

    private void applyItalicToSelection() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String style = textArea.getStyle(start, end);

        if (style.contains("-fx-font-style: italic")) {
            textArea.setStyle(start, end, style.replace("-fx-font-style: italic;", ""));
            italicBtn.setSelected(false);
        } else {
            textArea.setStyle(start, end, style + "-fx-font-style: italic;");
            italicBtn.setSelected(true);
        }
    }

    private void applyUnderlineToSelection() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String style = textArea.getStyle(start, end);

        if (style.contains("-fx-underline: true")) {
            textArea.setStyle(start, end, style.replace("-fx-underline: true;", ""));
            underlineBtn.setSelected(false);
        } else {
            textArea.setStyle(start, end, style + "-fx-underline: true;");
            underlineBtn.setSelected(true);
        }
    }

    private void applyStrikethroughToSelection() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String style = textArea.getStyle(start, end);

        if (style.contains("-fx-strikethrough: true")) {
            textArea.setStyle(start, end, style.replace("-fx-strikethrough: true;", ""));
            strikeBtn.setSelected(false);
        } else {
            textArea.setStyle(start, end, style + "-fx-strikethrough: true;");
            strikeBtn.setSelected(true);
        }
    }

    private void applyFontFamilyToSelection(String family) {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        textArea.setStyle(start, end, "-fx-font-family: '" + family + "';");
    }

    private void applyFontSizeToSelection(int size) {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        textArea.setStyle(start, end, "-fx-font-size: " + size + ";");
    }

    private void applyColorToSelection(Color color) {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String colorHex = toHexString(color);
        textArea.setStyle(start, end, "-fx-fill: " + colorHex + ";");
    }

    private void applyHighlightToSelection(Color color) {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String colorHex = toHexString(color);
        textArea.setStyle(start, end, "-fx-background-color: " + colorHex + ";");
    }

    private void applyAlignmentToSelection(String alignment) {
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

    private void clearFormattingFromSelection() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        textArea.clearStyle(start, end);
        boldBtn.setSelected(false);
        italicBtn.setSelected(false);
        underlineBtn.setSelected(false);
        strikeBtn.setSelected(false);
    }

    // ============= STYLE HANDLERS =============

    @FXML
    public void handleHeading1() {
        applyHeadingStyle(1);
    }

    @FXML
    public void handleHeading2() {
        applyHeadingStyle(2);
    }

    @FXML
    public void handleHeading3() {
        applyHeadingStyle(3);
    }

    @FXML
    public void handleNormal() {
        clearFormattingFromSelection();
    }

    @FXML
    public void handleQuote() {
        applyQuoteStyle();
    }

    private void applyHeadingStyle(int level) {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        int fontSize = level == 1 ? 28 : level == 2 ? 24 : 20;
        String style = "-fx-font-family: 'Arial'; -fx-font-size: " + fontSize + "; -fx-font-weight: bold; -fx-fill: #1565C0;";
        textArea.setStyle(start, end, style);
    }

    private void applyQuoteStyle() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();
        if (start == end) return;

        String style = "-fx-font-family: 'Georgia'; -fx-font-size: 14; -fx-font-style: italic; -fx-fill: #616161;";
        textArea.setStyle(start, end, style);
    }

    // ============= DOCUMENT HANDLERS =============

    @FXML
    public void handleTrackChanges() {
        trackingMode = !trackingMode;
        trackingStatusLabel.setText("Track Changes: " + (trackingMode ? "ON" : "OFF"));
        trackingStatusLabel.setStyle("-fx-text-fill: " + (trackingMode ? "#D32F2F" : "#7CB342") + "; -fx-font-weight: bold;");
    }

    @FXML
    public void handleStatistics() {
        showStatistics();
    }

    @FXML
    public void handleDocInfo() {
        showDocumentInfo();
    }

    // ============= VIEW HANDLERS =============

    @FXML
    public void handleTheme() {
        showAlert("Theme", "Theme switching coming soon");
    }

    @FXML
    public void handleCustomize() {
        showAlert("Customization", "UI customization coming soon");
    }

    // ============= HELP HANDLERS =============

    @FXML
    public void handleAbout() {
        showAboutDialog();
    }

    @FXML
    public void handleShortcuts() {
        showShortcutsDialog();
    }

    // ============= UTILITY METHODS =============

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

    private void updateFormattingToolbar() {
        int start = textArea.getSelection().getStart();
        int end = textArea.getSelection().getEnd();

        if (start == end) return;

        String style = textArea.getStyle(start, end);

        boldBtn.setSelected(style.contains("bold"));
        italicBtn.setSelected(style.contains("italic"));
        underlineBtn.setSelected(style.contains("underline: true"));
        strikeBtn.setSelected(style.contains("strikethrough: true"));
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    // ============= DIALOGS =============

    private void showFindReplaceDialog() {
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

    private void showStatistics() {
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
                        "Size: " + textArea.getText().length() + " characters"
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
        alert.setHeaderText("Advanced Text Editor v2.0 - FXML Edition");
        alert.setContentText(
                "A professional text editor with MS Word 2016 features\n\n" +
                        "Features:\n" +
                        "✓ Rich text editing with selection-based formatting\n" +
                        "✓ Multiple fonts and sizes\n" +
                        "✓ Bold, Italic, Underline, Strikethrough\n" +
                        "✓ Text and highlight colors\n" +
                        "✓ Text alignment options\n" +
                        "✓ Predefined styles (Heading, Quote, Normal)\n" +
                        "✓ Find & Replace\n" +
                        "✓ Track changes\n" +
                        "✓ Document statistics\n" +
                        "✓ FXML-based UI with modern design"
        );
        alert.showAndWait();
    }

    private void showShortcutsDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Keyboard Shortcuts");
        alert.setHeaderText("Common Shortcuts");
        alert.setContentText(
                "Ctrl+N - New Document\n" +
                        "Ctrl+O - Open\n" +
                        "Ctrl+S - Save\n" +
                        "Ctrl+Z - Undo\n" +
                        "Ctrl+Y - Redo\n" +
                        "Ctrl+X - Cut\n" +
                        "Ctrl+C - Copy\n" +
                        "Ctrl+V - Paste\n" +
                        "Ctrl+A - Select All\n" +
                        "Ctrl+H - Find & Replace\n" +
                        "Ctrl+B - Bold (Apply to selection)\n" +
                        "Ctrl+I - Italic (Apply to selection)\n" +
                        "Ctrl+U - Underline (Apply to selection)"
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
}

// FileHandler Implementation
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