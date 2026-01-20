# Hytale Plugin Fix Guide

## Problem
Das Plugin lädt nicht, obwohl es im Mods-Ordner ist. Die Items existieren (aus den JSON-Dateien), aber der Java-Code wird nicht ausgeführt.

## Wahrscheinliche Ursache
**Hytale unterscheidet zwischen:**
1. **Mods** (Asset-Packs) - JSON-Dateien für Items/Blocks
2. **Plugins** (Java-Code) - Muss anders deployed werden

## Lösung 1: Plugin als Classpath-Plugin deployen

Plugins müssen eventuell direkt im Server-Classpath sein, nicht als JAR im Mods-Ordner.

### Testen:
```bash
# 1. Entpacke das Plugin JAR
cd C:\Users\tobia\AppData\Roaming\Hytale\UserData\Mods
mkdir HytaleAE2_extracted
cd HytaleAE2_extracted
jar xf ../HytaleAE2-0.1.0-SNAPSHOT.jar

# 2. Kopiere die Klassen in den Server-Classpath (wenn möglich)
# ODER erstelle ein Plugin im Hytale-Format
```

## Lösung 2: Erstelle META-INF/services Eintrag

Hytale könnte ServiceLoader verwenden. Erstelle:
`META-INF/services/com.hypixel.hytale.server.core.plugin.JavaPlugin`

Inhalt:
```
com.tobi.mesystem.MEPlugin
```

## Lösung 3: Plugin-Descriptor

Manche Plugin-Systeme brauchen einen plugin.yml oder plugin.json zusätzlich zur manifest.json.

## Lösung 4: Nur Asset-Pack verwenden

Da die Items bereits funktionieren (aus den JSON-Dateien), können wir:
1. Das Asset-Pack im Mods-Ordner lassen (für Items/Blocks/Texturen)
2. Auf Java-Plugin verzichten (keine Events, nur statische Blöcke)
3. Oder: Minimales Plugin als Hytale-Example nachbauen

## Nächster Schritt
Schaue in die Hytale Plugin-Examples oder erstelle ein minimales Test-Plugin nach dem offiziellen Format.
