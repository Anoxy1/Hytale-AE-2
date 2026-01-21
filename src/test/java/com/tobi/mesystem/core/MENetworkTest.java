package com.tobi.mesystem.core;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tobi.mesystem.util.BlockPos;

/**
 * Unit tests for MENetwork.
 * 
 * Tests core network functionality: node management, channel allocation,
 * item storage, and network merging.
 * 
 * @author Anoxy1
 * @version 0.1.0
 * @since 0.1.0
 */
@DisplayName("MENetwork Tests")
class MENetworkTest {
    
    private MENetwork network;
    private UUID worldId;
    
    @BeforeEach
    void setUp() {
        network = new MENetwork();
        worldId = UUID.randomUUID();
    }
    
    @Test
    @DisplayName("New network has unique ID")
    void testNetworkCreation() {
        assertNotNull(network);
        assertNotNull(network.getNetworkId());
        
        MENetwork network2 = new MENetwork();
        assertNotEquals(network.getNetworkId(), network2.getNetworkId());
    }
    
    @Test
    @DisplayName("New network has zero nodes")
    void testEmptyNetwork() {
        assertEquals(0, network.getNodeCount());
    }
    
    @Test
    @DisplayName("Add node increases node count")
    void testAddNode() {
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        network.addNode(node);
        
        assertEquals(1, network.getNodeCount());
    }
    
    @Test
    @DisplayName("Add multiple nodes")
    void testAddMultipleNodes() {
        for (int i = 0; i < 5; i++) {
            BlockPos pos = new BlockPos(i, 0, 0);
            MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
            network.addNode(node);
        }
        
        assertEquals(5, network.getNodeCount());
    }
    
    @Test
    @DisplayName("Remove node decreases node count")
    void testRemoveNode() {
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode node = new MENode(worldId, pos, MEDeviceType.CABLE);
        
        network.addNode(node);
        assertEquals(1, network.getNodeCount());
        
        network.removeNode(node.getPosition());
        assertEquals(0, network.getNodeCount());
    }
    
    @Test
    @DisplayName("Cannot add null node")
    void testAddNullNode() {
        assertThrows(IllegalArgumentException.class, () -> {
            network.addNode(null);
        });
    }
    
    @Test
    @DisplayName("Default max channels is 8")
    void testDefaultMaxChannels() {
        assertEquals(8, network.getMaxChannels());
    }
    
    @Test
    @DisplayName("Adding controller increases max channels to 32")
    void testControllerIncreasesChannels() {
        BlockPos pos = new BlockPos(0, 0, 0);
        MENode controller = new MENode(worldId, pos, MEDeviceType.CONTROLLER);
        
        network.addNode(controller);
        
        assertEquals(32, network.getMaxChannels());
    }
    
    @Test
    @DisplayName("New network has zero stored items")
    void testEmptyStorage() {
        assertEquals(0, network.getStoredItemCount());
    }
    
    @Test
    @DisplayName("Insert item increases stored count")
    void testInsertItem() {
        int inserted = network.insertItem("minecraft:stone", 64, null);
        
        assertEquals(64, inserted);
        assertEquals(64, network.getStoredItemCount());
    }
    
    @Test
    @DisplayName("Extract item decreases stored count")
    void testExtractItem() {
        network.insertItem("minecraft:stone", 64, null);
        
        long extracted = network.extractItem("minecraft:stone", 32L);
        
        assertEquals(32, extracted);
        assertEquals(32, network.getStoredItemCount());
    }
    
    @Test
    @DisplayName("Cannot extract more than stored")
    void testExtractMoreThanStored() {
        network.insertItem("minecraft:stone", 10, null);
        
        long extracted = network.extractItem("minecraft:stone", 20L);
        
        assertEquals(10, extracted);
        assertEquals(0, network.getStoredItemCount());
    }
    
    @Test
    @DisplayName("Merge networks combines nodes")
    void testNetworkMerge() {
        MENetwork network2 = new MENetwork();
        
        BlockPos pos1 = new BlockPos(0, 0, 0);
        BlockPos pos2 = new BlockPos(1, 0, 0);
        
        MENode node1 = new MENode(worldId, pos1, MEDeviceType.CABLE);
        MENode node2 = new MENode(worldId, pos2, MEDeviceType.CABLE);
        
        network.addNode(node1);
        network2.addNode(node2);
        
        assertEquals(1, network.getNodeCount());
        assertEquals(1, network2.getNodeCount());
        
        network.merge(network2);
        
        assertEquals(2, network.getNodeCount());
    }
    
    @Test
    @DisplayName("Merge networks combines storage")
    void testNetworkMergeStorage() {
        MENetwork network2 = new MENetwork();
        
        network.insertItem("minecraft:stone", 64, null);
        network2.insertItem("minecraft:dirt", 32, null);
        
        network.merge(network2);
        
        assertEquals(96, network.getStoredItemCount());
    }
    
    @Test
    @DisplayName("Used channels never exceeds max channels")
    void testChannelLimit() {
        // Add many import buses (each uses 1 channel)
        for (int i = 0; i < 50; i++) {
            BlockPos pos = new BlockPos(i, 0, 0);
            MENode node = new MENode(worldId, pos, MEDeviceType.IMPORT_BUS);
            network.addNode(node);
        }
        
        assertTrue(network.getUsedChannels() <= network.getMaxChannels());
    }
}
