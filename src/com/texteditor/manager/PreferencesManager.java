package com.texteditor.manager;

import com.texteditor.model.Theme;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages application preferences with persistence.
 */
public class PreferencesManager {
    private Map<String, Object> preferences;
    private Theme currentTheme;
    private String preferencesFile;

    public PreferencesManager(String preferencesFilePath) {
        this.preferencesFile = preferencesFilePath;
        this.preferences = new HashMap<>();
        loadPreferences();
    }

    public void loadPreferences() {
        // Simple initialization without JSON for now
        setDefaults();
        initializeTheme();
    }

    private void setDefaults() {
        preferences.put("menuBarHeight", 40);
        preferences.put("scrollBarWidth", 15);
        preferences.put("fontScale", 100);
        preferences.put("theme", "Light");
        preferences.put("editorFont", "Consolas");
        preferences.put("editorFontSize", 12);
        preferences.put("autoSave", true);
        preferences.put("autoSaveInterval", 10);
    }

    private void initializeTheme() {
        String themeName = (String) preferences.get("theme");
        currentTheme = createTheme(themeName);
    }

    public Theme createTheme(String themeName) {
        if (themeName == null) {
            themeName = "Light";
        }

        switch (themeName) {
            case "Dark":
                return new Theme("Dark", "#1E1E1E", "#E0E0E0", "#64B5F6");
            case "HighContrast":
                return new Theme("HighContrast", "#000000", "#FFFFFF", "#FFFF00");
            default:
                return new Theme("Light", "#F5F5F5", "#333333", "#2196F3");
        }
    }

    public Object getPreference(String key) {
        return preferences.get(key);
    }

    public void setPreference(String key, Object value) {
        preferences.put(key, value);
        savePreferences();
    }

    public void savePreferences() {
        // Simple save - can be enhanced with JSON later
        System.out.println("Saving preferences...");
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setTheme(String themeName) {
        currentTheme = createTheme(themeName);
        preferences.put("theme", themeName);
        savePreferences();
    }

    public int getMenuBarHeight() {
        Object value = preferences.getOrDefault("menuBarHeight", 40);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return 40;
    }

    public void setMenuBarHeight(int height) {
        setPreference("menuBarHeight", height);
    }

    public int getScrollBarWidth() {
        Object value = preferences.getOrDefault("scrollBarWidth", 15);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return 15;
    }

    public void setScrollBarWidth(int width) {
        setPreference("scrollBarWidth", width);
    }

    public int getFontScale() {
        Object value = preferences.getOrDefault("fontScale", 100);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return 100;
    }

    public void setFontScale(int scale) {
        setPreference("fontScale", scale);
    }
}