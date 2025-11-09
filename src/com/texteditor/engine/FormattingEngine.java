package com.texteditor.engine;

import com.texteditor.model.Style;

/**
 * Handles all text formatting operations.
 */
public class FormattingEngine {
	public String applyBold(String text) { return "<b>" + text + "</b>";}
	public String applyItalic(String text) { return "<i>" + text + "</i>";}
	public String applyUnderline(String text) { return "<u>" + text + "</u>";}
	public String applyStrikethrough(String text) { return "<s>" + text + "</s>";}
    
	public String applyColor(String text, String color) { return String.format("<span style='color:%s'>%s</span>", color, text);}
	public String applyHighlight(String text, String backgroundColor) { return String.format("<span style='background-color:%s'>%s</span>", backgroundColor, text);}
    
	public String applyStyle(String text, Style style) {
		StringBuilder result = new StringBuilder(text);
		if (style.isBold()) { result = new StringBuilder(applyBold(result.toString()));}
		if (style.isItalic()) { result = new StringBuilder(applyItalic(result.toString()));}
		if (style.isUnderline()) { result = new StringBuilder(applyUnderline(result.toString()));}
		return result.toString();
	}
    
	public int calculateLineLength(String line) { return line.length();}
	public int getColumnFromOffset(String text, int offset) {
		int lineStart = text.lastIndexOf('\n', offset) + 1;
		return offset - lineStart;
	}
	public int getLineFromOffset(String text, int offset) { return text.substring(0, offset).split("\n", -1).length;}
}
