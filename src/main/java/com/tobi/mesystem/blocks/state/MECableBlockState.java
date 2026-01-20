package com.tobi.mesystem.blocks.state;

import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;

/**
 * ME Cable BlockState
 *
 * Transport block that connects ME network devices.
 * Uses connection bitmask for dynamic visual states (like HyPipes).
 * 
 * Connection bits (0-63):
 * - Bit 0: Down
 * - Bit 1: Up
 * - Bit 2: North
 * - Bit 3: South
 * - Bit 4: West
 * - Bit 5: East
 */
public class MECableBlockState extends BlockState {

    public static final BuilderCodec<MECableBlockState> CODEC;
    
    static {
        CODEC = BuilderCodec.builder(
            MECableBlockState.class,
            MECableBlockState::new,
            BlockState.BASE_CODEC
        )
        .append(
            new KeyedCodec<>("Connections", Codec.INTEGER),
            (state, connections) -> state.connections = connections,
            state -> state.connections
        )
        .add()
        .build();
    }
    
    private int connections; // Bitmask: 0-63 for 6 directions
    
    public MECableBlockState() {
        this.connections = 0;
    }
    
    public int getConnections() {
        return connections;
    }
    
    public void setConnections(int connections) {
        this.connections = connections;
    }
    
    /**
     * Check if cable is connected in specific direction
     * @param directionBit 0-5 for down, up, north, south, west, east
     */
    public boolean isConnected(int directionBit) {
        return (connections & (1 << directionBit)) != 0;
    }
    
    /**
     * Set connection state for specific direction
     */
    public void setConnected(int directionBit, boolean connected) {
        int mask = 1 << directionBit;
        this.connections = connected 
            ? (connections | mask) 
            : (connections & ~mask);
    }
}
