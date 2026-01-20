package com.tobi.mesystem.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.tobi.mesystem.util.NetworkManager;

import javax.annotation.Nonnull;

public class MEStatusCommand extends AbstractPlayerCommand {
    private final NetworkManager networkManager;

    public MEStatusCommand(@Nonnull String name, @Nonnull String description, 
                          boolean requiresConfirmation, NetworkManager networkManager) {
        super(name, description, requiresConfirmation);
        this.networkManager = networkManager;
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        int networkCount = 0;
        int totalItems = 0;
        
        // Zähle Networks und Items über alle Dimensionen
        for (java.util.Map<java.util.UUID, com.tobi.mesystem.core.MENetwork> dimMap : networkManager.getAllNetworks().values()) {
            networkCount += dimMap.size();
            for (com.tobi.mesystem.core.MENetwork network : dimMap.values()) {
                totalItems += network.getTotalItemCount();
            }
        }

        playerRef.sendMessage(Message.raw(
            "===============================\n" +
            "   ME System Status\n" +
            "-------------------------------\n" +
            "  Networks: " + networkCount + "\n" +
            "  Items stored: " + totalItems + "\n" +
            "==============================="
        ));
    }
}
