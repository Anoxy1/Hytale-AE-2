# HytaleAE2 - Action Items (Priorisiert)

**Basierend auf:** [DELTA_ANALYSIS.md](DELTA_ANALYSIS.md)  
**Erstellt:** 2026-01-21  
**Status:** Ready for Implementation

---

## Quick Summary

**Projekt-Status:** 75% Complete (Foundation Solid, Assets/Tests fehlen)  
**Konformität:** 85% (Code excellent, Process gaps)  
**Release-Ready:** ❌ NO (Assets + Tests required)

---

## 🔴 CRITICAL - Release Blocker (Must-Have)

### 1. Assets erstellen (4-8 Stunden)

**Was:** Texturen, Icons, 3D-Models für alle Blocks/Items  
**Warum:** Items sind aktuell unsichtbar im Spiel  
**Tool:** Blockbench (https://www.blockbench.net/)  
**Vorgaben:**
- Texturgröße: 32x32 oder 64x64 PNG (Vielfache von 32)
- Icons: 64x64 PNG
- Pixeldichte: 32px/Einheit für Blocks
- Keine Pure White (#FFFFFF) oder Pure Black (#000000)
- Beleuchtung in Textur einmalen

**Dateien:**
```
src/main/resources/Common/
├── Icons/ItemsGenerated/
│   ├── me_cable.png         # 64x64 PNG
│   ├── me_terminal.png      # 64x64 PNG
│   └── me_controller.png    # 64x64 PNG
├── BlockTextures/
│   ├── me_cable_texture.png
│   ├── me_terminal_texture.png
│   └── me_controller_texture.png
└── Models/
    ├── me_cable.blockymodel
    ├── me_terminal.blockymodel
    └── me_controller.blockymodel
```

**Acceptance Criteria:**
- [ ] Alle 3 Items haben Icons
- [ ] Alle 3 Blocks haben Texturen
- [ ] Alle 3 Blocks haben .blockymodel-Dateien
- [ ] In-Game-Test: Items sichtbar im Inventar
- [ ] In-Game-Test: Blocks korrekt gerendert

---

### 2. TESTING_GUIDE.md ausfüllen (2-3 Stunden)

**Was:** Vollständigen Testing-Guide schreiben  
**Warum:** Keine Test-Strategie dokumentiert  
**Referenz:** [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md), HelloPlugin

**Inhalte:**
```markdown
## TESTING_GUIDE.md Struktur

1. Testing Philosophy
   - Warum Tests wichtig sind
   - Test-Pyramide (Unit > Integration > E2E)

2. Unit-Tests
   - Framework: JUnit 5 + Mockito
   - Beispiele für Core-Klassen
   - Mocking Hytale API

3. Integration-Tests
   - BlockState-Registrierung testen
   - Network-Merge Szenarien

4. In-Game Manual Tests
   - Checkliste für alle Features
   - Bug-Report-Template

5. CI/CD Integration
   - Gradle Test Tasks
   - GitHub Actions Workflow
```

**Acceptance Criteria:**
- [ ] TESTING_GUIDE.md mindestens 200 Zeilen
- [ ] JUnit/Mockito Setup dokumentiert
- [ ] Mind. 3 Test-Beispiele (Code)
- [ ] In-Game Test Checkliste (10+ Items)
- [ ] CI/CD Integration erklärt

---

### 3. Unit-Tests schreiben - Core System (8-16 Stunden)

**Was:** Mindestens 50% Code-Coverage für Core-Klassen  
**Warum:** Keine Qualitätssicherung, Regression-Risiko  
**Framework:** JUnit 5 + Mockito

**build.gradle Ergänzungen:**
```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
    testImplementation 'org.mockito:mockito-core:5.7.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.7.0'
}

test {
    useJUnitPlatform()
}

plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.11"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}

test {
    finalizedBy jacocoTestReport
}
```

**Test-Struktur:**
```
src/test/java/com/tobi/mesystem/
├── core/
│   ├── MENetworkTest.java       # 10+ Tests
│   ├── MENodeTest.java          # 8+ Tests
│   └── MEDeviceTypeTest.java    # 5+ Tests
├── utils/
│   ├── BlockPosTest.java        # 6+ Tests
│   ├── DirectionTest.java       # 6+ Tests
│   └── ContainerUtilsTest.java  # 8+ Tests
└── commands/
    └── MEDebugCommandTest.java  # 4+ Tests
```

**Beispiel-Tests:**

**MENetworkTest.java:**
```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class MENetworkTest {
    private MENetwork network;
    
    @BeforeEach
    void setUp() {
        network = new MENetwork();
    }
    
    @Test
    void testNetworkCreation() {
        assertNotNull(network);
        assertEquals(0, network.getNodeCount());
    }
    
    @Test
    void testAddNode() {
        MENode node = new MENode(MEDeviceType.CABLE);
        network.addNode(node);
        assertEquals(1, network.getNodeCount());
    }
    
    @Test
    void testRemoveNode() {
        MENode node = new MENode(MEDeviceType.CABLE);
        network.addNode(node);
        network.removeNode(node);
        assertEquals(0, network.getNodeCount());
    }
    
    @Test
    void testChannelLimit() {
        // Test 8/32 channel system
        for (int i = 0; i < 10; i++) {
            MENode node = new MENode(MEDeviceType.IMPORT_BUS);
            network.addNode(node);
        }
        assertTrue(network.getUsedChannels() <= 32);
    }
    
    @Test
    void testNetworkMerge() {
        MENetwork network2 = new MENetwork();
        MENode node1 = new MENode(MEDeviceType.CABLE);
        MENode node2 = new MENode(MEDeviceType.CABLE);
        
        network.addNode(node1);
        network2.addNode(node2);
        
        network.merge(network2);
        assertEquals(2, network.getNodeCount());
    }
}
```

**BlockPosTest.java:**
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlockPosTest {
    @Test
    void testBlockPosCreation() {
        BlockPos pos = new BlockPos(10, 20, 30);
        assertEquals(10, pos.getX());
        assertEquals(20, pos.getY());
        assertEquals(30, pos.getZ());
    }
    
    @Test
    void testOffset() {
        BlockPos pos = new BlockPos(0, 0, 0);
        BlockPos offset = pos.offset(Direction.NORTH);
        assertEquals(0, offset.getX());
        assertEquals(0, offset.getY());
        assertEquals(-1, offset.getZ()); // North = -Z
    }
    
    @Test
    void testEquals() {
        BlockPos pos1 = new BlockPos(1, 2, 3);
        BlockPos pos2 = new BlockPos(1, 2, 3);
        BlockPos pos3 = new BlockPos(4, 5, 6);
        
        assertEquals(pos1, pos2);
        assertNotEquals(pos1, pos3);
    }
    
    @Test
    void testHashCode() {
        BlockPos pos1 = new BlockPos(1, 2, 3);
        BlockPos pos2 = new BlockPos(1, 2, 3);
        assertEquals(pos1.hashCode(), pos2.hashCode());
    }
}
```

**Acceptance Criteria:**
- [ ] build.gradle mit Test-Dependencies
- [ ] Mindestens 40 Tests geschrieben
- [ ] Code-Coverage mindestens 50% (Ziel: 70%)
- [ ] Alle Tests grün: `./gradlew test`
- [ ] Coverage-Report: `./gradlew jacocoTestReport`

---

## 🟠 HIGH - Wichtig für Stabilität

### 4. Integration-Tests für Core-System (4-6 Stunden)

**Was:** End-to-End Tests für Netzwerk-Logik  
**Warum:** Unit-Tests decken keine Interaktionen ab

**Test-Szenarien:**
```java
// IntegrationTest.java
class MESystemIntegrationTest {
    @Test
    void testControllerWithCablesAndTerminal() {
        // Setup
        MEControllerBlock controller = ...;
        MECableBlock cable1 = ...;
        METerminalBlock terminal = ...;
        
        // Connect
        cable1.connect(controller, terminal);
        
        // Verify
        assertTrue(controller.isActive());
        assertTrue(terminal.isConnected());
        assertEquals(1, controller.getNetwork().getUsedChannels());
    }
    
    @Test
    void testNetworkMergeViaCable() {
        // Zwei separate Netzwerke via Cable verbinden
        // -> Sollten automatisch mergen
    }
}
```

**Acceptance Criteria:**
- [ ] Mindestens 5 Integration-Tests
- [ ] Alle kritischen Netzwerk-Szenarien abgedeckt
- [ ] Tests grün

---

### 5. Config.yml implementieren (2-3 Stunden)

**Was:** Konfigurationsdatei für Runtime-Settings  
**Warum:** Aktuell sind Werte hartcodiert

**config.yml Struktur:**
```yaml
# HytaleAE2 Configuration
me-system:
  # Network Settings
  max-channels: 32          # 8 oder 32
  auto-network-merge: true
  
  # Performance
  search-radius: 16         # Block-Radius für Container-Search
  max-search-time-ms: 100
  
  # Debug
  debug-mode: false
  log-network-events: false
  
  # Features (Future)
  enable-autocrafting: false
  enable-storage-cells: false
```

**Java-Code:**
```java
public class MEConfig {
    private static MEConfig instance;
    private int maxChannels;
    private boolean autoNetworkMerge;
    private int searchRadius;
    // ...
    
    public static MEConfig load() {
        // YAML Parser (SnakeYAML)
    }
}
```

**Acceptance Criteria:**
- [ ] config.yml erstellt in resources/
- [ ] MEConfig.java implementiert
- [ ] Config-Loading in MEPlugin.onEnable()
- [ ] Dokumentation in README.md

---

### 6. Permission-System für Commands (1-2 Stunden)

**Was:** Rechte-Checks für `/medebug` und zukünftige Commands  
**Warum:** Aktuell kann jeder Debug-Commands ausführen

**Implementierung:**
```java
public class MEDebugCommand extends AbstractPlayerCommand {
    @Override
    public void execute(Player player, String[] args) {
        // Permission-Check
        if (!player.hasPermission("hytaleae2.command.debug")) {
            player.sendMessage("[ERROR] No permission for this command");
            return;
        }
        
        // Bestehende Logik...
    }
}
```

**manifest.json Ergänzung:**
```json
{
  "permissions": [
    {
      "id": "hytaleae2.command.debug",
      "description": "Access to /medebug command",
      "default": "op"
    }
  ]
}
```

**Acceptance Criteria:**
- [ ] Permission-Checks in allen Commands
- [ ] Permissions in manifest.json
- [ ] Dokumentation in README.md

---

### 7. CI/CD Test-Execution (1 Stunde)

**Was:** GitHub Actions Workflow um Tests erweitern  
**Warum:** Tests werden aktuell nicht automatisch ausgeführt

**.github/workflows/build.yml:**
```yaml
name: Build & Test

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 25
      uses: actions/setup-java@v4
      with:
        distribution: 'temurin'
        java-version: '25'
    
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: ~/.gradle/caches
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle') }}
    
    - name: Build with Gradle
      run: ./gradlew clean build
    
    - name: Run Tests
      run: ./gradlew test
    
    - name: Generate Coverage Report
      run: ./gradlew jacocoTestReport
    
    - name: Upload Test Results
      if: failure()
      uses: actions/upload-artifact@v4
      with:
        name: test-results
        path: build/test-results/
    
    - name: Upload Coverage to Codecov
      uses: codecov/codecov-action@v4
      with:
        files: build/reports/jacoco/test/jacocoTestReport.xml
        token: ${{ secrets.CODECOV_TOKEN }}
```

**Acceptance Criteria:**
- [ ] Workflow-Datei aktualisiert
- [ ] Tests laufen auf Push/PR
- [ ] Coverage-Upload konfiguriert
- [ ] Badge in README.md

---

## 🟡 MEDIUM - Qualität & Wartbarkeit

### 8. Javadoc vervollständigen (3-4 Stunden)

**Was:** Ziel 80%+ Coverage  
**Aktuell:** ~60% geschätzt

**Priorität-Klassen:**
```java
// Alle public/protected Methoden & Klassen dokumentieren:
- MENetwork.java
- MENode.java
- MEDeviceType.java
- MEPlugin.java
- MECableBlock.java
- METerminalBlock.java
- MEControllerBlock.java
```

**Beispiel:**
```java
/**
 * Represents a node in the ME Network.
 * Each block in the system (cables, terminals, controllers) is a node.
 * 
 * <p>Nodes track their connections, device type, and channel usage.
 * 
 * @see MENetwork
 * @see MEDeviceType
 * @since 0.1.0
 */
public class MENode {
    /**
     * Gets the device type of this node.
     * 
     * @return the device type (CABLE, TERMINAL, etc.)
     */
    public MEDeviceType getDeviceType() {
        return deviceType;
    }
}
```

**Acceptance Criteria:**
- [ ] Javadoc für alle public Klassen
- [ ] Javadoc für alle public Methoden
- [ ] Package-Info.java für jedes Package
- [ ] `./gradlew javadoc` ohne Warnungen

---

### 9. Code-Quality Plugins (1-2 Stunden)

**Was:** Checkstyle + SpotBugs für statische Code-Analyse

**build.gradle:**
```groovy
plugins {
    id 'checkstyle'
    id 'com.github.spotbugs' version '6.0.4'
}

checkstyle {
    toolVersion = '10.12.5'
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
}

spotbugs {
    effort = 'max'
    reportLevel = 'low'
}

tasks.withType(com.github.spotbugs.snom.SpotBugsTask) {
    reports {
        html.required = true
        xml.required = false
    }
}
```

**config/checkstyle/checkstyle.xml:** (Google Java Style)
```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <module name="TreeWalker">
        <module name="Indentation">
            <property name="basicOffset" value="4"/>
        </module>
        <module name="LineLength">
            <property name="max" value="120"/>
        </module>
    </module>
</module>
```

**Acceptance Criteria:**
- [ ] Checkstyle konfiguriert
- [ ] SpotBugs konfiguriert
- [ ] `./gradlew check` läuft ohne Fehler
- [ ] CI/CD integriert

---

### 10. Release-Automatisierung (2-3 Stunden)

**Was:** GitHub Actions für automatische Releases

**.github/workflows/release.yml:**
```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 25
      uses: actions/setup-java@v4
      with:
        distribution: 'temurin'
        java-version: '25'
    
    - name: Extract Version from Tag
      id: version
      run: echo "VERSION=${GITHUB_REF#refs/tags/v}" >> $GITHUB_OUTPUT
    
    - name: Update gradle.properties
      run: |
        sed -i "s/version=.*/version=${{ steps.version.outputs.VERSION }}/" gradle.properties
    
    - name: Build Release JAR
      run: ./gradlew clean shadowJar
    
    - name: Create GitHub Release
      uses: softprops/action-gh-release@v1
      with:
        files: build/libs/hytale-ae2-*.jar
        generate_release_notes: true
        draft: false
        prerelease: false
```

**Acceptance Criteria:**
- [ ] release.yml erstellt
- [ ] Test-Release mit Tag `v0.0.1-test`
- [ ] JAR automatisch hochgeladen
- [ ] Release-Notes generiert

---

### 11. Localization-Pfad Migration (30 Minuten)

**Was:** `Localization/` → `Server/Languages/`  
**Warum:** Konsistenz mit offiziellen Plugins

**Migration:**
```bash
# In src/main/resources/
mv Localization/en-US Server/Languages/en-US
mv Localization/de-DE Server/Languages/de-DE
rmdir Localization
```

**Acceptance Criteria:**
- [ ] Dateien verschoben
- [ ] In-Game-Test: Übersetzungen funktionieren
- [ ] Dokumentation aktualisiert

---

### 12. minServerVersion in manifest.json (5 Minuten)

**Was:** Kompatibilität dokumentieren

**manifest.json:**
```json
{
  "id": "hytale-ae2",
  "name": "Hytale AE2",
  "version": "0.1.0",
  "author": "Anoxy1",
  "description": "Matter/Energy infrastructure plugin",
  "entrypoint": "com.tobi.mesystem.MEPlugin",
  "IncludesAssetPack": true,
  "minServerVersion": "2026.01.17",
  "dependencies": []
}
```

**Acceptance Criteria:**
- [ ] manifest.json aktualisiert
- [ ] Dokumentiert in README.md

---

## 🟢 LOW - Nice-to-Have

### 13-16: Optional Features

Siehe [DELTA_ANALYSIS.md Section 8](DELTA_ANALYSIS.md#-low-nice-to-have) für Details.

---

## Implementation Roadmap

### Sprint 1: Release-Blocker (1-2 Wochen)

**Woche 1:**
- [ ] Tag 1-2: Assets erstellen (Item 1)
- [ ] Tag 3: TESTING_GUIDE.md (Item 2)
- [ ] Tag 4-5: Unit-Tests Core System (Item 3 - Teil 1)

**Woche 2:**
- [ ] Tag 1-2: Unit-Tests abschließen (Item 3 - Teil 2)
- [ ] Tag 3: In-Game-Tests durchführen
- [ ] Tag 4: Bugfixes
- [ ] Tag 5: Code-Review & Merge

**Ziel:** Release-fähiger Build mit Assets & Tests

---

### Sprint 2: Stabilität (1 Woche)

- [ ] Integration-Tests (Item 4)
- [ ] Config.yml (Item 5)
- [ ] Permission-System (Item 6)
- [ ] CI/CD Tests (Item 7)

**Ziel:** Stabile, konfigurierbare Plattform

---

### Sprint 3: Qualität (1 Woche)

- [ ] Javadoc (Item 8)
- [ ] Code-Quality Plugins (Item 9)
- [ ] Release-Automatisierung (Item 10)
- [ ] Kleine Fixes (Items 11-12)

**Ziel:** Production-Ready v0.1.0 Release

---

## Tracking & Reporting

### Daily Checklist
```
[ ] Stand-Up: Was wurde gestern gemacht?
[ ] Heute: Welches Item wird bearbeitet?
[ ] Blocker: Gibt es Probleme?
[ ] Tests: Alle grün? (`./gradlew test`)
[ ] Build: Erfolgreich? (`./gradlew build`)
```

### Weekly Review
```
[ ] Fortschritt: Wie viele Items abgeschlossen?
[ ] Coverage: Wie hoch ist die Test-Coverage?
[ ] Bugs: Neue Issues gefunden?
[ ] Roadmap: Sind wir im Plan?
```

---

## Resources & Links

**Tools:**
- Blockbench: https://www.blockbench.net/
- JUnit 5: https://junit.org/junit5/
- Mockito: https://site.mockito.org/
- JaCoCo: https://www.jacoco.org/

**Dokumentation:**
- [DELTA_ANALYSIS.md](DELTA_ANALYSIS.md) - Vollständige Analyse
- [TESTING_GUIDE.md](TESTING_GUIDE.md) - Test-Strategie (zu erstellen)
- [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) - Code-Patterns

**Offizielle Referenzen:**
- HelloPlugin: https://github.com/noel-lang/hytale-example-plugin
- Britakee Studios: https://britakee-studios.gitbook.io/hytale-plugin-development/

---

**Last Updated:** 2026-01-21  
**Next Review:** Nach Sprint 1
