package com.texteditor.manager;

import com.texteditor.model.Style;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages custom styles and style sets.
 */
public class StyleManager {
    private Map<String, Style> customStyles;
    private Map<String, Style> builtInStyles;
    private Style currentStyle;
    private String currentStyleSet;

    // Predefined style sets
    private static final Map<String, Map<String, Style>> STYLE_SETS = new HashMap<>();

    public StyleManager() {
        this.customStyles = new HashMap<>();
        this.builtInStyles = initializeBuiltInStyles();
        this.currentStyle = builtInStyles.get("Normal");
        this.currentStyleSet = "Light";
        initializeStyleSets();
    }

    private Map<String, Style> initializeBuiltInStyles() {
        Map<String, Style> styles = new HashMap<>();

        Style normalStyle = new Style("Normal", "Consolas", 12);
        Style heading1 = new Style("Heading 1", "Arial", 24);
        heading1.setBold(true);

        Style heading2 = new Style("Heading 2", "Arial", 18);
        heading2.setBold(true);

        Style quote = new Style("Quote", "Georgia", 12);
        quote.setItalic(true);

        Style code = new Style("Code", "Courier New", 10);
        code.setBackgroundColor("#F0F0F0");

        styles.put("Normal", normalStyle);
        styles.put("Heading 1", heading1);
        styles.put("Heading 2", heading2);
        styles.put("Quote", quote);
        styles.put("Code", code);

        return styles;
    }

    private void initializeStyleSets() {
        // Light Style Set
        Map<String, Style> lightSet = new HashMap<>(builtInStyles);
        STYLE_SETS.put("Light", lightSet);

        // Professional Style Set
        Map<String, Style> professionalSet = new HashMap<>();
        Style profNormal = new Style("Normal", "Calibri", 11);
        Style profHeading = new Style("Heading 1", "Calibri", 16);
        profHeading.setBold(true);
        professionalSet.put("Normal", profNormal);
        professionalSet.put("Heading 1", profHeading);
        STYLE_SETS.put("Professional", professionalSet);

        // Academic Style Set
        Map<String, Style> academicSet = new HashMap<>();
        Style acadNormal = new Style("Normal", "Times New Roman", 12);
        academicSet.put("Normal", acadNormal);
        STYLE_SETS.put("Academic", academicSet);
    }

    // Custom style management
    public void createCustomStyle(String name, Style style) {
        style.setName(name);
        customStyles.put(name, style);
    }

    public void deleteCustomStyle(String name) {
        customStyles.remove(name);
    }

    public Style getStyle(String name) {
        return customStyles.getOrDefault(name, builtInStyles.get(name));
    }

    public void applyStyle(String styleName) {
        Style style = getStyle(styleName);
        if (style != null) {
            currentStyle = style;
        }
    }

    public List<String> getAllStyleNames() {
        Set<String> allNames = new HashSet<>();
        allNames.addAll(builtInStyles.keySet());
        allNames.addAll(customStyles.keySet());
        return new ArrayList<>(allNames);
    }

    public List<String> getCustomStyleNames() {
        return new ArrayList<>(customStyles.keySet());
    }

    public void applyStyleSet(String setName) {
        if (STYLE_SETS.containsKey(setName)) {
            currentStyleSet = setName;
            Map<String, Style> styleSet = STYLE_SETS.get(setName);
            builtInStyles.putAll(styleSet);
        }
    }

    public List<String> getAvailableStyleSets() {
        return new ArrayList<>(STYLE_SETS.keySet());
    }

    public Style getCurrentStyle() { return currentStyle; }
}
