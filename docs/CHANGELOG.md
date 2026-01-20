# Changelog

All notable changes to HytaleAE2 are documented here.  
Format: [Keep a Changelog](https://keepachangelog.com/)  
Versioning: [Semantic Versioning](https://semver.org/)

---

## [Unreleased]

### Planned
- Terminal GUI implementation
- Storage Cells (1K, 4K, 16K, 64K variants)
- ME Drive block (multi-cell storage)
- Advanced container search with filters

---

## [0.1.0] - 2026-01-20

### Added

#### Core System
- MENetwork: Digital storage system with channel management
- MENode: Network nodes with connection management
- MEDeviceType: Device type system (6 types defined)
- ContainerUtils: Inventory search utility (6-direction block search)

#### Block System
- MECableBlock: Network transport block
- MEControllerBlock: Network controller block
- METerminalBlock: Player interaction terminal
- BlockState JSON definitions with Codec support
- Block placement event handlers

#### Commands & Testing
- MEDebugCommand: Basic debug testing command
- Network status reporting
- Container detection validation

#### Code Quality
- SLF4J logging framework with ASCII-safe standards
- Project governance documentation (PROJECT_RULES.md)
- Best practices guide (PLUGIN_BEST_PRACTICES.md)
- Contributing guidelines (CONTRIBUTING.md)

#### Deployment
- Gradle build with Shadow JAR
- Single-player deployment (%APPDATA%\Hytale\UserData\Mods\)
- Server plugin deployment (plugins/ directory)
- GitHub Actions CI workflow

#### Documentation
- Consolidated docs structure (22 → 7 core files)
- HelloPlugin compliance patterns
- Hytale API reference
- Setup & quick start guides
- Release workflow documentation

### Fixed

#### Logging & Output
- Unicode/emoji garbling in Windows console (replaced with ASCII)
- Log formatting issues in CI/CD pipelines
- Inconsistent logging across modules

#### Documentation
- Broken relative links after docs consolidation
- Redundant pattern documentation (centralized in PLUGIN_BEST_PRACTICES.md)
- Missing HelloPlugin pattern examples

#### Code Style
- Enforced UTF-8 encoding with .editorconfig
- LF line endings normalization (.gitattributes)
- Removed non-ASCII characters from all code paths

### Changed

#### Documentation
- Consolidated 22 .md files to 7 core files
- Reorganized docs structure for clarity (README → Tier 1, PROJECT_RULES → Tier 2, etc.)
- Enhanced PLUGIN_BEST_PRACTICES with HelloPlugin patterns
- Enhanced PROJECT_RULES with emoji/unicode ban + logging standards

#### Build System
- Updated Gradle wrapper to latest version
- Enhanced .gitignore with IDE and build artifacts
- Added .editorconfig for consistent code style

#### Project Structure
- Moved PLUGIN_BEST_PRACTICES.md from root to docs/
- Created docs/PROJECT_RULES.md for governance
- Created .github/RELEASE_NOTES.md for release workflow

### Deprecated

- N/A (v0.1.0 is initial release)

### Removed

#### Documentation
- Obsolete status reports (BUILD_COMPLETE.md, REFACTORING_COMPLETE.md, OPTIMIZATION_REPORT.md, etc.)
- Duplicate API references (HYTALE_PLUGIN_REFERENCE.md merged into API_REFERENCE.md)
- Redundant setup docs (SETUP.md merged into QUICK_START.md)

#### Code Cleanup
- Removed all emoji and non-ASCII characters from source
- Removed Unicode box-drawing from console output
- Removed hardcoded paths (use environment variables)

### Security

- No API keys or credentials in repository
- No sensitive data in logs
- No wildcard imports (enforced by code review)
- UTF-8 encoding enforced to prevent encoding attacks

---

## Release Management

### How to Release

See [.github/RELEASE_NOTES.md](../.github/RELEASE_NOTES.md) for step-by-step instructions.

**Quick version bump:**
1. Update `gradle.properties` version
2. Update `docs/CHANGELOG.md` with release notes
3. `git commit -m "release: vX.Y.Z"`
4. `git tag -a vX.Y.Z -m "Release vX.Y.Z"`
5. `git push origin main --tags`

### Versioning

- **PATCH** (0.1.0 → 0.1.1): Bug fixes
- **MINOR** (0.1.0 → 0.2.0): New features (backward-compatible)
- **MAJOR** (0.1.0 → 1.0.0): Breaking changes

---

## Current Status

| Component | Status | Version |
|-----------|--------|---------|
| Core System | ✅ Complete | 0.1.0 |
| Block System | ✅ Complete | 0.1.0 |
| Logging | ✅ Complete | 0.1.0 |
| Documentation | ✅ Complete (v1.1) | 0.1.0 |
| Tests | ⏳ Planned | 0.2.0 |
| Terminal GUI | ⏳ Planned | 0.3.0 |
| Storage Cells | ⏳ Planned | 0.2.0 |

---

## Roadmap

### Phase 1: Foundation (Current - 0.1.x)
- ✅ Core ME system
- ✅ Block registration
- ✅ Basic commands
- ✅ Documentation & governance

### Phase 2: Storage (Planned - 0.2.x)
- Storage Cells (1K, 4K, 16K, 64K)
- ME Drive block
- Item serialization
- Persistence layer

### Phase 3: UI (Planned - 0.3.x)
- Terminal GUI framework
- Container browser
- Network status display
- Item search interface

### Phase 4: Stability (Planned - 1.0.0)
- Performance optimization
- Extensive testing
- Community feedback integration
- Official release

---

**Last Updated**: 2026-01-20  
**Maintained By**: Anoxy1
