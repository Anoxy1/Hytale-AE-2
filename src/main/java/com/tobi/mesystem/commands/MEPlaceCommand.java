package com.tobi.mesystem.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.tobi.mesystem.blocks.MECableBlock;
import com.tobi.mesystem.blocks.MEControllerBlock;
import com.tobi.mesystem.blocks.METerminalBlock;
import com.tobi.mesystem.util.BlockPos;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * Debug command to force-place ME blocks at player position
 * Usage: /place <cable|terminal|controller>
 */
public class MEPlaceCommand extends AbstractPlayerCommand {

    public MEPlaceCommand(@Nonnull String name, @Nonnull String description, boolean requiresConfirmation) {
        super(name, description, requiresConfirmation);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        // Simple implementation: place cable at test position
        // In production, would parse args and get player location
        
        try {
            // For now, use a test position - in production would get from player location
            BlockPos testPos = new BlockPos(0, 65, 0);
            
            playerRef.sendMessage(Message.raw("Attempting to place ME Cable at " + testPos));
            
            MECableBlock.onPlaced(testPos, world);
            playerRef.sendMessage(Message.raw("ME Cable placed at " + testPos));
            
        } catch (Exception e) {
            playerRef.sendMessage(Message.raw("Error placing block: " + e.getMessage()));
            com.tobi.mesystem.MEPlugin.getInstance().getPluginLogger()
                .at(Level.SEVERE).withCause(e).log("Error in /place command");
        }
    }
}
