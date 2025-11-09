package com.texteditor.model;

import java.io.Serializable;

/**
 * Represents a UI theme with customizable colors and settings.
 */
public class Theme implements Serializable {
	private static final long serialVersionUID = 1L;
    
	private String name;
	private String backgroundColor;
	private String textColor;
	private String accentColor;
	private String menuBarColor;
	private String scrollbarColor;
	private String borderColor;
	private int menuBarHeight;
	private int scrollbarWidth;
	private int fontScale;
    
    // Constructors
	public Theme() {
	        this.name = "Light";
        	this.backgroundColor = "#F5F5F5";
	        this.textColor = "#333333";
        	this.accentColor = "#2196F3";
	        this.menuBarColor = "#FAFAFA";
        	this.scrollbarColor = "#CCCCCC";
	        this.borderColor = "#CCCCCC";
        	this.menuBarHeight = 40;
	        this.scrollbarWidth = 15;
        	this.fontScale = 100;
	}
    
	public Theme(String name, String background, String text, String accent) {
        	this();
	        this.name = name;
        	this.backgroundColor = background;
	        this.textColor = text;
        	this.accentColor = accent;
	}
    
    // Getters and Setters
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
    
	public String getBackgroundColor() { return backgroundColor; }
	public void setBackgroundColor(String color) { this.backgroundColor = color; }
    
	public String getTextColor() { return textColor; }
	public void setTextColor(String color) { this.textColor = color; }
    
	public String getAccentColor() { return accentColor; }
	public void setAccentColor(String color) { this.accentColor = color; }
    
	public String getMenuBarColor() { return menuBarColor; }
	public void setMenuBarColor(String color) { this.menuBarColor = color; }
    
	public String getScrollbarColor() { return scrollbarColor; }
	public void setScrollbarColor(String color) { this.scrollbarColor = color; }
    
	public String getBorderColor() { return borderColor; }
	public void setBorderColor(String color) { this.borderColor = color; }
    
	public int getMenuBarHeight() { return menuBarHeight; }
	public void setMenuBarHeight(int height) { this.menuBarHeight = height; }
    
	public int getScrollbarWidth() { return scrollbarWidth; }
	public void setScrollbarWidth(int width) { this.scrollbarWidth = width; }
    
	public int getFontScale() { return fontScale; }
	public void setFontScale(int scale) { this.fontScale = scale; }
    
    // CSS generation for JavaFX
	public String generateCSS() {
        	return String.format(
        			".root { -fx-base: %s; -fx-control-inner-background: %s; } " + ".text-input { -fx-text-fill: %s; -fx-control-inner-background: %s; } " + ".menu-bar { -fx-base: %s; -fx-padding: %dpx; } " + ".scroll-bar { -fx-padding: %dpx; }", backgroundColor, backgroundColor, textColor, backgroundColor, menuBarColor, menuBarHeight, scrollbarWidth
        			);
        }
}
