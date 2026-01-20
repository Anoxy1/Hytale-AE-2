package com.tobi.mesystem.util;

import java.util.Objects;

/**
 * Immutable 3D block position in the world.
 * 
 * Represents a block's coordinates as (x, y, z) integers.
 * This class is immutable and thread-safe, making it suitable
 * for use as a HashMap key.
 * 
 * <p><b>Key Features:</b>
 * <ul>
 *   <li>Immutable - thread-safe by design</li>
 *   <li>Optimized equals/hashCode for HashMap performance</li>
 *   <li>Convenient offset methods for neighbor access</li>
 *   <li>Cached common positions (e.g., ORIGIN)</li>
 * </ul>
 * 
 * <p><b>Based on:</b> HyPipes BlockPos pattern
 * 
 * @author Anoxy1
 * @version 0.1.0
 * @since 0.1.0
 * @see Direction
 */
public final class BlockPos {
    private final int x;
    private final int y;
    private final int z;
    
    // Cache für häufig verwendete Positionen
    private static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
    
    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Gets the X coordinate.
     * 
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }
    
    /**
     * Gets the Y coordinate.
     * 
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }
    
    /**
     * Gets the Z coordinate.
     * 
     * @return the z coordinate
     */
    public int getZ() {
        return z;
    }
    
    /**
     * Returns the origin position (0, 0, 0).
     * 
     * @return cached origin position
     */
    public static BlockPos origin() {
        return ORIGIN;
    }
    
    /**
     * Creates a new position offset in the given direction.
     * 
     * @param direction the direction to offset
     * @return new position offset by one block in the given direction
     */
    public BlockPos offset(Direction direction) {
        return new BlockPos(
            x + direction.getOffsetX(),
            y + direction.getOffsetY(),
            z + direction.getOffsetZ()
        );
    }
    
    /**
     * Creates a new position with custom offset.
     * 
     * @param dx the x offset
     * @param dy the y offset
     * @param dz the z offset
     * @return new position offset by the given deltas
     */
    public BlockPos offset(int dx, int dy, int dz) {
        return new BlockPos(x + dx, y + dy, z + dz);
    }
    
    /**
     * Calculates Manhattan distance to another position.
     * 
     * @param other the other position
     * @return the Manhattan distance (sum of absolute coordinate differences)
     */
    public int manhattanDistance(BlockPos other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z);
    }
    
    /**
     * Berechnet quadrierte Euklidische Distanz zu anderer Position
     * (Vermeidet Math.sqrt für Performance)
     */
    public double distanceSq(BlockPos other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    /**
     * Berechnet Euklidische Distanz zu anderer Position
     */
    public double distance(BlockPos other) {
        return Math.sqrt(distanceSq(other));
    }
    
    /**
     * Konvertiert eine Hytale BlockPos zu einer MESystem BlockPos
     * 
     * @param hytaleBlockPos Hytale API BlockPos (com.hypixel.hytale.server.core.world.block.BlockPos)
     * @return Konvertierte MESystem BlockPos
     */
    public static BlockPos fromHytaleBlockPos(Object hytaleBlockPos) {
        try {
            // Nutze Reflection um auf Hytale BlockPos zuzugreifen
            // (Hytale API Namen sind nicht public verfügbar)
            Class<?> hytaleBlockPosClass = hytaleBlockPos.getClass();
            
            int x = (Integer) hytaleBlockPosClass.getMethod("getX").invoke(hytaleBlockPos);
            int y = (Integer) hytaleBlockPosClass.getMethod("getY").invoke(hytaleBlockPos);
            int z = (Integer) hytaleBlockPosClass.getMethod("getZ").invoke(hytaleBlockPos);
            
            return new BlockPos(x, y, z);
            
        } catch (Exception e) {
            // Fallback wenn Reflection fehlschlägt
            throw new RuntimeException("Konnte Hytale BlockPos nicht konvertieren", e);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockPos)) return false;
        BlockPos other = (BlockPos) obj;
        return x == other.x && y == other.y && z == other.z;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
    
    @Override
    public String toString() {
        return String.format("BlockPos(%d, %d, %d)", x, y, z);
    }
}
