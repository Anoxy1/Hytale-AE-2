package com.tobi.mesystem.util;

import java.util.Objects;

/**
 * Block Position - 3D Koordinate in der Welt
 * 
 * Basiert auf HyPipes BlockPos
 * 
 * Immutable Value Object:
 * - Thread-Safe durch Unveränderlichkeit
 * - Kann sicher als Map-Key verwendet werden
 * - Optimierte equals/hashCode Implementation
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
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
    /**
     * Gibt Origin (0,0,0) zurück
     */
    public static BlockPos origin() {
        return ORIGIN;
    }
    
    /**
     * Erstellt neue Position in Richtung verschoben
     */
    public BlockPos offset(Direction direction) {
        return new BlockPos(
            x + direction.getOffsetX(),
            y + direction.getOffsetY(),
            z + direction.getOffsetZ()
        );
    }
    
    /**
     * Erstellt neue Position mit Offset
     */
    public BlockPos offset(int dx, int dy, int dz) {
        return new BlockPos(x + dx, y + dy, z + dz);
    }
    
    /**
     * Berechnet Manhattan-Distanz zu anderer Position
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
