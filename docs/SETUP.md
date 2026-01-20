# HytaleAE2 - Setup Guide

**Version:** 0.1.0-SNAPSHOT  
**Status:** Foundation Complete ✅  
**Last Updated:** Januar 2026

---

## 🎯 Quick Start

### Prerequisites
- Java Development Kit (JDK) 17+
- Gradle 8.0+ (oder Gradle Wrapper)
- IDE (IntelliJ IDEA oder VS Code empfohlen)
- Hytale Client/Server (für Testing)

---

## 📦 Installation

### 1. Project Setup

```bash
# Clone Repository
git clone https://github.com/Anoxy1/Hytale-AE-2.git
cd Hytale-AE-2

# Oder: Manuell erstellen
mkdir HytaleAE2
cd HytaleAE2
```

### 2. Dependencies Setup

**Option A: Wenn libs/ leer ist**
```bash
# Kopiere die Referenz-Plugins ins libs/ Verzeichnis
copy path\to\ChestTerminal-2.0.8.jar libs\
copy path\to\HyPipes-1.0.5-SNAPSHOT.jar libs\
```

**Option B: Wenn HytaleServer.jar verfügbar**
```bash
# Kopiere HytaleServer.jar nach libs/
copy path\to\HytaleServer.jar libs\
```

# Setup

Authoritative environment setup based on Hytale Server Manual and HelloPlugin.

## Requirements

- Java 25 (Temurin recommended)
- HytaleServer.jar (server API)

## Obtain Server Files

Option 1 (Launcher):
- `build/libs/HytaleAE2-0.1.0-SNAPSHOT.jar`


## 🛠️ Development Environment

### IntelliJ IDEA Setup

1. **Import Project**
   ```
   File → Open → Select HytaleAE2 folder
   ```

2. **Configure JDK**
   Set SDK to JDK 17+
   ```

3. **Gradle Configuration**
   ```
   View → Tool Windows → Gradle
   Reload All Gradle Projects
   ```

4. **Run Configuration**
   - Create new "Application" configuration
   - Main class: `com.tobi.mesystem.MEPlugin`
### VS Code Setup

1. **Install Extensions**
   - Extension Pack for Java
   - Gradle for Java

2. **Open Project**
   ```bash
   code .
   ```

3. **Configure Java**
   - Press `Ctrl+Shift+P`
   - Select "Java: Configure Java Runtime"
   - Set JDK 17+

---

## 📂 Project Structure

```
HytaleAE2/
├── src/
│   └── main/
│       ├── java/com/tobi/mesystem/
│       │   ├── MEPlugin.java           # Main Plugin Class
│       │   ├── blocks/                 # Block Implementations
│       │   │   ├── MECableBlock.java
│       │   │   ├── MEControllerBlock.java
│       │   │   └── METerminalBlock.java
│       │   ├── core/                   # Core Systems ✅
│       │   │   ├── MENetwork.java
│       │   │   ├── MENode.java
│       │   │   └── MEDeviceType.java
│       │   ├── events/                 # Event Handlers
│       │   ├── gui/                    # GUI Systems
│       │   └── util/                   # Utilities ✅
│       │       ├── BlockPos.java
│       │       └── Direction.java
│       └── resources/
│           ├── manifest.json           # Plugin Manifest ✅
│           └── Server/                 # Asset Pack ✅
│               ├── Item/Items/
│               └── Languages/en-US/
├── build.gradle                        # Build Configuration ✅
├── gradle.properties                   # Gradle Settings ✅
├── settings.gradle                     # Project Settings ✅
└── docs/                               # Documentation
```

---

## 🚀 Deployment

### Single Player Installation

```bash
# Windows
copy build\libs\HytaleAE2-0.1.0-SNAPSHOT.jar %APPDATA%\Hytale\UserData\Mods\

# Linux/Mac
cp build/libs/HytaleAE2-0.1.0-SNAPSHOT.jar ~/Library/Application\ Support/Hytale/UserData/Mods/
```

### Dedicated Server Installation

```bash
# Copy to server plugins directory
copy build\libs\HytaleAE2-0.1.0-SNAPSHOT.jar [Server-Path]\plugins\

# Restart server
java -jar HytaleServer.jar
```

---

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
.\gradlew test

# Run specific test class
.\gradlew test --tests MENetworkTest

# Generate coverage report
.\gradlew test jacocoTestReport
```

### Manual Testing
1. Build plugin: `.\gradlew build`
2. Copy JAR to Hytale Mods folder
3. Launch Hytale
4. Check console for "ME System started!" message
5. Place ME blocks in-game (when implemented)

---

## 🔧 Configuration

### build.gradle Key Settings

```gradle
// Hytale API Version
compileOnly files('libs/HytaleServer.jar')

// Reference Plugins (for development)
compileOnly files('libs/ChestTerminal-2.0.8.jar')
compileOnly files('libs/HyPipes-1.0.5-SNAPSHOT.jar')

// Shadow Plugin (creates fat JAR)
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}
```

### manifest.json Configuration

```json
{
  "Id": "HytaleAE2",
  "Version": "0.1.0-SNAPSHOT",
  "LoadOn": "server",
  "IncludesAssetPack": true,
  "JavaPlugin": "com.tobi.mesystem.MEPlugin"
}
```

---

## 📋 Checklists

### Initial Setup ✅
- [x] JDK 17+ installed
- [x] Gradle Wrapper initialized
- [x] Project structure created
- [x] manifest.json configured
- [x] build.gradle configured
- [x] Core classes implemented

### Development Ready
- [x] IDE configured
- [x] Dependencies resolved
- [x] First build successful
- [ ] Unit tests written
- [ ] Integration tests configured
- [ ] Debug configuration set up

### Deployment Ready (Pending)
- [x] Plugin loads successfully
- [x] Logging works
- [x] Error handling implemented
- [ ] Blocks register in-game
- [ ] GUIs functional
- [ ] Network system tested

---

## 🐛 Troubleshooting

### Build Fails
**Problem:** `Cannot find HytaleServer.jar`  
**Solution:** Ensure HytaleServer.jar is in `libs/` directory, or use reference plugins as fallback

### Plugin Not Loading
**Problem:** Plugin doesn't appear in Hytale  
**Solution:** 
1. Check manifest.json `JavaPlugin` path is correct
2. Verify JAR is in correct Mods/plugins folder
3. Check server logs for errors

### IDE Doesn't Recognize Classes
**Problem:** Red underlines in code  
**Solution:**
1. Reload Gradle project
2. Invalidate caches and restart IDE
3. Verify JDK is configured correctly

### Blocks Don't Register
**Problem:** "Block not found" in logs  
**Solution:**
1. Verify JSON definitions in `Server/Item/Items/`
2. Check `State.Definitions.Id` matches registration ID
3. Ensure `IncludesAssetPack: true` in manifest.json

---

## 📚 Next Steps

### For Development
1. Read [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) for implementation roadmap
2. Review [API_REFERENCE.md](API_REFERENCE.md) for Hytale API documentation
3. Check [TESTING_GUIDE.md](TESTING_GUIDE.md) for testing procedures

### For Implementation
1. Implement block Codec definitions (requires HytaleServer.jar)
2. Create GUI systems (reference ChestTerminal patterns)
3. Wire up block interaction events
4. Test network formation in-game

---

## 🤝 Contributing

### Code Style
- Follow Java naming conventions
- Use JavaDoc for public methods
- Keep classes under 500 lines
- Write unit tests for core logic

### Documentation
- Update IMPLEMENTATION_STATUS.md when completing features
- Document API changes in API_REFERENCE.md
- Add examples to DEVELOPMENT_GUIDE.md

---

## 📞 Support & Resources

### Documentation
- [README.md](../README.md) - Project overview
- [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) - Best practices guide
- [INDEX.md](../INDEX.md) - Documentation index

### Community
- Hytale Modding Discord
- GitHub Issues
- Community Forums

---

**Status:** Setup Complete - Ready for Development! 🚀
