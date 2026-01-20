package com.tobi.mesystem.events;

import org.apache.logging.log4j.Logger;

import com.tobi.mesystem.MEPlugin;
import com.tobi.mesystem.blocks.MECableBlock;
import com.tobi.mesystem.blocks.MEControllerBlock;
import com.tobi.mesystem.blocks.METerminalBlock;
import com.tobi.mesystem.util.BlockPos;

/**
 * Hytale Block Event Listener - für Hytale Event-Integration
 *
 * INSTALLATION: Diese Klasse wird registriert im Hytale Plugin-Setup. Die
 * @EventHandler Annotation muss von Hytale API importiert werden.
 *
 * Verwende Reflection oder ein Wrapper-Interface um diese Klasse bei der
 * Plugin-Initialization zu registrieren.
 *
 * BEISPIEL:
 *
 * // In MEPlugin.setup() oder separater Registrar-Klasse: try { Object
 * listener = new HytaleBlockEventListenerStub(); Method registerListener =
 * pluginManager.getClass() .getMethod("registerListener", JavaPlugin.class,
 * Object.class); registerListener.invoke(pluginManager, this, listener); }
 * catch (Exception e) { logger.error("Fehler bei Event-Registrierung", e); }
 */
public class HytaleBlockEventListenerStub {

    private final Logger logger;

    public HytaleBlockEventListenerStub() {
        Logger tempLogger;
        try {
            MEPlugin plugin = MEPlugin.getInstance();
            tempLogger = plugin.getPluginLogger();
        } catch (Exception e) {
            // Fallback if MEPlugin not fully initialized
            tempLogger = org.apache.logging.log4j.LogManager.getLogger(HytaleBlockEventListenerStub.class);
            tempLogger.warn("MEPlugin not initialized during HytaleBlockEventListenerStub construction");
        }
        this.logger = tempLogger;
    }

    // ==================== BLOCK PLACE EVENT ====================
    /**
     * Handler für PlaceBlockEvent Annotation: @EventHandler registriert diese
     * Methode automatisch bei Hytale
     */
    @EventHandler
    public void onPlaceBlock(Object placeBlockEvent) {
        try {
            if (!MEPlugin.isInitialized()) {
                logger.debug("MEPlugin not fully initialized - ignoring PlaceBlockEvent");
                return;
            }
            if (placeBlockEvent == null) {
                return;
            }

            // Extrahiere Event-Daten via Reflection
            String blockId = (String) invokeMethod(placeBlockEvent, "getBlockId");
            Object blockPosObj = invokeMethod(placeBlockEvent, "getBlockPos");
            Object world = invokeMethod(placeBlockEvent, "getWorld");

            BlockPos pos = BlockPos.fromHytaleBlockPos(blockPosObj);

            logger.debug("PlaceBlockEvent: " + blockId + " at " + pos);

            // Route zu entsprechendem Block-Handler
            if (isMECable(blockId)) {
                MECableBlock.onPlaced(pos, world);
                logger.info("ME Cable platziert at " + pos);

            } else if (isMETerminal(blockId)) {
                METerminalBlock.onPlaced(pos, world);
                logger.info("ME Terminal platziert at " + pos);

            } else if (isMEController(blockId)) {
                MEControllerBlock.onPlaced(pos, world);
                logger.info("ME Controller platziert at " + pos);
            }

        } catch (Exception e) {
            logger.error("Fehler in PlaceBlockEvent", e);
        }
    }

    // ==================== BLOCK BREAK EVENT ====================
    /**
     * Handler für BreakBlockEvent Annotation: @EventHandler registriert diese
     * Methode automatisch bei Hytale
     */
    @EventHandler
    public void onBreakBlock(Object breakBlockEvent) {
        try {
            if (!MEPlugin.isInitialized()) {
                logger.debug("MEPlugin not fully initialized - ignoring BreakBlockEvent");
                return;
            }
            if (breakBlockEvent == null) {
                return;
            }

            String blockId = (String) invokeMethod(breakBlockEvent, "getBlockId");
            Object blockPosObj = invokeMethod(breakBlockEvent, "getBlockPos");
            Object world = invokeMethod(breakBlockEvent, "getWorld");

            BlockPos pos = BlockPos.fromHytaleBlockPos(blockPosObj);

            logger.debug("BreakBlockEvent: " + blockId + " at " + pos);

            if (isMECable(blockId)) {
                MECableBlock.onBroken(pos, world);
                logger.info("ME Cable zerstört at " + pos);

            } else if (isMETerminal(blockId)) {
                METerminalBlock.onBroken(pos, world);
                logger.info("ME Terminal zerstört at " + pos);

            } else if (isMEController(blockId)) {
                MEControllerBlock.onBroken(pos, world);
                logger.info("ME Controller zerstört at " + pos);
            }

        } catch (Exception e) {
            logger.error("Fehler in BreakBlockEvent", e);
        }
    }

    // ==================== BLOCK USE/INTERACT EVENT ====================
    /**
     * Handler für UseBlockEvent (Interaction) Annotation: @EventHandler
     * registriert diese Methode automatisch bei Hytale
     */
    @EventHandler
    public void onUseBlock(Object useBlockEvent) {
        try {
            if (!MEPlugin.isInitialized()) {
                logger.debug("MEPlugin not fully initialized - ignoring UseBlockEvent");
                return;
            }
            if (useBlockEvent == null) {
                return;
            }

            String blockId = (String) invokeMethod(useBlockEvent, "getBlockId");
            Object blockPosObj = invokeMethod(useBlockEvent, "getBlockPos");
            Object world = invokeMethod(useBlockEvent, "getWorld");
            Object player = invokeMethod(useBlockEvent, "getPlayer");

            BlockPos pos = BlockPos.fromHytaleBlockPos(blockPosObj);

            logger.debug("UseBlockEvent: " + blockId + " at " + pos);

            // Terminal-Interaktion
            if (isMETerminal(blockId)) {
                boolean handled = METerminalBlock.onRightClick(pos, world, player);
                if (handled) {
                    // Cancelled die Standard-Block-Interaktion
                    invokeMethod(useBlockEvent, "setCancelled", new Class[]{boolean.class}, true);
                    logger.debug("Terminal-GUI geöffnet");
                }
            }

        } catch (Exception e) {
            logger.error("Fehler in UseBlockEvent", e);
        }
    }

    // ==================== HELPER METHODS ====================
    /**
     * Ruft eine Methode auf einem Objekt via Reflection auf
     */
    private Object invokeMethod(Object obj, String methodName) {
        try {
            return obj.getClass().getMethod(methodName).invoke(obj);
        } catch (ReflectiveOperationException e) {
            logger.warn("Konnte Methode " + methodName + " nicht aufrufen", e);
            return null;
        }
    }

    /**
     * Ruft eine Methode mit Parametern auf einem Objekt via Reflection auf
     */
    private Object invokeMethod(Object obj, String methodName, Class<?>[] paramTypes, Object... params) {
        try {
            return obj.getClass().getMethod(methodName, paramTypes).invoke(obj, params);
        } catch (ReflectiveOperationException e) {
            logger.warn("Konnte Methode " + methodName + " nicht aufrufen", e);
            return null;
        }
    }

    private boolean isMECable(String blockId) {
        return blockId != null && blockId.equals("mesystem:me_cable");
    }

    private boolean isMETerminal(String blockId) {
        return blockId != null && blockId.equals("mesystem:me_terminal");
    }

    private boolean isMEController(String blockId) {
        return blockId != null && blockId.equals("mesystem:me_controller");
    }
}
