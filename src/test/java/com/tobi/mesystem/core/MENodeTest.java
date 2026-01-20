package com.tobi.mesystem.core;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tobi.mesystem.util.BlockPos;
import com.tobi.mesystem.util.Direction;

/**
 * Unit tests for MENode.
 * 
 * Tests node creation, connections, and properties.
 * 
 * @author Anoxy1
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("MENode Tests")
class MENodeTest {
    
    @Test
    @DisplayName("Node creation with valid parameters")
    void testNodeCreation() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(5, 10, 15);
        
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        assertNotNull(node);
        assertEquals(worldId, node.getWorldId());
        assertEquals(pos, node.getPosition());
        assertEquals(MEDeviceType.CABLE, node.getDeviceType());
    }
    
    @Test
    @DisplayName("New node has no connections")
    void testInitialConnections() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        assertTrue(node.getConnections().isEmpty());
    }
    
    @Test
    @DisplayName("Add connection")
    void testAddConnection() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        node.addConnection(Direction.NORTH);
        
        assertTrue(node.hasConnection(Direction.NORTH));
        assertEquals(1, node.getConnections().size());
    }
    
    @Test
    @DisplayName("Add multiple connections")
    void testAddMultipleConnections() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        node.addConnection(Direction.NORTH);
        node.addConnection(Direction.SOUTH);
        node.addConnection(Direction.EAST);
        
        assertEquals(3, node.getConnections().size());
        assertTrue(node.hasConnection(Direction.NORTH));
        assertTrue(node.hasConnection(Direction.SOUTH));
        assertTrue(node.hasConnection(Direction.EAST));
    }
    
    @Test
    @DisplayName("Remove connection")
    void testRemoveConnection() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        node.addConnection(Direction.NORTH);
        assertTrue(node.hasConnection(Direction.NORTH));
        
        node.removeConnection(Direction.NORTH);
        assertFalse(node.hasConnection(Direction.NORTH));
    }
    
    @Test
    @DisplayName("New node is active by default")
    void testDefaultActive() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        assertTrue(node.isActive());
    }
    
    @Test
    @DisplayName("Can set node inactive")
    void testSetInactive() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        node.setActive(false);
        
        assertFalse(node.isActive());
    }
    
    @Test
    @DisplayName("Default priority is 0")
    void testDefaultPriority() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        assertEquals(0, node.getPriority());
    }
    
    @Test
    @DisplayName("Can set priority")
    void testSetPriority() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.IMPORT_BUS);
        
        node.setPriority(10);
        
        assertEquals(10, node.getPriority());
    }
    
    @Test
    @DisplayName("Get connected neighbors")
    void testGetConnectedNeighbors() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(5, 10, 15);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        node.addConnection(Direction.NORTH);
        node.addConnection(Direction.UP);
        
        BlockPos[] neighbors = node.getConnectedNeighbors();
        
        assertEquals(2, neighbors.length);
        assertTrue(containsPosition(neighbors, new BlockPos(5, 10, 14))); // North = -Z
        assertTrue(containsPosition(neighbors, new BlockPos(5, 11, 15))); // Up = +Y
    }
    
    @Test
    @DisplayName("Different device types")
    void testDeviceTypes() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        
        MENode cable = new MENode(worldId, pos, MEDeviceType.CABLE);
        MENode terminal = new MENode(worldId, pos, MEDeviceType.TERMINAL);
        MENode controller = new MENode(worldId, pos, MEDeviceType.CONTROLLER);
        
        assertEquals(MEDeviceType.CABLE, cable.getDeviceType());
        assertEquals(MEDeviceType.TERMINAL, terminal.getDeviceType());
        assertEquals(MEDeviceType.CONTROLLER, controller.getDeviceType());
    }
    
    @Test
    @DisplayName("Can associate node with network")
    void testNetworkAssociation() {
        UUID worldId = UUID.randomUUID();
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        MENetwork network = new MENetwork();
        node.setNetwork(network);
        
        assertEquals(network, node.getNetwork());
    }
    
    // Helper method
    private boolean containsPosition(BlockPos[] positions, BlockPos target) {
        for (BlockPos pos : positions) {
            if (pos.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
