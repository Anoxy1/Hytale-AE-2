package com.tobi.mesystem.core;

/**
 * ME Device Typen
 * 
 * Definiert welche Arten von Blöcken im ME-Netzwerk existieren können
 */
public enum MEDeviceType {
    /**
     * ME Cable - Verbindet Devices
     * Channel Usage: 0 (durchleiten nur)
     */
    CABLE(0),
    
    /**
     * ME Terminal - Zugriff auf Storage
     * Channel Usage: 1
     */
    TERMINAL(1),
    
    /**
     * ME Drive - Storage für Cells
     * Channel Usage: 1 (pro Cell separat?)
     */
    DRIVE(1),
    
    /**
     * ME Chest - Direkte Storage
     * Channel Usage: 1
     */
    CHEST(1),
    
    /**
     * ME Import Bus - Items ins Netzwerk
     * Channel Usage: 1
     */
    IMPORT_BUS(1),
    
    /**
     * ME Export Bus - Items aus Netzwerk
     * Channel Usage: 1
     */
    EXPORT_BUS(1),
    
    /**
     * ME Controller - Erweitert Channels auf 32
     * Channel Usage: 0 (Controller braucht selbst keine)
     */
    CONTROLLER(0),
    
    /**
     * ME Interface - Auto-Crafting Interface
     * Channel Usage: 1
     */
    INTERFACE(1),
    
    /**
     * ME Pattern Provider - Pattern Encoding
     * Channel Usage: 1
     */
    PATTERN_PROVIDER(1),
    
    /**
     * Molecular Assembler - Crafting Execution
     * Channel Usage: 0 (wird von Pattern Provider versorgt)
     */
    MOLECULAR_ASSEMBLER(0);
    
    private final int channelUsage;
    
    MEDeviceType(int channelUsage) {
        this.channelUsage = channelUsage;
    }
    
    /**
     * Wie viele Channels dieses Device benötigt
     */
    public int getChannelUsage() {
        return channelUsage;
    }
    
    /**
     * Ist dies ein Storage-Device?
     */
    public boolean isStorageDevice() {
        return this == DRIVE || this == CHEST;
    }
    
    /**
     * Ist dies ein I/O Device?
     */
    public boolean isIODevice() {
        return this == IMPORT_BUS || this == EXPORT_BUS || this == INTERFACE;
    }
    
    /**
     * Ist dies ein Crafting-Device?
     */
    public boolean isCraftingDevice() {
        return this == PATTERN_PROVIDER || this == MOLECULAR_ASSEMBLER || this == INTERFACE;
    }
}
