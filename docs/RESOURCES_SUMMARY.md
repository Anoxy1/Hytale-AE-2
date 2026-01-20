# Hytale Ressourcen – Durchgesuchte Inhalte & Zusammenfassung

## Offizielle Dokumentation (doc.hytaledev.fr)

### 1. Building and Running – Vollständiger Setup-Guide
**Status:** ✅ Erfolgreich durchsucht

**Kernthemen:**
- **Project Setup:** Example-Mod Repository mit automatisiertem Setup-Script
- **Tools Download:** Hytale-Downloader & CFR Decompiler
- **Server Download:** HytaleServer.jar Bezug aus Hytale-Installation
- **Reference Source Generation:** Decompilation für IDE-Unterstützung
- **Build-Prozess:** Gradle-basiert (`./gradlew build`)
- **JAR-Ausgabe:** `app/build/libs/ExamplePlugin-1.0.0.jar`
- **Server-Start:** Java-Kommando mit Assets
- **Authentication:** `/auth login device` erforderlich
- **Plugin Deployment:** JAR in `server/mods/` kopieren
- **Troubleshooting:** Java 25 Versionsanforderung, Manifest-Fehler, Permission-Issues

**Wichtige Links aus der Doku:**
- Getting Started
- Core Concepts (Registries, Assets, Codecs, Commands, Events, Tasks)
- Gameplay Systems
- World Systems
- UI Systems
- Advanced (Networking, Effects, Particles)
- Reference (API, Manifest Schema, Registries)

**Beispiel-Code:**
```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void start() {
        getEventRegistry().register(PlayerConnectEvent.class, event -> {
            PlayerRef playerRef = event.getPlayerRef();
            getLogger().at(Level.INFO).log("Player connecting: " + playerRef.getUsername());
        });
    }

    @Override
    public void shutdown() {
        getLogger().at(Level.INFO).log("Plugin is shutting down!");
    }
}
```

---

### 2. Hytale Plugin Documentation (Startseite)
**Status:** ✅ Erfolgreich durchsucht

**Inhalte:**
- Umfassende Plugin-Dokumentation für Hytale Server
- Strukturierte Kategorien für alle Aspekte der Plugin-Entwicklung
- Discord-Community Link: https://discord.gg/4UPCz84Nst

**Dokumentations-Kategorien:**
1. **Getting Started** – Umgebung einrichten, erste Plugin
2. **Core Concepts** – Registries, Assets, Codecs, Commands, Events, Tasks
3. **Gameplay Systems** – Farming, Shops, Reputation, Memories, Objectives
4. **World** – Universes, Chunks, Blocks, Entities, World Generation, Portals
5. **UI Systems** – HUD, Custom Pages, Windows, Inventory, Permissions
6. **Advanced** – Networking, Effects, Particles, Dynamic Lights
7. **Reference** – API Reference, Manifest Schema, Registries

---

### 3. Tasks & UI Endpoints
**Status:** ⚠️ 404 Not Found

- `https://doc.hytaledev.fr/en/tasks/` – nicht erreichbar
- `https://doc.hytaledev.fr/en/ui/` – nicht erreichbar
- **Alternative:** Diese sind wahrscheinlich unter `/en/core-concepts/` oder ähnlich verschoben

---

## Community-Dokumentation

### 4. Hytale Wiki – Modding (Deutsch)
**Status:** ✅ Erfolgreich durchsucht

**Kernaussagen:**
- Modding erfolgt über **Java Server Plugins**
- **Client-Modding nicht möglich** – nur Server-seitig
- Im Spiel eingebaute Modding-Tools vorhanden
- Quelle: Offizielle Aussage von @Simon_Hypixel (Twitter, 26. Januar 2019)

**Kategorien im Wiki:**
- Spielablauf (Rüstung, Blöcke, Waffen, Essen, Kreaturen, Aufträge)
- Weltgeneration (Altverse, Strukturen, Zonen)
- Inhalt im Spiel (Modelling, Modding, Musik, Avatar)
- Mehrspieler (Minispiele, Serverbrowser, Freundesliste)

---

## Projektstruktur nach offiziellem Setup

```
example-mod/
├── app/
│   ├── src/main/java/org/example/
│   │   └── ExamplePlugin.java
│   ├── src/main/resources/
│   │   └── manifest.json
│   └── build.gradle.kts
├── server/
│   ├── Server/
│   │   └── HytaleServer.jar
│   └── Assets.zip
├── src-ref/                    # Decompiled reference sources
├── .bin/                       # Downloaded tools
├── setup.sh                    # Setup automation script
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

## Technische Anforderungen

**Java-Version:** Java 25 erforderlich  
**Build-Tool:** Gradle  
**Sprache:** Java  
**Server-JAR:** HytaleServer.jar (aus Hytale-Installation bezogen)  
**Decompiler:** CFR 0.152 für Reference Sources  
**Plugin-Format:** JAR (ausführbar)

---

## Setup-Workflow (Schnellstart)

1. **Repository klonen:** `git clone https://github.com/hytale-france/example-mod.git`
2. **Tools herunterladen:** `./setup.sh --download`
3. **Server herunterladen:** `./setup.sh --setup`
4. **Reference Sources (optional):** `./setup.sh --decompile`
5. **Plugin bauen:** `./gradlew build`
6. **Plugin deployen:** JAR nach `server/mods/` kopieren
7. **Server starten:** `java -jar Server/HytaleServer.jar --assets Assets.zip`
8. **Authentifizieren:** `/auth login device`

---

## Häufige Fehler & Lösungen

| Fehler | Ursache | Lösung |
|--------|--------|--------|
| `Unsupported class file major version 69` | Falsche Java-Version | Java 25 installieren |
| `Failed to load manifest file!` | Manifest nicht in JAR | `manifest.json` in `src/main/resources/` prüfen |
| `./setup.sh: Permission denied` | Fehlende Ausführungsrechte | `chmod +x setup.sh` auf Linux/macOS |
| `No server tokens configured` | Keine Authentifizierung | `/auth login device` eingeben |

---

## Nächste Schritte (aus Dokumentation)

Nach erfolgreichem Setup empfohlene Topics:
1. Plugin Lifecycle
2. Commands erstellen
3. Events registrieren
4. World-Interaktion
5. UI-Systems
6. Advanced Networking

---

## Links zur Dokumentation

| Bereich | URL |
|--------|-----|
| Startseite | https://doc.hytaledev.fr/en/ |
| Building & Running | https://doc.hytaledev.fr/en/getting-started/building-and-running/ |
| Getting Started | https://doc.hytaledev.fr/en/getting-started |
| Core Concepts | https://doc.hytaledev.fr/en/core-concepts |
| Gameplay Systems | https://doc.hytaledev.fr/en/gameplay-systems |
| World Systems | https://doc.hytaledev.fr/en/world |
| UI Systems | https://doc.hytaledev.fr/en/ui-systems |
| Advanced | https://doc.hytaledev.fr/en/advanced |
| Reference | https://doc.hytaledev.fr/en/reference |

---

## Quellen & Referenzen

- **Offizielle Docs:** https://doc.hytaledev.fr/
- **Hytale Wiki (DE):** https://hytale.fandom.com/de/wiki/Modding
- **Official Statement:** [@Simon_Hypixel](https://twitter.com/Simon_Hypixel) (Januar 2019)
- **Discord Community:** https://discord.gg/4UPCz84Nst

---

**Durchsucht am:** 20. Januar 2026  
**Status:** ✅ Dokumentation aufrufbar und analysiert
