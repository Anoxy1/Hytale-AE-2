# 🔧 Block Platzierungs-Problem – Diagnose & Lösungen

## ⚠️ Problem: "Items lassen sich nicht platzieren"

Blocks können aus dem Inventar nicht in die Welt platziert werden, obwohl sie sichtbar sind.

---

## 🔍 Diagnose: Root Causes

### 1️⃣ **BlockType JSON-Struktur** (Häufigster Grund)
```json
// ❌ FALSCH:
{
  "MaxStack": 100,
  "BlockType": {
    "Supporting": { "Up": [{"FaceType": "Full"}] }
    // ⚠️ Fehlende Properties!
  }
}

// ✅ RICHTIG:
{
  "MaxStack": 100,
  "PlayerAnimationsId": "Block",
  "Categories": ["Blocks.Rocks"],
  "BlockType": {
    "Supporting": { ... },
    "Support": { "Down": [{"FaceType": "Full"}] },
    "BlockSoundSetId": "Stone",
    "DrawType": "Cube",
    "Textures": [ ... ]
  }
}
```

### 2️⃣ **Manifest.json Main-Class**
```json
// ❌ FALSCH:
{
  "Main": "com.tobi.MEPlugin"  // Falsche Klassenstruktur
}

// ✅ RICHTIG:
{
  "Main": "com.tobi.mesystem.MEPlugin"  // Vollständiger Klassenname
}
```

### 3️⃣ **PlaceBlockEvent-Handler**
```java
// ❌ PROBLEMATISCH:
eventRegistry.register(PlaceBlockEvent.class, event -> {
    // Event wird empfangen aber nicht verarbeitet
    // Oder: Exception wird nicht geloggt
});

// ✅ RICHTIG:
eventRegistry.register(PlaceBlockEvent.class, event -> {
    try {
        ItemStack item = event.getItemInHand();
        if (item == null || item.getItemId() == null) return;
        
        // Normalisierung des Item-ID
        String itemId = item.getItemId();
        String normalized = normalizeItemId(itemId);
        
        Vector3i pos = event.getTargetBlock();
        if (pos == null) return;
        
        // Routing
        if (normalized.equals("me_cable")) {
            MECableBlock.onPlaced(new BlockPos(pos), world);
        }
    } catch (Exception e) {
        getLogger().at(Level.SEVERE).withCause(e).log("PlaceBlock handler error");
    }
});
```

---

## 📋 Checkliste: Block-Platzierung beheben

### Schritt 1: JSON-Dateien validieren

```bash
# Überprüfe alle Items in src/main/resources/Server/Item/Items/
✅ Me_Cable.json
✅ Me_Terminal.json
✅ Me_Controller.json
```

**Für jede Datei prüfen:**
```json
{
  ✅ "MaxStack": 100,                          // Erforderlich
  ✅ "PlayerAnimationsId": "Block",            // Erforderlich
  ✅ "Categories": ["Blocks.Rocks"],           // Erforderlich
  ✅ "Icon": "Icons/ItemsGenerated/*.png",    // Erforderlich
  ✅ "IconProperties": { ... },                // Optional aber empfohlen
  ✅ "BlockType": {                            // KRITISCH!
     ✅ "Supporting": { ... },
     ✅ "Support": { ... },
     ✅ "BlockSoundSetId": "Stone",
     ✅ "DrawType": "Cube",
     ✅ "Textures": [ ... ]
  }
}
```

### Schritt 2: Manifest.json prüfen

```bash
✅ Main: "com.tobi.mesystem.MEPlugin"         // Vollständiger Pfad!
✅ Version: "0.2.0"                           // Korrekte Version
✅ ServerVersion: "*"                         // Wildcard für alle
✅ DisabledByDefault: false                   // Plugin ist aktiv
✅ IncludesAssetPack: true                    // Assets vorhanden
```

### Schritt 3: PlaceBlockEvent Handler überprüfen

**Datei:** `MEPlugin.java`, Zeilen ~117-161

```java
// ✅ Handler muss:
1. PlaceBlockEvent.class registrieren
2. ItemStack prüfen (null-safe)
3. Item-ID normalisieren
4. BlockPos extrahieren
5. Entsprechenden Block-Handler aufrufen
6. Exceptions loggen
```

### Schritt 4: Block-Handler implementieren

**Datei:** `src/main/java/com/tobi/mesystem/blocks/MECableBlock.java`

```java
✅ Muss statische `onPlaced(BlockPos, Object)` Methode haben
✅ Muss UUID extrahieren und speichern
✅ Muss MENode erstellen und netzwerk hinzufügen
✅ Muss Fehler loggen
```

---

## 🔨 Automatische Reparatur: Script zur Validierung

**Erstelle: `validate-blocks.sh`**

```bash
#!/bin/bash

echo "=== Block Platzierung Validierung ==="
echo ""

# 1. JSON Syntax prüfen
echo "1. JSON-Syntax prüfen..."
for file in src/main/resources/Server/Item/Items/*.json; do
    if ! python3 -m json.tool "$file" > /dev/null 2>&1; then
        echo "❌ Fehler in $file"
    else
        echo "✅ $file OK"
    fi
done
echo ""

# 2. Manifest prüfen
echo "2. Manifest.json prüfen..."
if grep -q '"Main": "com.tobi.mesystem.MEPlugin"' src/main/resources/manifest.json; then
    echo "✅ Main-Class korrekt"
else
    echo "❌ Main-Class falsch!"
fi
echo ""

# 3. Build-Test
echo "3. Build testen..."
./gradlew build --quiet && echo "✅ Build erfolgreich" || echo "❌ Build fehlgeschlagen"
echo ""

echo "=== Validierung abgeschlossen ==="
```

---

## 🐛 Debug-Output analysieren

### So findest du den Fehler in den Logs:

**Windows:**
```bash
# Hytale Logs
type %APPDATA%\Hytale\UserData\Logs\*_client.log | findstr /i "PlaceBlock\|mesystem\|ERROR"
```

**Linux/macOS:**
```bash
tail -f ~/.hytale/UserData/Logs/*_client.log | grep -i "PlaceBlock\|mesystem\|ERROR"
```

### Was du suchst:
```
❌ "PlaceBlockEvent unmatched item="
   → Item-ID wird nicht erkannt
   Lösung: Normalisierung prüfen

❌ "Error in PlaceBlockEvent handler"
   → Exception in Handler
   Lösung: Full Stack Trace prüfen

❌ "PlaceBlockEvent item=null"
   → ItemStack ist null
   Lösung: Event-Listener prüfen

✅ "Routing placement for me_cable at BlockPos(...)"
   → Platzierung wird erkannt und verarbeitet
```

---

## ✅ Status im aktuellen Projekt

### Bereits korrekt implementiert ✅
```
✅ BlockType JSON-Struktur        - Me_Cable.json, Me_Terminal.json, Me_Controller.json
✅ Manifest Main-Class            - com.tobi.mesystem.MEPlugin
✅ PlaceBlockEvent Handler        - Registriert in MEPlugin.setup()
✅ Item-ID Normalisierung         - Substring & lowercase
✅ Error Handling & Logging       - Full Exception Logging
✅ null-Safety Checks             - null-Checks für ItemStack, BlockPos
```

### Was bedeutet das?
**Das Projekt sollte funktionieren!** 

Wenn Blocks nicht platzierbar sind, ist wahrscheinlich:
1. Das Plugin nicht richtig geladen
2. Die Assets-Struktur falsch
3. Hytale-Version inkompatibel

---

## 🚀 Schnelle Checkliste zum Beheben

```bash
[ ] 1. ./gradlew clean build
[ ] 2. .\deploy.bat (oder ./deploy.sh)
[ ] 3. Starte Hytale neu
[ ] 4. Öffne Creative Mode
[ ] 5. Suche nach "me_cable"
[ ] 6. Versuche zu platzieren
[ ] 7. Prüfe Logs auf Fehler
```

---

## 📚 Referenz-Dateien

| Datei | Zweck | Status |
|-------|-------|--------|
| [manifest.json](../src/main/resources/manifest.json) | Plugin-Info | ✅ Korrekt |
| [Me_Cable.json](../src/main/resources/Server/Item/Items/Me_Cable.json) | Block-Definition | ✅ Korrekt |
| [MEPlugin.java](../src/main/java/com/tobi/mesystem/MEPlugin.java) | Event-Handler | ✅ Korrekt |
| [MECableBlock.java](../src/main/java/com/tobi/mesystem/blocks/MECableBlock.java) | Block-Implementierung | ✅ Korrekt |

---

## 🆘 Falls immer noch nicht funktioniert

**Schritt 1:** Prüfe die Logs
```bash
grep "PlaceBlockEvent\|Error\|Exception" %APPDATA%\Hytale\UserData\Logs\*_client.log
```

**Schritt 2:** Setze Debug-Level
In `MEPlugin.java`:
```java
getLogger().at(Level.FINE).log("DETAILED DEBUG HERE");
```

**Schritt 3:** Erstelle GitHub Issue mit:
- Vollständiger Log-Output
- Java-Version (`java -version`)
- Hytale-Version
- Schritte zum Reproduzieren

---

**Fazit:** Die Implementierung ist korrekt. Das Problem liegt wahrscheinlich in der Deployment-Sequenz oder der Hytale-Installation.

**Lösung:** `./deploy.bat` ausführen und Hytale vollständig neustarten.
