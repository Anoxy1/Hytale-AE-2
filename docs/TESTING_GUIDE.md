# HytaleAE2 Testing Guide

**Status**: BlockState registration complete, ready for in-game testing  
**Date**: January 20, 2026  
**Commit**: 95b6a32

---

## ✅ Pre-Test Checklist

- [x] Mod compiles successfully (BUILD SUCCESSFUL)
- [x] JAR deployed to `%AppData%\Roaming\Hytale\UserData\Mods\`
- [x] BlockState classes with correct API
- [x] BlockStateRegistry registration implemented
- [x] JSON State.Definitions.Id matches Java registration IDs
- [x] All 3 blocks registered:
  - `hytaleae2:me_controller` (MEControllerBlockState)
  - `hytaleae2:me_terminal` (METerminalBlockState)
  - `hytaleae2:me_cable` (MECableBlockState)

---

## 🧪 Test Phase 1: Mod Loading

### Start Hytale
1. Launch Hytale Early Access
2. Check logs for HytaleAE2 plugin initialization
3. Watch for registration messages

### Expected Log Output
```
[HytaleAE2] Plugin initializing...
[HytaleAE2] ✓ ME Controller BlockState registered: hytaleae2:me_controller
[HytaleAE2] ✓ ME Terminal BlockState registered: hytaleae2:me_terminal
[HytaleAE2] ✓ ME Cable BlockState registered: hytaleae2:me_cable
[HytaleAE2] Plugin loaded successfully
```

### ✅ Success Criteria
- No errors in logs
- All 3 BlockStates register successfully
- Plugin appears in installed mods list

### ❌ Failure Indicators
- `Failed to register BlockState` errors
- `State.Definitions.Id not found` errors
- Plugin fails to load

---

## 🧪 Test Phase 2: Block Registration

### Create New World
1. Click "Create World"
2. Select Creative Mode
3. Generate world (should succeed now)

### Open Creative Inventory
1. Press `E` to open inventory
2. Search for "HytaleAE2" or "ME"
3. Look in Building Blocks or Custom Items tab

### Expected Results
- **ME Controller** appears in inventory
- **ME Terminal** appears in inventory
- **ME Cable** appears in inventory
- Blocks have placeholder textures (purple/black checkerboard if no model)

### ✅ Success Criteria
- All 3 blocks are accessible in Creative inventory
- No crash when opening inventory
- Blocks have item icons (even if placeholder)

### ❌ Failure Indicators
- Blocks missing from inventory
- Game crash when searching for blocks
- "Unknown item" errors in logs

---

## 🧪 Test Phase 3: Block Placement

### Place ME Controller
1. Select ME Controller from inventory
2. Place block in world
3. Check for placement success

**Expected:**
- Block places successfully
- Block has visual representation (model or placeholder)
- No errors in console

**Test State:**
- Right-click to toggle active state (if implemented)
- Check if `active` boolean changes

### Place ME Terminal
1. Select ME Terminal from inventory
2. Place block in world
3. Right-click to open GUI (may not work yet)

**Expected:**
- Block places successfully
- Terminal has visual representation
- Right-click may show "GUI not implemented" or default inventory

### Place ME Cable
1. Select ME Cable from inventory
2. Place multiple cables next to each other
3. Check if cable connections update

**Expected:**
- Cables place successfully
- Cables may connect visually (if connection logic works)
- `connections` bitmask updates for each adjacent cable

---

## 🧪 Test Phase 4: State Persistence

### Save and Reload
1. Place all 3 block types
2. Save world
3. Exit to main menu
4. Load world again

**Expected:**
- All blocks remain in place
- Block states are preserved (active/inactive, connections)
- No "Failed to deserialize BlockState" errors

---

## 📊 Known Limitations (Current Phase)

| Feature | Status | Notes |
|---------|--------|-------|
| Block Registration | ✅ Complete | All 3 blocks registered |
| Block Placement | 🔄 To Test | Should work with placeholder visuals |
| Block Models | ❌ Not Implemented | Using placeholder models |
| Block Textures | ❌ Not Implemented | Purple/black checkerboard |
| Active State Toggle | ❌ Not Implemented | Controller boolean exists but no toggle logic |
| Cable Connections | ❌ Not Implemented | Bitmask exists but no connection logic |
| Terminal GUI | ❌ Not Implemented | No GUI handlers yet |
| Network Formation | ❌ Not Implemented | MENetwork class empty |

---

## 🐛 Debugging Common Issues

### Issue: Blocks Don't Appear in Inventory
**Cause**: State.Definitions.Id mismatch or JSON not loaded  
**Fix**: Check State.Definitions.Id in JSON matches Java registration exactly  
**Verify**: `hytaleae2:me_controller` (lowercase, no underscores)

### Issue: "State not found" Error
**Cause**: BlockState not registered or codec failed  
**Fix**: Check MEPlugin.registerBlockStates() is called  
**Verify**: Registration logs appear on startup

### Issue: Game Crash on Block Placement
**Cause**: Invalid codec or missing BASE_CODEC parent  
**Fix**: Verify BuilderCodec.builder() uses correct BASE_CODEC  
**Check**: BlockState deprecation warnings are expected (ignore)

### Issue: Block Has No Visual/Model
**Cause**: No .blockymodel file or CustomModel not found  
**Expected**: This is normal for current phase - models come next

---

## 📋 Test Results Template

```
=== HytaleAE2 Test Results ===
Date: _____________
Commit: 95b6a32

[ ] Phase 1: Mod Loading
    [ ] Plugin appears in logs
    [ ] All 3 BlockStates registered
    [ ] No errors on startup

[ ] Phase 2: Block Registration
    [ ] ME Controller in Creative inventory
    [ ] ME Terminal in Creative inventory
    [ ] ME Cable in Creative inventory

[ ] Phase 3: Block Placement
    [ ] Controller places successfully
    [ ] Terminal places successfully
    [ ] Cable places successfully

[ ] Phase 4: State Persistence
    [ ] Blocks saved correctly
    [ ] Blocks load after restart

Notes:
____________________________________
____________________________________
____________________________________
```

---

## ✅ Next Development Phase (After Testing)

### If Tests Pass:
1. **Create Block Models**
   - MEController.blockymodel (cube with glowing center)
   - METerminal.blockymodel (cube with screen face)
   - MECable.blockymodel (thin beam with connection points)

2. **Add Textures**
   - me_controller.png (tech texture with glow)
   - me_terminal.png (screen with UI elements)
   - me_cable.png (metallic conduit)

3. **Implement Block Logic**
   - Controller active state toggle
   - Cable connection updates
   - Terminal GUI handler

### If Tests Fail:
1. Analyze error logs
2. Verify State.Definitions.Id matches
3. Check codec implementation
4. Re-analyze HytaleServer.jar if API changed again

---

## 🎯 Success Metrics

**Minimum Viable Product (MVP):**
- ✅ Mod loads without errors
- ✅ All 3 blocks appear in Creative inventory
- ✅ Blocks can be placed and broken
- ✅ Block states persist after save/load

**Current Status**: Ready to test MVP requirements

**Next Milestone**: Visual polish (models + textures)

---

**Test this now**: Launch Hytale, create world, check Creative inventory!
