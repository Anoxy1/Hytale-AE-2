# Hytale manifest.json Format - Januar 2026

**Quelle:** Offizielle Hytale Docs, Community Templates, Working Plugins (Januar 2026)

## Korrektes Format für Java Plugins

### Mit Main Class (Java Plugin mit Code)

```json
{
  "Group": "com.example",
  "Name": "Plugin Name",
  "Version": "1.0.0",
  "Main": "com.example.plugin.MainClass",
  "Description": "Plugin description",
  "Authors": [
    {
      "Name": "Author Name",
      "Email": "",
      "Url": ""
    }
  ],
  "Website": "",
  "Dependencies": {},
  "OptionalDependencies": {},
  "LoadBefore": {},
  "DisabledByDefault": false,
  "IncludesAssetPack": true,
  "SubPlugins": []
}
```

### Ohne Main Class (Asset Pack / Mod)

```json
{
  "Group": "Author",
  "Name": "Mod Name",
  "Version": "1.0.0",
  "Description": "Mod description",
  "Authors": [
    {
      "Name": "Author Name",
      "Email": "",
      "Url": ""
    }
  ],
  "Website": "",
  "Dependencies": {},
  "OptionalDependencies": {},
  "LoadBefore": {},
  "DisabledByDefault": false,
  "IncludesAssetPack": false,
  "SubPlugins": []
}
```

## Wichtige Unterschiede zu Standard Plugin-Manifests

### ✅ Korrekt (Hytale Format)
- `"Group"` - PascalCase, nicht "id" (Author/Organization)
- `"Name"` - PascalCase, nicht "name" (Display Name)
- `"Version"` - PascalCase, Semantic Versioning
- `"Main"` - **ERFORDERLICH für Java Plugins** - Vollständiger Klassenpfad
- `"Authors"` - Array von Objekten mit Name/Email/Url
- `"Dependencies"` - Leeres Objekt `{}`
- `"IncludesAssetPack"` - `true` wenn JSON assets vorhanden sind

### ❌ Falsch (Standard Plugin Format)
- `"id": "modid"` - existiert nicht in Hytale
- `"name": "Mod Name"` - lowercase (falsch!)
- `"version": "1.0.0"` - lowercase (falsch!)
- `"authors": ["String"]` - nur Strings statt Objekte
- `"main"` fehlt bei Java Plugins - **Plugin wird nicht laden!**

## Kritische Fehler & Lösungen

### ❌ Fehler: "Failed to load plugin at com.hypixel.hytale.server.core.plugin..."

**Ursache:** `"Main"` Feld fehlt in manifest.json

**Lösung:**
```json
{
  "Main": "com.tobi.mesystem.MEPlugin",
  ...
}
```

### ❌ Fehler: "Failed to validate asset! Key: Name Results: FAIL: Can't be null!"

**Ursache:** Lowercase field names (falsche Kapitalisierung)

**Lösung:** Verwende PascalCase:
```json
// FALSCH:
{"name": "My Plugin"}

// RICHTIG:
{"Name": "My Plugin"}
```

### ❌ Fehler: "NoClassDefFoundError: com.example.MyPlugin"

**Ursache:** Main class path ist falsch oder Klasse existiert nicht

**Lösung:** 
1. Prüfe dass die Klasse existiert: `src/main/java/com/tobi/mesystem/MEPlugin.java`
2. Verwende vollständigen Package-Pfad: `"Main": "com.tobi.mesystem.MEPlugin"`
3. Stelle sicher dass die Klasse `JavaPlugin` extended:
```java
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class MEPlugin extends JavaPlugin {
    public MEPlugin(JavaPluginInit init) {
        super(init);
    }
}
```

## Plugin Architecture & Requirements

### JavaPlugin Base Class

**Alle Plugins MÜSSEN von `JavaPlugin` erben:**

```java
package com.tobi.mesystem;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;

public class MEPlugin extends JavaPlugin {
    
    // ERFORDERLICH: Konstruktor mit JavaPluginInit parameter
    public MEPlugin(@Nonnull JavaPluginInit init) {
        super(init);  // KRITISCH: super() call
    }
    
    @Override
    protected void setup() {
        // Phase 1: Registrierung (Commands, Events, BlockStates)
        getLogger().info("Plugin setup...");
    }
    
    @Override
    protected void start() {
        // Phase 2: Start nach allen setup() calls
        getLogger().info("Plugin started!");
    }
    
    @Override
    protected void shutdown() {
        // Phase 3: Cleanup beim Herunterfahren
        getLogger().info("Plugin shutdown...");
    }
}
```

### Plugin Lifecycle

1. **Constructor** - `new YourPlugin(JavaPluginInit init)` - Instanziierung
2. **setup()** - Registriere Commands, Events, Codecs, BlockStates
3. **start()** - Nach allen Plugins' setup(), starte Services
4. **shutdown()** - Cleanup, speichere Daten

### Wichtige API Methoden

```java
// Logging
getLogger().info("Message");
getLogger().warn("Warning");
getLogger().error("Error", exception);

// Registries
getCommandRegistry().registerCommand(new MyCommand());
getEventRegistry().register(MyEvent.class, handler);
getBlockStateRegistry().registerBlockState(...);
getCodecRegistry().register("key", MyClass.class, CODEC);

// Config
getDataFolder();  // plugins/YourPlugin/ directory
```

## Beispiele aus funktionierenden Mods

### Bonnibel - One Log Four Planks
```json
{
  "Group": "Bonnibel",
  "Name": "Bonnibel - One Log Four Planks",
  "Version": "1.0.0",
  "Description": "A simple mod that changes vanilla wood crafting...",
  "Authors": [
    {
      "Name": "Bonnibel",
      "Email": "",
      "Url": ""
    },
    {
      "Name": "Megadoger",
      "Email": "",
      "Url": ""
    }
  ],
  "Website": "",
  "Dependencies": {},
  "OptionalDependencies": {},
  "LoadBefore": {},
  "DisabledByDefault": false,
  "IncludesAssetPack": false,
  "SubPlugins": []
}
```

### HytaleAE2 (ME System)
```json
{
  "Group": "Tobi",
  "Name": "ME System",
  "Version": "0.2.0",
  "Description": "Applied Energistics 2 für Hytale - ME Network Storage & Transport System",
  "Authors": [
    {
      "Name": "Tobi",
      "Email": "",
      "Url": ""
    }
  ],
  "Website": "",
  "Dependencies": {},
  "OptionalDependencies": {},
  "LoadBefore": {},
  "DisabledByDefault": false,
  "IncludesAssetPack": false,
  "SubPlugins": []
}
```

## Hinweise

1. **PascalCase ist Pflicht!** Alle Felder müssen mit Großbuchstaben beginnen
2. **Authors ist immer ein Array von Objekten**, nie ein String-Array
3. **Main-Feld ist nur für Java Plugins erforderlich**, nicht für Asset Packs
4. **IncludesAssetPack: true** wenn du JSON files in `Server/` oder `Common/` hast
5. **Dependencies**: Syntax noch nicht vollständig dokumentiert (verwende leeres Objekt)

## Offizielle Template-Projekte

### Kaupenjoe's Hytale Example Plugin
- GitHub: https://github.com/Kaupenjoe/Hytale-Example-Plugin
- Tutorial: https://www.youtube.com/watch?v=qKI_6gFmnzA (16. Januar 2026)
- Includes: Development environment setup, build configuration, example code

### realBritakee's Hytale Template Plugin
- GitHub: https://github.com/realBritakee/hytale-template-plugin
- Features: Automated testing, CI/CD workflow, Gradle plugin for server testing
- Minimal ready-to-use template mit modern build tools

### Gradle Plugin für manifest.json
- Plugin: https://plugins.gradle.org/plugin/hytale-manifest (v1.0.22, 14. Januar 2026)
- Automatische manifest.json Generierung aus build.gradle

## System Requirements

- **Java:** Version 25 (LTS)
- **Architectures:** x64 und arm64 unterstützt
- **Server:** Hytale Dedicated Server (Early Access, Januar 2026)

## Best Practices

1. **@Nonnull Annotations** verwenden für JavaPluginInit parameter
2. **Try-Catch in setup()** um Plugin-Loading-Failures zu vermeiden
3. **Singleton Pattern** für getInstance() bei Bedarf
4. **Thread-Safe Code** wenn mehrere Threads genutzt werden
5. **Proper Logging** mit getLogger() statt System.out.println()

## Quellen

1. **PascalCase ist Pflicht** - Alle Hauptfelder beginnen mit Großbuchstaben
2. **Authors ist immer ein Array** - Auch bei nur einem Autor
3. **Leere Felder sind erlaubt** - Email und Url können leer bleiben
4. **Dependencies** - Aktuell scheint es keine Dependency-Syntax zu geben
5. **SubPlugins** - Für modulare Mods (noch nicht dokumentiert)

## Quelle

Diese Information wurde durch:
- Reverse Engineering funktionierender Mods
- Analyse von Hytale Early Access (Januar 2026)
- Trial & Error beim Plugin-Loading

Stand: Januar 20, 2026
