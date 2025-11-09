package com.texteditor.model;

import javafx.scene.paint.Color;
import java.io.Serializable;

/**
 * Represents a text style with font properties and formatting options.
 * Uses composition to manage formatting attributes.
 */
public class Style implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String fontFamily;
    private int fontSize;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean strikethrough;
    private String textColor;
    private String backgroundColor;
    private TextAlignment alignment;
    private double lineSpacing;
    private int indentLevel;
    private String styleType; // "Heading1", "Body", "Quote", etc.

    // Enum for text alignment
    public enum TextAlignment {
        LEFT, CENTER, RIGHT, JUSTIFY
    }

    // Constructors
    public Style() {
        this.name = "Normal";
        this.fontFamily = "Consolas";
        this.fontSize = 12;
        this.bold = false;
        this.italic = false;
        this.underline = false;
        this.strikethrough = false;
        this.textColor = "#000000";
        this.backgroundColor = "#FFFFFF";
        this.alignment = TextAlignment.LEFT;
        this.lineSpacing = 1.0;
        this.indentLevel = 0;
        this.styleType = "Normal";
    }

    public Style(String name, String fontFamily, int fontSize) {
        this();
        this.name = name;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }

    public boolean isBold() { return bold; }
    public void setBold(boolean bold) { this.bold = bold; }

    public boolean isItalic() { return italic; }
    public void setItalic(boolean italic) { this.italic = italic; }

    public boolean isUnderline() { return underline; }
    public void setUnderline(boolean underline) { this.underline = underline; }

    public boolean isStrikethrough() { return strikethrough; }
    public void setStrikethrough(boolean strikethrough) { this.strikethrough = strikethrough; }

    public String getTextColor() { return textColor; }
    public void setTextColor(String color) { this.textColor = color; }

    public String getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String color) { this.backgroundColor = color; }

    public TextAlignment getAlignment() { return alignment; }
    public void setAlignment(TextAlignment alignment) { this.alignment = alignment; }

    public double getLineSpacing() { return lineSpacing; }
    public void setLineSpacing(double spacing) { this.lineSpacing = spacing; }

    public int getIndentLevel() { return indentLevel; }
    public void setIndentLevel(int level) { this.indentLevel = level; }

    public String getStyleType() { return styleType; }
    public void setStyleType(String type) { this.styleType = type; }

    // CSS String generation for JavaFX
    public String toCSS() {
        StringBuilder css = new StringBuilder();
        css.append("-fx-font-family: '").append(fontFamily).append("'; ");
        css.append("-fx-font-size: ").append(fontSize).append("pt; ");

        if (bold) css.append("-fx-font-weight: bold; ");
        if (italic) css.append("-fx-font-style: italic; ");

        css.append("-fx-text-fill: ").append(textColor).append("; ");
        css.append("-fx-background-color: ").append(backgroundColor).append("; ");

        return css.toString();
    }

    @Override
    public String toString() {
        return "Style{" +
                "name='" + name + '\'' +
                ", fontFamily='" + fontFamily + '\'' +
                ", fontSize=" + fontSize +
                ", bold=" + bold +
                ", italic=" + italic +
                ", styleType='" + styleType + '\'' +
                '}';
    }
}
