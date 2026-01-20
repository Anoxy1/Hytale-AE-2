package com.tobi.mesystem.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.logging.Level;

import com.hypixel.hytale.logger.HytaleLogger;

/**
 * Configuration manager for HytaleAE2.
 * 
 * Loads settings from config.properties file and provides
 * typed access to configuration values.
 * 
 * Based on Britakee Studios Best Practices:
 * - Centralized configuration
 * - Type-safe getters
 * - Default values
 * - Runtime reloadable
 * 
 * @since 0.1.0
 */
public class MEConfig {
    
    private static final String CONFIG_FILENAME = "hytaleae2-config.properties";
    
    // Network Settings
    private int maxChannels = 32;
    private boolean autoNetworkMerge = true;
    
    // Performance Settings
    private int searchRadius = 16;
    private int maxSearchTimeMs = 100;
    private int threadPoolSize = 4;
    
    // Feature Flags
    private boolean debugMode = false;
    private boolean logNetworkEvents = false;
    private boolean enableAutocrafting = false;
    private boolean enableStorageCells = false;
    
    // Storage Settings
    private long storageCapacityPerDrive = 1024;
    private int maxItemTypesPerCell = 63;
    
    private final HytaleLogger logger;
    private final Path configPath;
    private final Properties properties;
    
    /**
     * Creates a new configuration manager.
     * 
     * @param logger the logger for this plugin
     * @param dataFolder the plugin's data folder
     */
    public MEConfig(HytaleLogger logger, Path dataFolder) {
        this.logger = logger;
        this.configPath = dataFolder.resolve(CONFIG_FILENAME);
        this.properties = new Properties();
    }
    
    /**
     * Loads configuration from file.
     * If file doesn't exist, creates it with defaults.
     * 
     * @return true if loaded successfully
     */
    public boolean load() {
        try {
            if (!Files.exists(configPath)) {
                logger.at(Level.INFO).log("Config file not found, creating defaults: %s", configPath);
                createDefaultConfig();
            }
            
            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
                parseProperties();
                logger.at(Level.INFO).log("[OK] Configuration loaded from: %s", configPath);
                return true;
            }
        } catch (IOException e) {
            logger.at(Level.SEVERE).withCause(e).log("Failed to load configuration, using defaults");
            return false;
        }
    }
    
    /**
     * Saves current configuration to file.
     * 
     * @return true if saved successfully
     */
    public boolean save() {
        try {
            updateProperties();
            try (var output = Files.newOutputStream(configPath)) {
                properties.store(output, "HytaleAE2 Configuration");
                logger.at(Level.INFO).log("[OK] Configuration saved to: %s", configPath);
                return true;
            }
        } catch (IOException e) {
            logger.at(Level.SEVERE).withCause(e).log("Failed to save configuration");
            return false;
        }
    }
    
    /**
     * Reloads configuration from file.
     * 
     * @return true if reloaded successfully
     */
    public boolean reload() {
        logger.at(Level.INFO).log("Reloading configuration...");
        return load();
    }
    
    /**
     * Creates default configuration file from embedded resource or hardcoded defaults.
     */
    private void createDefaultConfig() throws IOException {
        // Ensure parent directory exists
        Files.createDirectories(configPath.getParent());
        
        // Try to copy from resources first
        try (InputStream resource = getClass().getResourceAsStream("/config-default.properties")) {
            if (resource != null) {
                Files.copy(resource, configPath, StandardCopyOption.REPLACE_EXISTING);
                logger.at(Level.INFO).log("[OK] Default config copied from resources");
                return;
            }
        } catch (IOException e) {
            logger.at(Level.WARNING).log("Could not copy default config from resources: %s", e.getMessage());
        }
        
        // Fallback: Create with hardcoded defaults
        updateProperties();
        try (var output = Files.newOutputStream(configPath)) {
            properties.store(output, "HytaleAE2 Configuration - Generated Defaults");
            logger.at(Level.INFO).log("[OK] Default config created with hardcoded values");
        }
    }
    
    /**
     * Parses properties from file into typed fields.
     */
    private void parseProperties() {
        // Network Settings
        maxChannels = getInt("network.maxChannels", 32);
        autoNetworkMerge = getBoolean("network.autoMerge", true);
        
        // Performance Settings
        searchRadius = getInt("performance.searchRadius", 16);
        maxSearchTimeMs = getInt("performance.maxSearchTimeMs", 100);
        threadPoolSize = getInt("performance.threadPoolSize", 4);
        
        // Feature Flags
        debugMode = getBoolean("debug.enabled", false);
        logNetworkEvents = getBoolean("debug.logNetworkEvents", false);
        enableAutocrafting = getBoolean("features.autocrafting", false);
        enableStorageCells = getBoolean("features.storageCells", false);
        
        // Storage Settings
        storageCapacityPerDrive = getLong("storage.capacityPerDrive", 1024L);
        maxItemTypesPerCell = getInt("storage.maxItemTypesPerCell", 63);
        
        // Validate values
        validateConfiguration();
    }
    
    /**
     * Updates properties from typed fields.
     */
    private void updateProperties() {
        // Network Settings
        properties.setProperty("network.maxChannels", String.valueOf(maxChannels));
        properties.setProperty("network.autoMerge", String.valueOf(autoNetworkMerge));
        
        // Performance Settings
        properties.setProperty("performance.searchRadius", String.valueOf(searchRadius));
        properties.setProperty("performance.maxSearchTimeMs", String.valueOf(maxSearchTimeMs));
        properties.setProperty("performance.threadPoolSize", String.valueOf(threadPoolSize));
        
        // Feature Flags
        properties.setProperty("debug.enabled", String.valueOf(debugMode));
        properties.setProperty("debug.logNetworkEvents", String.valueOf(logNetworkEvents));
        properties.setProperty("features.autocrafting", String.valueOf(enableAutocrafting));
        properties.setProperty("features.storageCells", String.valueOf(enableStorageCells));
        
        // Storage Settings
        properties.setProperty("storage.capacityPerDrive", String.valueOf(storageCapacityPerDrive));
        properties.setProperty("storage.maxItemTypesPerCell", String.valueOf(maxItemTypesPerCell));
    }
    
    /**
     * Validates configuration values and applies constraints.
     */
    private void validateConfiguration() {
        // Max channels must be 8 or 32
        if (maxChannels != 8 && maxChannels != 32) {
            logger.at(Level.WARNING).log("Invalid maxChannels value: %d, using 32", maxChannels);
            maxChannels = 32;
        }
        
        // Search radius must be positive and reasonable
        if (searchRadius < 1 || searchRadius > 64) {
            logger.at(Level.WARNING).log("Invalid searchRadius: %d, using 16", searchRadius);
            searchRadius = 16;
        }
        
        // Thread pool size must be positive
        if (threadPoolSize < 1) {
            logger.at(Level.WARNING).log("Invalid threadPoolSize: %d, using 4", threadPoolSize);
            threadPoolSize = 4;
        }
        
        // Max search time must be positive
        if (maxSearchTimeMs < 10) {
            logger.at(Level.WARNING).log("Invalid maxSearchTimeMs: %d, using 100", maxSearchTimeMs);
            maxSearchTimeMs = 100;
        }
    }
    
    // Helper methods for property parsing
    
    private int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.at(Level.WARNING).log("Invalid integer for %s: %s, using default: %d", key, value, defaultValue);
            return defaultValue;
        }
    }
    
    private long getLong(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.at(Level.WARNING).log("Invalid long for %s: %s, using default: %d", key, value, defaultValue);
            return defaultValue;
        }
    }
    
    private boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value.trim());
    }
    
    // Public getters
    
    public int getMaxChannels() {
        return maxChannels;
    }
    
    public boolean isAutoNetworkMerge() {
        return autoNetworkMerge;
    }
    
    public int getSearchRadius() {
        return searchRadius;
    }
    
    public int getMaxSearchTimeMs() {
        return maxSearchTimeMs;
    }
    
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public boolean isLogNetworkEvents() {
        return logNetworkEvents;
    }
    
    public boolean isAutocraftingEnabled() {
        return enableAutocrafting;
    }
    
    public boolean isStorageCellsEnabled() {
        return enableStorageCells;
    }
    
    public long getStorageCapacityPerDrive() {
        return storageCapacityPerDrive;
    }
    
    public int getMaxItemTypesPerCell() {
        return maxItemTypesPerCell;
    }
    
    // Public setters (for runtime modification)
    
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public void setLogNetworkEvents(boolean logNetworkEvents) {
        this.logNetworkEvents = logNetworkEvents;
    }
}
