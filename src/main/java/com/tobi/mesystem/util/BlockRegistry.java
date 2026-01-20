package com.tobi.mesystem.util;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * BlockRegistry - Registriert ME Blocks bei Hytale Nutzt Reflection um Hytale
 * Block-System anzusprechen
 */
public class BlockRegistry {

    private static final Logger logger = LogManager.getLogger(BlockRegistry.class);
    private final Object blockManager;

    public BlockRegistry(Object hytaleBlockManager) {
        this.blockManager = hytaleBlockManager;
    }

    /**
     * Registriert alle ME-System Blocks
     */
    public void registerAllBlocks() {
        logger.info("=== ME System Block-Registrierung ===");

        if (blockManager == null) {
            logger.warn("BlockManager ist null - Block-Registrierung übersprungen");
            return;
        }

        try {
            registerMECableBlock();
            registerMETerminalBlock();
            registerMEControllerBlock();

            logger.info("✓ Alle Blocks registriert");
        } catch (Exception e) {
            logger.error("Fehler beim Registrieren der Blocks", e);
        }
    }

    /**
     * ME Cable Block - Transport und Stromversorgung
     */
    private void registerMECableBlock() throws Exception {
        logger.info("Registriere: ME Cable Block");

        BlockConfig config = new BlockConfig(
                "me_cable", // Block ID
                "ME Cable", // Display Name
                "Transport block for ME networks", // Description
                0.5f, 0.5f, 0.5f, // RGB Color (grey)
                true, // Solid
                true, // Full Block
                1.0f, // Hardness
                1.0f // Resistance
        );

        registerBlock(config);
    }

    /**
     * ME Terminal Block - Terminal zum Abrufen/Lagern von Items
     */
    private void registerMETerminalBlock() throws Exception {
        logger.info("Registriere: ME Terminal Block");

        BlockConfig config = new BlockConfig(
                "me_terminal",
                "ME Terminal",
                "Access point for ME network storage",
                0.8f, 0.2f, 0.2f, // RGB Color (orange-ish)
                true,
                true,
                1.5f, // Slightly harder
                1.5f
        );

        registerBlock(config);
    }

    /**
     * ME Controller Block - Zentrale für bis zu 32 Kanäle
     */
    private void registerMEControllerBlock() throws Exception {
        logger.info("Registriere: ME Controller Block");

        BlockConfig config = new BlockConfig(
                "me_controller",
                "ME Controller",
                "Main hub for ME network (32 channels max)",
                0.3f, 0.7f, 0.3f, // RGB Color (greenish)
                true,
                true,
                2.0f, // Hardest block
                2.0f
        );

        registerBlock(config);
    }

    /**
     * Hilfsmethode zum Registrieren eines Blocks via Reflection
     */
    private void registerBlock(BlockConfig config) throws Exception {
        if (blockManager == null) {
            logger.debug("BlockManager null beim Registrieren von " + config.blockId + " - übersprungen");
            return;
        }

        try {
            // blockManager.registerBlock(config)
            Method registerMethod = blockManager.getClass().getMethod(
                    "registerBlock",
                    String.class, // blockId
                    String.class, // displayName
                    String.class, // description
                    float.class, // r
                    float.class, // g
                    float.class, // b
                    boolean.class, // solid
                    boolean.class, // fullBlock
                    float.class, // hardness
                    float.class // resistance
            );

            registerMethod.invoke(
                    blockManager,
                    config.blockId,
                    config.displayName,
                    config.description,
                    config.colorR,
                    config.colorG,
                    config.colorB,
                    config.solid,
                    config.fullBlock,
                    config.hardness,
                    config.resistance
            );

            logger.info("  ✓ " + config.displayName + " (" + config.blockId + ")");

        } catch (NoSuchMethodException e) {
            logger.warn("Hytale BlockManager hat kein registerBlock() - Block-System nicht verfügbar");
        } catch (Exception e) {
            logger.warn("Fehler beim Aufrufen von BlockManager.registerBlock(): " + e.getClass().getName() + " - " + e.getMessage());
            logger.debug("Stack trace:", e);
        }
    }

    /**
     * Block-Konfiguration
     */
    private static class BlockConfig {

        String blockId;
        String displayName;
        String description;
        float colorR, colorG, colorB;
        boolean solid;
        boolean fullBlock;
        float hardness;
        float resistance;

        BlockConfig(String blockId, String displayName, String description,
                float r, float g, float b, boolean solid, boolean fullBlock,
                float hardness, float resistance) {
            this.blockId = blockId;
            this.displayName = displayName;
            this.description = description;
            this.colorR = r;
            this.colorG = g;
            this.colorB = b;
            this.solid = solid;
            this.fullBlock = fullBlock;
            this.hardness = hardness;
            this.resistance = resistance;
        }
    }
}
