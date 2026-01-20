# Project Rules for HytaleAE2

**Version**: 1.0  
**Last Updated**: 2026-01-20  
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

---

## ✅ Mandatory Checks Before Commit

1. **Build succeeds**: `gradlew clean build` → `BUILD SUCCESSFUL`
2. **No broken links**: Run grep check: `grep -r "PLUGIN_BEST_PRACTICES\|docs/" src/ | grep -v ".class"`
3. **No IDE artifacts**: `.vs/`, `.idea/`, `*.iml`, `build/` excluded by `.gitignore`
4. **Code style**: Check for 4-space indent, UTF-8, LF (automated on save)
5. **Commit message**: Follows [Conventional Commits](docs/CONTRIBUTING.md)

---

## 🚫 Never Do

- ❌ **Force-push to main**: `git push --force` (uses `git push` only)
- ❌ **Delete remote branches** without team approval
- ❌ **Edit under build/**: Generated files (exception: manual manifest fixes only)
- ❌ **Commit build artifacts**: `libs/`, `*.class`, `.gradle/` (see `.gitignore`)
- ❌ **Hardcode paths**: Use `%APPDATA%` (Windows) or XDG_DATA_HOME (Linux)
- ❌ **Mix Unicode with ASCII**: Choose one; ASCII preferred for console output
- ❌ **Skip tests**: If you add a feature, add a test (future: test framework TBD)

---

## 📚 Key References

| Topic | File | Source |
|-------|------|--------|
| **Quick Start** | [docs/QUICK_START.md](QUICK_START.md) | Hytale Server Manual + HelloPlugin |
| **Setup & Build** | [docs/SETUP.md](SETUP.md) | Official Hytale docs |
| **API Patterns** | [docs/API_REFERENCE.md](API_REFERENCE.md) | HelloPlugin + Hytale Server Manual |
| **Code Patterns** | [docs/PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) | Internal (ChestTerminal, MENode patterns) |
| **Manifest Format** | [docs/HYTALE_MANIFEST_FORMAT.md](HYTALE_MANIFEST_FORMAT.md) | Hytale specification |
| **Dev Guide** | [docs/DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) | Architecture & workflows |
| **Contributing** | [docs/CONTRIBUTING.md](CONTRIBUTING.md) | PR guidelines & conventions |

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
