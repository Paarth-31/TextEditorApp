package com.texteditor.util;

/**
 * Application-wide constants.
 */
public class Constants {

    public static final String APP_TITLE = "Advanced Text Editor";
    public static final String APP_VERSION = "1.0.0";

    // File extensions
    public static final String TEXT_FILE_EXTENSION = ".txt";
    public static final String RTF_FILE_EXTENSION = ".rtf";
    public static final String JTX_FILE_EXTENSION = ".jtx";

    // UI Dimensions
    public static final int MIN_WINDOW_WIDTH = 800;
    public static final int MIN_WINDOW_HEIGHT = 600;
    public static final int DEFAULT_WINDOW_WIDTH = 1200;
    public static final int DEFAULT_WINDOW_HEIGHT = 800;

    // Font Sizes
    public static final int MIN_FONT_SIZE = 8;
    public static final int MAX_FONT_SIZE = 72;
    public static final int DEFAULT_FONT_SIZE = 12;

    // Timeouts
    public static final int AUTO_SAVE_INTERVAL_MS = 60000; // 1 minute

    // File Paths
    public static final String CONFIG_DIR = System.getProperty("user.home") + "/.texteditor";
    public static final String PREFERENCES_FILE = CONFIG_DIR + "/preferences.json";
    public static final String STYLES_FILE = CONFIG_DIR + "/styles.json";
    public static final String RECENT_FILES = CONFIG_DIR + "/recent.json";
}
