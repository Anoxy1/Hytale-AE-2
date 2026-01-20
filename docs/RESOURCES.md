# Hytale Plugin/Mod – Dokumentations- & Community-Ressourcen

Eine vollständige, kopierbare Gesamtliste aller Hytale Plugin-/Mod-Dokumentations- und Community-Ressourcen.

---

## Offizielle / Community-Baseline Dokumentation

**OFFIZIELLE RESSOURCEN (Hypixel Studios)**

1. https://britakee-studios.gitbook.io/hytale-modding-documentation/plugins-java-development/07-getting-started-with-plugins – **Getting Started with Plugins** (OFFIZIELL – 10-Schritt Guide, Java 25, IntelliJ IDEA, Project Structure, Gradle, Authentication, Debugging, Troubleshooting)

2. https://hytale.com/news/2025/11/hytale-modding-strategy-and-status – **Hytale Modding Strategy & Status** (OFFIZIELL Post by Slikey – Server-side first Architektur, Visual Scripting Vision, Tool-Übersicht, Community Channels)

3. https://support.hytale.com/ – Hytale Server Manual & Support (OFFIZIELL – Hytale Downloader CLI, Server Setup)

**OFFIZIELLE BEISPIEL-PLUGINS**

4. https://github.com/noel-lang/hytale-example-plugin – **HelloPlugin** (OFFIZIELL by Hypixel – einfaches Beispiel mit /hello Befehl und Title, Project Structure, Installation Guide)

5. https://github.com/Kaupenjoe/Hytale-Example-Plugin – **Kaupenjoe Hytale Plugin Template** (Community ★83 Forks – mit Decompilation Support, gradle.properties, Custom Game Dir)

**WEITERE COMMUNITY-DOKUMENTATION**

6. https://doc.hytaledev.fr/en/ – Hytale Plugin Documentation (Setup, Events, Commands, ECS, World, Inventory, Tasks, UI, Netzwerk, Permissions)

7. https://doc.hytaledev.fr/fr/getting-started/ – Hytale Plugin Docs (französisch, Einstieg)

8. https://doc.hytaledev.fr/en/getting-started/building-and-running/ – Building and Running (Setup & Deployment)

9. https://doc.hytaledev.fr/en/tasks/ – Tasks (Asynchrone Aufgaben / Task API)

10. https://doc.hytaledev.fr/en/ui/ – UI System (HUD & Custom UI)

---

## Projekt-Setup & Guides

11. https://hytale-docs.com/docs/modding/plugins/project-setup – Plugin Projekt-Setup, Manifest-Guide

12. https://hytalemodding.dev/en/docs/guides/plugin/setting-up-env – Entwicklungsumgebung einrichten (JDK, IDE, Maven/Gradle)

13. https://hytalemodding.dev/en/docs/guides/plugin/creating-commands – Commands erstellen (Java)

14. https://hytalemodding.dev/en/docs/guides/plugin/creating-events – Events erstellen / Registrierung

---

## Community-getriebene Entwickler-Docs

15. https://www.hytale-dev.com/ – Community-Docs zu Plugin-Entwicklung, Server-Setup, ECS, Java APIs

16. https://hytalemods.gg/ – Community-Hub für Mods/Plugins, Tutorials & Guides

17. https://hytaleplugins.gg/ – Community-Plugin-Hub mit Beispielen und Ressourcen

---

## IDE Plugins & Tools

18. https://plugins.jetbrains.com/plugin/25141-hytale-development-tools – **Hytale Development Tools für IntelliJ IDEA** (Project Wizard, manifest.json Generator, Debugging Support)

19. https://plugins.jetbrains.com/plugin/25142-hytale-ui-support – **Hytale .ui Support** (Syntax Highlighting für UI-Dateien)

20. https://gradle.org/plugins/de.crazydev22.hytale – **Hytale Gradle Plugin** (Version 0.1.3+, automatisiertes Plugin-Build)

## 3D Modeling & Asset Tools

21. https://www.blockbench.net/ – **Blockbench** (Official Partner für Hytale Modelle, Texturen, Animationen)

   **Blockbench für Hytale – Richtlinien:**
   - Primitive: Nur Würfel & Quads (keine komplexen Meshes)
   - Texturgröße: Vielfache von 32px (32, 64, 96, 128, etc.)
   - Pixeldichte: Character 64px/Einheit, Props/Blöcke 32px/Einheit
   - Beleuchtung: Lights/Shadows direkt in Texture einmalen
   - Vermeiden: Pure White (#FFFFFF) & Pure Black (#000000) – Stören In-Game-Beleuchtung

22. https://www.curseforge.com/hytale/mods – **CurseForge** (OFFICIAL Partner für Mod-Distribution, Installation, Management, Monetization seit Day 1)

## Tools / Bibliotheken / Alternative APIs

23. https://www.hytalejs.com/ – HytaleJS: TypeScript / JavaScript Plugin API

24. https://www.curseforge.com/hytale/mods/htdevlib – HTDevLib Utility-Library für Hytale Plugins (Hilfsklassen, Events, ECS)

25. https://github.com/Ranork/Hytale-Server-Unpacked – Dekompilierte Server-API / inoffizielle API-Referenz

---

## Weitere Community-Ressourcen

26. https://hytale.fandom.com/de/wiki/Modding – Hytale Wiki: Modding Übersicht (allgemein)

27. https://support.hytale.com/hc/en-us/articles/45326769420827-Hytale-Server-Manual – **Hytale Server Manual** (Setup, Configuration, Operation, Troubleshooting)

---

## Übersicht nach Themen

### Offizielle Ressourcen & Getting Started
- #1 (Getting Started Guide – OFFIZIELL), #2 (Modding Strategy – OFFIZIELL), #3 (Support & Server Manual)

### Offizielle Beispiel-Plugins & Templates
- #4 (HelloPlugin – OFFIZIELL), #5 (Kaupenjoe Template – Community ★83)

### Setup & Entwicklungsumgebung
- #11, #12, #18, #19, #20 (IDE Plugins & Tools)

### Plugin-API & Events
- #6, #9, #10, #13, #14

### Community-Hubs & Tutorials
- #15, #16, #17, #26

### 3D Modeling & Assets
- #21 (Blockbench – OFFIZIELL), #22 (CurseForge – OFFIZIELL Distribution)

### Tools & Bibliotheken
- #23, #24, #25

### Mehrsprachige Ressourcen
- Englisch: #1, #2, #3, #4, #5, #6, #8, #9, #10, #11, #12, #13, #14, #15, #18, #19, #20, #21, #22, #23, #24, #25, #27
- Französisch: #7
- Deutsch: #26

---

## 🎯 Empfohlener Einstiegspfad (2026)

1. **Offizielle Ressourcen zuerst**: #1 (Getting Started Guide) + #2 (Modding Philosophy)
2. **Beispiel-Code**: #4 (HelloPlugin) oder #5 (Kaupenjoe Template)
3. **API-Details**: #6, #13, #14 (nach Bedarf)
4. **Community-Support**: #15, #16, #17 (für Fragen & Best Practices)

**Alle Links sind direkt kopierbar und decken Setup, Plugin-Entwicklung, Event-Handling, Commands, UI, Tasks, Tools, Bibliotheken und Community-Ressourcen ab.**
