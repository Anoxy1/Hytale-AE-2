# Next Steps - HytaleServer.jar Integration

## Current Status
✅ JSON block definitions complete  
✅ BlockState stub classes created  
✅ Asset pack structure implemented  
✅ MEPlugin registration method ready  
⏳ **NEXT**: Add HytaleServer.jar for full BlockState implementation

---

## Step 1: Obtain HytaleServer.jar

### Option A: Extract from Hytale Installation
```powershell
# Hytale Early Access includes HytaleServer.jar in installation
# Look in: C:\Users\<username>\AppData\Roaming\Hytale\install\<version>\
```

### Option B: Use Decompiled Version
```powershell
# If you decompiled HyPipes or ChestTerminal, the JAR may be in their libs/
# Check: libs/com/hypixel/hytale/server/core/plugin/
```

### Option C: Server Distribution
```powershell
# Hytale Dedicated Server includes HytaleServer.jar
# Download from Hytale website when available
```

---

## Step 2: Add to Project

### 2.1 Copy JAR to libs/
```powershell
# Create directory structure
mkdir libs\com\hypixel\hytale\server\core\plugin

# Copy JAR
copy <source>\HytaleServer.jar libs\com\hypixel\hytale\server\core\plugin\
```

### 2.2 Update build.gradle
Add to dependencies section:
```gradle
dependencies {
    // Existing dependencies
    implementation 'org.apache.logging.log4j:log4j-core:2.20.0'
    
    // Add HytaleServer.jar
    compileOnly files('libs/com/hypixel/hytale/server/core/plugin/HytaleServer.jar')
}
```

**Note**: Use `compileOnly` not `implementation` - Hytale provides it at runtime!

### 2.3 Rebuild
```powershell
.\gradlew clean build
```

---

## Step 3: Implement Codecs

### 3.1 Import Required Classes
Add to each BlockState file:
```java
import com.hypixel.hytale.server.core.block.BlockState;
import com.hypixel.hytale.server.core.codec.BuilderCodec;
import com.hypixel.hytale.server.core.codec.Codec;
```

### 3.2 Update MEControllerBlockState
```java
public class MEControllerBlockState extends BlockState {
    public static final BuilderCodec<MEControllerBlockState> CODEC = 
        BuilderCodec.of(MEControllerBlockState::new)
            .withField("active", Codec.BOOL, state -> state.active)
            .build();
    
    private final boolean active;
    
    public MEControllerBlockState(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public MEControllerBlockState setActive(boolean active) {
        return new MEControllerBlockState(active);
    }
}
```

### 3.3 Update METerminalBlockState
```java
import com.hypixel.hytale.server.core.block.data.ItemContainerStateData;

public class METerminalBlockState extends BlockState {
    public static final BuilderCodec<METerminalBlockState> CODEC = 
        BuilderCodec.of(METerminalBlockState::new).build();
    
    public METerminalBlockState() {
        // Terminal uses ItemContainerStateData for inventory
    }
}
```

### 3.4 Update MECableBlockState
```java
public class MECableBlockState extends BlockState {
    public static final BuilderCodec<MECableBlockState> CODEC = 
        BuilderCodec.of(MECableBlockState::new)
            .withField("connections", Codec.INT, state -> state.connections)
            .build();
    
    private final int connections; // Bitmask: 0-63 for 6 directions
    
    public MECableBlockState(int connections) {
        this.connections = connections;
    }
    
    public int getConnections() {
        return connections;
    }
    
    public MECableBlockState setConnections(int connections) {
        return new MECableBlockState(connections);
    }
}
```

---

## Step 4: Enable Registration

### 4.1 Uncomment MEPlugin.registerBlockStates()
Remove the `/* ... */` comment block around registration code.

### 4.2 Add Imports to MEPlugin
```java
import com.hypixel.hytale.server.core.block.BlockStateRegistry;
import com.hypixel.hytale.server.core.block.data.ItemContainerStateData;
import com.hypixel.hytale.server.core.interaction.Interaction;
import com.tobi.mesystem.blocks.state.MEControllerBlockState;
import com.tobi.mesystem.blocks.state.METerminalBlockState;
import com.tobi.mesystem.blocks.state.MECableBlockState;
```

### 4.3 Implement getBlockStateRegistry()
Add to MEPlugin:
```java
private BlockStateRegistry getBlockStateRegistry() {
    try {
        return (BlockStateRegistry) getClass()
            .getMethod("getBlockStateRegistry")
            .invoke(this);
    } catch (Exception e) {
        throw new RuntimeException("Failed to get BlockStateRegistry", e);
    }
}
```

---

## Step 5: Build & Test

### 5.1 Clean Build
```powershell
.\gradlew clean build
```

### 5.2 Deploy
```powershell
copy build\libs\HytaleAE2-0.1.0-SNAPSHOT.jar C:\Users\tobia\AppData\Roaming\Hytale\UserData\Mods\
```

### 5.3 Test in Hytale
1. Launch Hytale Single Player
2. Check log for:
   ```
   → Registriere BlockStates...
     ✓ ME Controller BlockState registered
     ✓ ME Terminal BlockState registered
     ✓ ME Cable BlockState registered
   ✓ BlockState-Registry aktiviert
   ```
3. Create new world
4. Enter Creative Mode: `/gamemode creative`
5. Check inventory for ME blocks

---

## Step 6: Add Models & Textures

### 6.1 Create Models Directory
```powershell
mkdir src\main\resources\Client\Models
```

### 6.2 Create Textures Directory
```powershell
mkdir src\main\resources\Client\Textures
```

### 6.3 Create Basic Models
Use Hytale Model Maker or Blockbench with Hytale export:
- `MEController.blockymodel` - 16x16x16 cube with glowing center
- `METerminal.blockymodel` - 16x16x16 cube with screen face
- `MECable_Straight.blockymodel` - 6x6x16 cable
- `MECable_Corner.blockymodel` - 6x6x10 L-shape
- `MECable_T.blockymodel` - 6x6x10 T-shape
- `MECable_Cross.blockymodel` - 6x6x6 + cross shape

### 6.4 Create Textures
16x16 PNG files:
- `MEController.png` - Dark gray with blue energy pattern
- `METerminal.png` - Dark gray with green screen
- `MECable.png` - Light gray metallic

### 6.5 Update JSON Definitions
Add Model references to JSON files:
```json
{
  "Id": "HytaleAE2:ME_Controller",
  "Model": {
    "Path": "Client/Models/MEController.blockymodel"
  },
  "State": {
    "Definitions": {
      "Id": "ME_Controller"
    }
  }
}
```

---

## Expected Results

### Console Output
```
╔════════════════════════════════════════════════════════════╗
║         ME System - Setup & Initialisierung                ║
╚════════════════════════════════════════════════════════════╝
→ Starte NetworkManager...
  ✓ NetworkManager initialisiert
→ Registriere BlockStates...
  ✓ ME Controller BlockState registered
  ✓ ME Terminal BlockState registered
  ✓ ME Cable BlockState registered
  ✓ Terminal Interaction registered
✓ BlockState-Registry aktiviert
→ Registriere Event-Listener...
  ✓ Event-Registry aktiviert
✓ ME System Setup erfolgreich abgeschlossen
╔════════════════════════════════════════════════════════════╗
║         ME System erfolgreich gestartet! 🚀                 ║
╚════════════════════════════════════════════════════════════╝
```

### In-Game
- ME Controller appears in Creative inventory
- ME Terminal appears in Creative inventory
- ME Cable appears in Creative inventory
- All blocks can be placed in world
- Models render correctly

---

## Troubleshooting

### Compile Error: Cannot find symbol BlockState
**Solution**: Ensure HytaleServer.jar is in libs/ and build.gradle has correct path

### Runtime Error: NoClassDefFoundError
**Solution**: Change `implementation` to `compileOnly` in build.gradle - Hytale provides classes at runtime

### Blocks Don't Appear in Inventory
**Solution**: Check JSON files are in correct location: `src/main/resources/Server/Item/Items/`

### Registration Fails
**Solution**: Verify State.Definitions.Id in JSON matches registration string in Java

### Models Don't Load
**Solution**: Check Model.Path in JSON matches actual file location

---

## Files to Modify

### Must Edit
1. `build.gradle` - Add HytaleServer.jar dependency
2. `MEControllerBlockState.java` - Implement Codec
3. `METerminalBlockState.java` - Implement Codec
4. `MECableBlockState.java` - Implement Codec
5. `MEPlugin.java` - Uncomment registration code

### Should Create
6. `Client/Models/*.blockymodel` - Block models
7. `Client/Textures/*.png` - Block textures

### Can Update (Optional)
8. JSON files - Add Model references
9. items.lang - Add more translations

---

## Success Criteria

- [ ] Project compiles with HytaleServer.jar
- [ ] No runtime errors in console
- [ ] All 3 blocks appear in Creative inventory
- [ ] Blocks can be placed without errors
- [ ] Block models render (even if basic cubes)
- [ ] Breaking blocks drops correct items
- [ ] Network formation detected (Controller + Terminal)

---

**Ready to proceed?** Get HytaleServer.jar and follow Step 2!

**Questions?** Check [BLOCKSTATE_REGISTRY_GUIDE.md](BLOCKSTATE_REGISTRY_GUIDE.md)

**Status**: ⏳ Awaiting HytaleServer.jar
