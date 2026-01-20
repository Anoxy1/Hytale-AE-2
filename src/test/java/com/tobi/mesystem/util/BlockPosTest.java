package com.tobi.mesystem.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for BlockPos.
 * 
 * Tests immutability, equals/hashCode contract, and offset operations.
 * 
 * @author Anoxy1
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("BlockPos Tests")
class BlockPosTest {
    
    @Test
    @DisplayName("Constructor creates position with correct coordinates")
    void testConstructor() {
        BlockPos pos = new BlockPos(10, 20, 30);
        
        assertEquals(10, pos.getX());
        assertEquals(20, pos.getY());
        assertEquals(30, pos.getZ());
    }
    
    @Test
    @DisplayName("Origin returns (0, 0, 0)")
    void testOrigin() {
        BlockPos origin = BlockPos.origin();
        
        assertEquals(0, origin.getX());
        assertEquals(0, origin.getY());
        assertEquals(0, origin.getZ());
    }
    
    @Test
    @DisplayName("Offset by direction creates new position")
    void testOffsetByDirection() {
        BlockPos pos = new BlockPos(0, 0, 0);
        
        BlockPos north = pos.offset(Direction.NORTH);
        assertEquals(0, north.getX());
        assertEquals(0, north.getY());
        assertEquals(-1, north.getZ()); // North = -Z
        
        BlockPos up = pos.offset(Direction.UP);
        assertEquals(0, up.getX());
        assertEquals(1, up.getY()); // Up = +Y
        assertEquals(0, up.getZ());
    }
    
    @Test
    @DisplayName("Offset by deltas creates new position")
    void testOffsetByDeltas() {
        BlockPos pos = new BlockPos(5, 10, 15);
        BlockPos offset = pos.offset(2, 3, 4);
        
        assertEquals(7, offset.getX());
        assertEquals(13, offset.getY());
        assertEquals(19, offset.getZ());
    }
    
    @Test
    @DisplayName("Manhattan distance calculation is correct")
    void testManhattanDistance() {
        BlockPos pos1 = new BlockPos(0, 0, 0);
        BlockPos pos2 = new BlockPos(3, 4, 5);
        
        int distance = pos1.manhattanDistance(pos2);
        assertEquals(12, distance); // |3-0| + |4-0| + |5-0| = 12
    }
    
    @Test
    @DisplayName("Equals contract: reflexive")
    void testEqualsReflexive() {
        BlockPos pos = new BlockPos(1, 2, 3);
        assertEquals(pos, pos);
    }
    
    @Test
    @DisplayName("Equals contract: symmetric")
    void testEqualsSymmetric() {
        BlockPos pos1 = new BlockPos(1, 2, 3);
        BlockPos pos2 = new BlockPos(1, 2, 3);
        
        assertEquals(pos1, pos2);
        assertEquals(pos2, pos1);
    }
    
    @Test
    @DisplayName("Equals contract: transitive")
    void testEqualsTransitive() {
        BlockPos pos1 = new BlockPos(1, 2, 3);
        BlockPos pos2 = new BlockPos(1, 2, 3);
        BlockPos pos3 = new BlockPos(1, 2, 3);
        
        assertEquals(pos1, pos2);
        assertEquals(pos2, pos3);
        assertEquals(pos1, pos3);
    }
    
    @Test
    @DisplayName("Equals returns false for different positions")
    void testNotEquals() {
        BlockPos pos1 = new BlockPos(1, 2, 3);
        BlockPos pos2 = new BlockPos(4, 5, 6);
        
        assertNotEquals(pos1, pos2);
    }
    
    @Test
    @DisplayName("Equals returns false for null")
    void testNotEqualsNull() {
        BlockPos pos = new BlockPos(1, 2, 3);
        assertNotEquals(pos, null);
    }
    
    @Test
    @DisplayName("HashCode is consistent with equals")
    void testHashCode() {
        BlockPos pos1 = new BlockPos(1, 2, 3);
        BlockPos pos2 = new BlockPos(1, 2, 3);
        BlockPos pos3 = new BlockPos(4, 5, 6);
        
        // Equal objects must have same hash code
        assertEquals(pos1.hashCode(), pos2.hashCode());
        
        // Different objects may have different hash codes (not guaranteed, but likely)
        // We don't assert inequality as it could theoretically collide
    }
    
    @Test
    @DisplayName("ToString contains coordinates")
    void testToString() {
        BlockPos pos = new BlockPos(10, 20, 30);
        String str = pos.toString();
        
        assertTrue(str.contains("10"));
        assertTrue(str.contains("20"));
        assertTrue(str.contains("30"));
    }
    
    @Test
    @DisplayName("Immutability: offset does not modify original")
    void testImmutability() {
        BlockPos original = new BlockPos(5, 10, 15);
        BlockPos offset = original.offset(Direction.NORTH);
        
        // Original should not be modified
        assertEquals(5, original.getX());
        assertEquals(10, original.getY());
        assertEquals(15, original.getZ());
        
        // Offset should be different
        assertNotEquals(original, offset);
    }
}
