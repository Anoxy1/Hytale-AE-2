# Project Rules for HytaleAE2

**Version**: 1.1  
**Last Updated**: 2026-01-20 (v1.1 – Critical emoji/Unicode rules + logging standards)  
**For**: Future Developers & AI Agents

---

## 📋 TL;DR – Essential Rules (Read First!)

1. **Language**: Java 25 LTS (Temurin recommended)
2. **Build**: `gradlew clean build` (or `./gradlew` on Linux/macOS)
3. **Deploy**: Single-player: `%APPDATA%\Hytale\UserData\Mods\`, Server: `plugins/`
4. **No destructive Git**: Never force-push, never delete branches without approval
5. **Refs**: All docs in `docs/`, no root .md files except `README.md`
6. **Official Sources**: [HelloPlugin](https://github.com/noel-lang/hytale-example-plugin), [Hytale Server Manual](https://support.hytale.com/) override internal docs
7. **Branches**: `main` only; use feature branches locally, PR to main
8. **Code Style**: 4-space indent, UTF-8, LF line endings (enforced by .editorconfig)
9. **Never Edit**: `build/`, `libs/`, generated code in `src/main/java/com/` (except logic)
10. **Before Commit**: `gradlew clean build` must pass; check `.gitignore` to avoid IDE/build artifacts

---

## 🎯 Project Objectives

- **Goal**: HytaleAE2 is a Minecraft/Hytale-like plugin system focusing on ME (Matter/Energy) blocks and terminal infrastructure
- **Scope**: Container search (ChestTerminal-inspired), debug commands, block placement, inventory management
- **Not in Scope**: Client-side GUIs (planned), storage cells (planned), external APIs

---

## 🏗️ Repository Structure

```
.
├── README.md                          # Root: Project overview (ONLY .md at root)
├── build.gradle / settings.gradle     # Gradle config
├── gradlew / gradlew.bat              # Gradle wrapper (auto-downloaded)
├── src/
│   ├── main/java/com/tobi/...         # Source code (Java 25)
│   └── main/resources/                # Assets (manifest.json, textures, localization)
├── build/                             # Generated (DO NOT EDIT)
├── libs/                              # External libs (DO NOT COMMIT)
├── docs/                              # All documentation
│   ├── README.md                      # Docs entry point
│   ├── PROJECT_RULES.md               # This file
│   ├── INDEX.md                       # Complete index + canonical sources
│   ├── QUICK_START.md                 # 5-min onboarding (official sources)
│   ├── SETUP.md                       # Detailed setup (Java 25, build, deploy)
│   ├── DEVELOPMENT_GUIDE.md           # Dev workflow, architecture
│   ├── API_REFERENCE.md               # Hytale API reference
│   ├── PLUGIN_BEST_PRACTICES.md       # Code patterns & conventions
│   ├── HYTALE_MANIFEST_FORMAT.md      # manifest.json spec
│   ├── CONTRIBUTING.md                # PR guidelines, commit format
│   └── [more docs...]
├── .github/workflows/build.yml        # CI: build on push
├── .gitignore                         # Git exclusions
├── .gitattributes                     # Line ending normalization (LF)
└── .editorconfig                      # Code style (UTF-8, 4-space indent)
```

---

## 🔧 Development Workflow

### Setup

```bash
# Clone repo
git clone https://github.com/Anoxy1/Hytale-AE-2.git
cd Hytale-AE-2

# First time: Download wrapper & build
gradlew clean build

# On Linux/macOS:
./gradlew clean build
```

### Build & Deploy

```bash
# Full build (includes tests, creates shadowJar)
gradlew clean build

# Deploy to local single-player (Windows)
# Artifact copied to: %APPDATA%\Hytale\UserData\Mods\

# Deploy to server (manual copy)
cp build/libs/hytale-ae2-*.jar <server>/plugins/
```

### Code Changes

1. **Pull latest**: `git pull origin main`
2. **Create local branch**: `git checkout -b feature/my-feature` (never commit to main locally)
3. **Make changes** in `src/main/java/com/tobi/`
4. **Build & verify**: `gradlew clean build`
5. **Commit**: Use [Conventional Commits](https://www.conventionalcommits.org/) (see CONTRIBUTING.md)
   ```
   feat: add container search
   fix: correct MENode world reference
   chore: update docs
   ```
6. **Push & PR**: `git push origin feature/my-feature`, then create PR to `main` on GitHub
7. **Merge**: Once approved, GitHub will merge to main

---

## 📝 Code Style & Standards

### Language & Format

- **Java 25 LTS** (Temurin recommended; [Official setup](https://support.hytale.com/))
- **4-space indentation** (enforced by `.editorconfig`)
- **UTF-8 encoding** (no emoji, no box-drawing; ASCII alternatives only)
- **LF line endings** (`.gitattributes` enforces normalization)
- **Trailing semicolons**: Required

### Naming Conventions

- **Classes**: `PascalCase` (e.g., `MENode`, `ContainerUtils`, `MEDebugCommand`)
- **Methods**: `camelCase` (e.g., `getWorld()`, `findNearbyContainers()`)
- **Constants**: `UPPER_CASE` (e.g., `MAX_RANGE`, `DEFAULT_SIZE`)
- **Private fields**: `camelCase` with `private` modifier

### Imports & Packages

- **Package structure**: `com.tobi.*`
- **No wildcard imports**: Use explicit imports (enforced by Pylance)
- **Organize imports**: IDE auto-formats on save

### Comments & Documentation

- **Javadoc for public methods**: Include `@param`, `@return`, `@throws`
- **Inline comments**: Explain *why*, not *what* the code does
- **TODO comments**: Format: `// TODO: description (JIRA-XXX or GitHub #XXX if applicable)`

### CRITICAL: No Emoji or Unicode in Code/Strings

**HARD RULE**: String literals, comments, and console output **MUST use ASCII only**. No emoji, box-drawing, or special Unicode.

**Why**: Formatierungsprobleme in Logs (z.B. Windows Console, CI output). Emojis/Unicode können als Wirrwarr oder Fragezeichen auftreten, besonders in:
- CI/CD logs (.github/workflows/)
- Server console logs
- Remote SSH sessions
- Non-UTF-8 terminals

**Examples (WRONG)**:
```java
// WRONG: Contains emoji and box-drawing
System.out.println("[✓] Container found at: " + pos);
System.out.println("┌─ MENode Status ─┐");
String statusMsg = "🚀 Plugin loaded!";
logger.info("━━━━ Starting ━━━━");
```

**Examples (CORRECT)**:
```java
// CORRECT: ASCII only
System.out.println("[OK] Container found at: " + pos);
System.out.println("=== MENode Status ===");
String statusMsg = "Plugin loaded successfully.";
logger.info("======== Starting ========");
```

**Allowed ASCII equivalents**:
| Don't Use | Use Instead | Example |
|-----------|-------------|---------|
| ✓, ✔, ✅ | [OK], [YES], [DONE] | `[OK] Operation complete` |
| ✗, ❌ | [FAIL], [NO], [ERR] | `[FAIL] Connection timeout` |
| → | `->` | `Config -> %APPDATA%` |
| ├─, │, └─ | `\|`, `--`, `-` | `src/main/java/com/tobi` |
| 🚀, ⚡ | `>>`, `>>:` | `>> Plugin initialization` |
| 💥, ⚠️ | `[WARN]`, `[CRITICAL]` | `[WARN] Low memory` |

**Enforcement**:
1. **IDE check**: Configure Java formatter to warn on non-ASCII in strings
2. **Pre-commit**: Run `grep -r '[^\x00-\x7F]' src/main/java/com/tobi/` before commit
3. **Code review**: Reject PRs with emoji/Unicode in code paths
4. **CI warning**: (Future) Add linter rule to flag non-ASCII strings

**HelloPlugin Reference**:  
Check [HelloPlugin source](https://github.com/noel-lang/hytale-example-plugin/blob/main/src/main/java/) – you'll notice official examples use plain ASCII in logs and strings.

---

## ✅ Mandatory Checks Before Commit

1. **Build succeeds**: `gradlew clean build` → `BUILD SUCCESSFUL`
2. **No broken links**: Run grep check: `grep -r "PLUGIN_BEST_PRACTICES\|docs/" src/ | grep -v ".class"`
3. **No IDE artifacts**: `.vs/`, `.idea/`, `*.iml`, `build/` excluded by `.gitignore`
4. **Code style**: Check for 4-space indent, UTF-8, LF (automated on save)
5. **Commit message**: Follows [Conventional Commits](docs/CONTRIBUTING.md)
6. **ASCII-only check**: `grep -r '[^\x00-\x7F]' src/main/java/com/tobi/` should return NOTHING
7. **No System.out chaos**: All console logs use `logger` (SLF4J) with ASCII-only format strings

---

## 📢 Logging & Console Output Standards

**All logging MUST be ASCII-safe and readable in any terminal.**

### Logging Framework

- **Use**: SLF4J with Logback (or java.util.logging as fallback)
- **NOT**: Direct `System.out.println()` in production code (okay for quick debugging, but remove before commit)

### Log Format Examples

**HelloPlugin Reference**: See [HelloPlugin logging](https://github.com/noel-lang/hytale-example-plugin/blob/main/src/main/java/com/example/HelloPlugin.java)

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MEDebugCommand {
    private static final Logger logger = LoggerFactory.getLogger(MEDebugCommand.class);
    
    public void execute() {
        // CORRECT: ASCII-only, structured logging
        logger.info("=== MEDebugCommand Execution ===");
        logger.debug("Container search range: {} blocks", SEARCH_RANGE);
        logger.warn("[WARN] No containers found in range");
        logger.error("[ERROR] Failed to access world object");
        
        // WRONG: Don't do this
        // System.out.println("✓ Command executed!");  // NEVER in code
        // logger.info("━━ Command Complete ━━");       // Unicode forbidden
    }
}
```

### Log Level Guidelines

| Level | When to Use | Example |
|-------|------------|---------|
| `DEBUG` | Development, detailed tracing | `logger.debug("Search starting from: {}", pos)` |
| `INFO` | Important state changes | `logger.info("Plugin initialized")` |
| `WARN` | Potential issues, not failures | `logger.warn("Container not found at: {}", pos)` |
| `ERROR` | Recoverable errors | `logger.error("Failed to read container", e)` |

### Terminal Output in Commands/Testing

When debugging via console output (MEDebugCommand, testing), use ASCII-safe formatting:

```java
// CORRECT
String output = String.format(
    "[INFO] Container at [%d, %d, %d] with %d items",
    x, y, z, itemCount
);
System.out.println(output);

// CORRECT: Simple box with ASCII
System.out.println("========================================");
System.out.println("   MENode Status Report");
System.out.println("========================================");
System.out.println("| X: " + x + "        | Y: " + y + "        | Z: " + z);
System.out.println("========================================");

// WRONG: Emoji/Unicode
// System.out.println("✓ Status: [■■■■□] 80%");  // NEVER
```

---

## 🚫 Never Do

- ❌ **Force-push to main**: `git push --force` (uses `git push` only)
- ❌ **Delete remote branches** without team approval
- ❌ **Edit under build/**: Generated files (exception: manual manifest fixes only)
- ❌ **Commit build artifacts**: `libs/`, `*.class`, `.gradle/` (see `.gitignore`)
- ❌ **Hardcode paths**: Use `%APPDATA%` (Windows) or XDG_DATA_HOME (Linux)
- ❌ **Use emoji or Unicode** in code: Log output must be ASCII-only (see "Logging & Console Output" section)
- ❌ **Skip tests**: If you add a feature, add a test (future: test framework TBD)
- ❌ **Bypass build checks**: Always run `gradlew clean build` before commit—no exceptions
- ❌ **Mixed line endings**: Only LF (`.gitattributes` enforces this; CRLF will fail CI)
- ❌ **Wildcard imports**: Explicitly import what you use (IDE auto-formats on save)

---

## 📚 Key References

| Topic | File | Purpose |
|-------|------|---------|
| **Quick Start** | [QUICK_START.md](QUICK_START.md) | Clone, build, deploy in 5 minutes |
| **API Reference** | [API_REFERENCE.md](API_REFERENCE.md) | Hytale API (BlockState, Codec, Commands, manifest.json) |
| **Code Patterns** | [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) | HelloPlugin patterns, logging, anti-patterns |
| **Dev Guide** | [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) | Architecture, roadmap, testing |
| **Contributing** | [CONTRIBUTING.md](CONTRIBUTING.md) | PR guidelines, commit format, review process |
| **Release Notes** | [../.github/RELEASE_NOTES.md](../.github/RELEASE_NOTES.md) | How to create releases |
| **Changelog** | [CHANGELOG.md](CHANGELOG.md) | Version history and release notes |
| **Index** | [INDEX.md](INDEX.md) | Complete documentation index |

---

## 🎯 Code Patterns & HelloPlugin Compliance

Our codebase follows the official Hytale HelloPlugin structure and patterns.

**For detailed code patterns, HelloPlugin examples, and anti-patterns:**  
See [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#helliplugin-standard-structure)

**Key rules:**
- Extend `JavaPlugin` with `onPluginEnable()` / `onPluginDisable()`
- Register commands via `getCommandRegistry()`
- Use SLF4J logging (never `System.out.println()` in production)
- ASCII-only output (see section above)
- manifest.json in `src/main/resources/` (see [API_REFERENCE.md](API_REFERENCE.md#manifestjson-format-plugin-configuration))

---

## 🔐 Secret Management

- **No API keys in code**: Use environment variables or `~/.hytale/config.json`
- **No credentials in Git**: Add sensitive paths to `.gitignore`
- **GitHub Secrets**: (future setup) For CI/CD deployments

---

## 🐛 Bug Reports & Feature Requests

- **Use GitHub Issues**: Provide minimal reproduction, stack trace, Java version
- **Labels**: `bug`, `enhancement`, `documentation`, `help-wanted`
- **Assign to MAIN branch**: No tracking on local branches

---

## 🔄 CI/CD Pipeline

**Trigger**: Push to `main`  
**Action**: [.github/workflows/build.yml](.github/workflows/build.yml)  
**Steps**:
1. Checkout code
2. Setup Java 25 (Temurin)
3. Run `gradlew clean build`
4. Verify `BUILD SUCCESSFUL`
5. (Future) Deploy to test server

**Status**: Check GitHub Actions tab or PR status badge

---

## 📅 Versioning & Changelog

- **Version format**: `MAJOR.MINOR.PATCH` (e.g., `0.1.0`)
- **Changelog**: Update `CHANGELOG.md` on each release (WIP)
- **Release cadence**: TBD (as-needed; tag `vX.Y.Z` on main after merge)

---

## ⚖️ License & Attribution

- **License**: [Check LICENSE file](../LICENSE) (likely MIT or similar)
- **Attribution**: Mention Hytale API sources in code comments
- **ChestTerminal API**: Internal extraction noted; credit to original ChestTerminal project if used externally

---

## 🤖 For AI Agents / Future Developers

**First Steps**:
1. Read this file (PROJECT_RULES.md)
2. Read [docs/README.md](README.md) for overview
3. Read [docs/QUICK_START.md](QUICK_START.md) for 5-minute onboarding
4. Read [docs/INDEX.md](INDEX.md) to find detailed docs by topic

**Before Coding**:
- [ ] Run `gradlew clean build` to verify environment
- [ ] Check [docs/DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) for architecture
- [ ] Review [docs/PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) for code patterns

**Common Tasks**:
- **New feature**: Branch from main, code, test build, commit (Conventional), push, PR, wait for approval
- **Bug fix**: Same flow, but prefix commit with `fix:`
- **Docs update**: Edit in `docs/`, verify links, commit, push
- **Code cleanup**: Use IDE refactoring tools; test build before commit

**Help**:
- Official Hytale docs: [support.hytale.com](https://support.hytale.com/)
- HelloPlugin reference: [noel-lang/hytale-example-plugin](https://github.com/noel-lang/hytale-example-plugin)
- Team docs: [docs/INDEX.md](INDEX.md)

---

## 📞 Questions or Changes?

- **Unclear rule?**: Open an issue with label `documentation`
- **Need new rule?**: Discuss in PR or issue; update this file and changelog
- **Version bump?**: See "Versioning & Changelog" section above

---

**Version History**:
- **v1.0** (2026-01-20): Initial creation; core rules, build workflow, code style, agent onboarding
- **v1.1** (2026-01-20): **CRITICAL UPDATE** – Added hard emoji/Unicode ban with context, logging standards (SLF4J), terminal output guidelines, HelloPlugin pattern examples, ASCII-only pre-commit check
