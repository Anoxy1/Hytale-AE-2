# HytaleAE2 Documentation Index

**Complete guide to all documentation. Start here to find what you need.**

**Last Updated:** 2026-01-20  
**Status:** Foundation Complete (v0.1.0)

---

## 🎯 Quick Navigation

### For New Developers
1. **[QUICK_START.md](QUICK_START.md)** – Clone, build, deploy (5 minutes)
2. **[DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)** – Architecture overview
3. **[PROJECT_RULES.md](PROJECT_RULES.md)** – Governance & best practices

### For Contributors
1. **[PROJECT_RULES.md](PROJECT_RULES.md)** – Rules, workflow, standards
2. **[CONTRIBUTING.md](CONTRIBUTING.md)** – PR guidelines
3. **[PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md)** – Code patterns
4. **[../.github/RELEASE_NOTES.md](../.github/RELEASE_NOTES.md)** – How to release

### For Deploying / Server Setup
1. **[QUICK_START.md](QUICK_START.md)** – Deployment steps
2. **[API_REFERENCE.md](API_REFERENCE.md)** – manifest.json format
3. **[CHANGELOG.md](CHANGELOG.md)** – Version history

---

## 📚 Core Documentation (7 Files)

### Tier 1: Entry Points
| File | Purpose | Audience |
|------|---------|----------|
| [README.md](../README.md) | Project overview | Everyone |
| [QUICK_START.md](QUICK_START.md) | 5-minute setup | New developers |

### Tier 2: Governance
| File | Purpose | Audience |
|------|---------|----------|
| [PROJECT_RULES.md](PROJECT_RULES.md) | Rules, build, git, release workflow | All developers |
| [CONTRIBUTING.md](CONTRIBUTING.md) | PR process, commit format | Contributors |

### Tier 3: Code Quality
| File | Purpose | Audience |
|------|---------|----------|
| [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) | Patterns, logging, anti-patterns | Developers |

### Tier 4: Architecture & Development
| File | Purpose | Audience |
|------|---------|----------|
| [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) | Architecture, roadmap, design | Developers |
| [API_REFERENCE.md](API_REFERENCE.md) | Hytale API, BlockState, Codecs, manifest.json | Developers |
| [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md) | JSON schemas for Items, Blocks, NPCs, Loot, Prefabs | Developers/Content Creators |

### Special Files
| File | Purpose | Location |
|------|---------|----------|
| [CHANGELOG.md](CHANGELOG.md) | Release notes & version history | docs/ |
| [RELEASE_NOTES.md](../.github/RELEASE_NOTES.md) | How to release | .github/ |
| [TESTING_GUIDE.md](TESTING_GUIDE.md) | Testing strategies | docs/ (future expansion) |
| [RESOURCES.md](RESOURCES.md) | External links & references | docs/ |

---

## 📖 Topic Quick Lookup

### Getting Started
- **Setup**: [QUICK_START.md](QUICK_START.md) (clone, build, deploy)
- **Rules**: [PROJECT_RULES.md](PROJECT_RULES.md) (code standards, workflow)
- **Architecture**: [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)

### Coding & Development
- **Code Patterns**: [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md)
- **HelloPlugin Examples**: [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#helliplugin-standard-structure)
- **Logging Standards**: [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#logging-best-practices) & [PROJECT_RULES.md](PROJECT_RULES.md#logging--console-output-standards)
- **Anti-Patterns**: [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#common-pitfalls-anti-patterns-to-avoid)
- **ASCII/Unicode Rules**: [PROJECT_RULES.md](PROJECT_RULES.md#critical-no-emoji-or-unicode-in-codestrings) & [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#1-emojiunicode-in-code-the-1-problem)

### API & Configuration
- **Block System API**: [API_REFERENCE.md](API_REFERENCE.md#blockstate-system)
- **Codec System**: [API_REFERENCE.md](API_REFERENCE.md#codec-system)
- **manifest.json Format**: [API_REFERENCE.md](API_REFERENCE.md#manifestjson-format-plugin-configuration)
- **Plugin Lifecycle**: [API_REFERENCE.md](API_REFERENCE.md#event-system)
- **JSON Data Assets**: [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md) (Items, Blocks, NPCs, Loot Tables, Prefabs, Config)
- **Item Templates**: [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md#item-templates)
- **Block Templates**: [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md#block-templates)
- **NPC Templates**: [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md#npc-templates)
- **Loot Tables**: [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md#loot-tables)
- **World Configuration**: [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md#server--world-configuration)

### Git & Collaboration
- **Branching & PRs**: [CONTRIBUTING.md](CONTRIBUTING.md)
- **Commit Format**: [CONTRIBUTING.md](CONTRIBUTING.md)
- **Code Review**: [CONTRIBUTING.md](CONTRIBUTING.md)

### Releases & Versions
- **How to Release**: [../.github/RELEASE_NOTES.md](../.github/RELEASE_NOTES.md)
- **Version History**: [CHANGELOG.md](CHANGELOG.md)
- **Versioning Strategy**: [PROJECT_RULES.md](PROJECT_RULES.md#-versioning--changelog)

---

## 🔍 Searching by Keyword

### Debugging
- Block Placement Issues: [docs/BLOCK_PLACEMENT_FIX.md](../docs/BLOCK_PLACEMENT_FIX.md) (archived reference)
- Troubleshooting: [QUICK_START.md](QUICK_START.md#-troubleshooting)
- Debug Commands: [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)

### Performance
- Optimization Tips: [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#-performance--optimization)
- Thread Pools: [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#1-thread-pool-management)

### Security
- Secret Management: [PROJECT_RULES.md](PROJECT_RULES.md#-secret-management)
- Input Validation: [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md#-error-handling--security)

### Testing
- Testing Strategy: [TESTING_GUIDE.md](TESTING_GUIDE.md)
- MEDebugCommand: [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)

---

## 🌍 External References (Canonical Sources)

Always check official sources first. Internal docs may lag behind.

- **Hytale Server Manual**: https://support.hytale.com/
- **HelloPlugin (Official Example)**: https://github.com/noel-lang/hytale-example-plugin
- **Semantic Versioning**: https://semver.org/
- **Conventional Commits**: https://www.conventionalcommits.org/
- **Keep a Changelog**: https://keepachangelog.com/

---

## 📊 Document Status

| File | Status | Version | Last Updated |
|------|--------|---------|--------------|
| QUICK_START.md | ✅ Current | 1.1 | 2026-01-20 |
| PROJECT_RULES.md | ✅ Current | 1.1 | 2026-01-20 |
| CONTRIBUTING.md | ✅ Current | 1.0 | 2026-01-20 |
| PLUGIN_BEST_PRACTICES.md | ✅ Current | 1.1 | 2026-01-20 |
| DEVELOPMENT_GUIDE.md | ✅ Current | 1.0 | 2026-01-20 |
| API_REFERENCE.md | ✅ Current | 1.1 | 2026-01-20 |
| CHANGELOG.md | ✅ Current | 0.1.0 | 2026-01-20 |
| JSON_DATA_ASSETS.md | ✅ Current | 1.0 | 2026-01-21 |
| TESTING_GUIDE.md | ⏳ Incomplete | 1.0 | TBD |
| RESOURCES.md | ✅ Reference | 1.0 | 2026-01-20 |
| README.md (root) | ✅ Current | 1.0 | 2026-01-20 |

---

## 🗑️ Archived / Deleted Files

The following files were consolidated into the 7-core structure:

- ~~SETUP.md~~ → Merged into [QUICK_START.md](QUICK_START.md)
- ~~HYTALE_MANIFEST_FORMAT.md~~ → Merged into [API_REFERENCE.md](API_REFERENCE.md#manifestjson-format-plugin-configuration)
- ~~HYTALE_PLUGIN_REFERENCE.md~~ → Merged into [API_REFERENCE.md](API_REFERENCE.md)
- ~~HYTALE_PLUGIN_COMPLETE_GUIDE.md~~ → Merged into [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) & [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md)
- ~~BUILD_COMPLETE.md~~ → Status moved to [CHANGELOG.md](CHANGELOG.md)
- ~~PROJECT_STATUS.md~~ → Status moved to [CHANGELOG.md](CHANGELOG.md)
- ~~IMPLEMENTATION_STATUS.md~~ → Status moved to [CHANGELOG.md](CHANGELOG.md)
- ~~REFACTORING_COMPLETE.md~~ → Historical (see [CHANGELOG.md](CHANGELOG.md))
- ~~OPTIMIZATION_REPORT.md~~ → Historical (see [CHANGELOG.md](CHANGELOG.md))
- ~~PROJECT_STRUCTURE.md~~ → Info in [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)
- ~~RESOURCES_SUMMARY.md~~ → Merged into [RESOURCES.md](RESOURCES.md)

**Migration completed**: 2026-01-20

---

## 🔧 Maintenance Notes

### To Add New Documentation
1. Check if it fits into existing categories
2. If new doc needed, update this INDEX.md
3. Keep cross-references updated
4. Link to canonical external sources

### To Update Documentation
1. Update relevant .md file
2. Update version in this INDEX
3. Add entry to [CHANGELOG.md](CHANGELOG.md)
4. Commit with message: `docs: update [filename]`

### Before Each Release
1. Review all docs for accuracy
2. Update [CHANGELOG.md](CHANGELOG.md)
3. Follow [../.github/RELEASE_NOTES.md](../.github/RELEASE_NOTES.md)

---

**Questions?** See [PROJECT_RULES.md](PROJECT_RULES.md#-questions-or-changes)

## 🔍 Nach Thema finden

### Assets & Struktur
→ **[HYTALE_PLUGIN_COMPLETE_GUIDE.md](HYTALE_PLUGIN_COMPLETE_GUIDE.md)** (Kapitel 1-2)
- Common/ Ordnerstruktur
- Asset-Naming Conventions
- Texture & Icon Guidelines

### Block Development
→ **[HYTALE_PLUGIN_COMPLETE_GUIDE.md](HYTALE_PLUGIN_COMPLETE_GUIDE.md)** (Kapitel 3)
- Block JSON Schema
- IconProperties
- States & Variants
- DrawType & Models

### Interactions & Recipes
→ **[HYTALE_PLUGIN_COMPLETE_GUIDE.md](HYTALE_PLUGIN_COMPLETE_GUIDE.md)** (Kapitel 4-5)
- Interaction System
- OpenContainer
- Crafting Recipes
- Workbench Integration

### Java Plugin Code
→ **[HYTALE_PLUGIN_COMPLETE_GUIDE.md](HYTALE_PLUGIN_COMPLETE_GUIDE.md)** (Kapitel 6)
→ **[API_REFERENCE.md](API_REFERENCE.md)**
- JavaPlugin Base Class
- onLoad / onEnable / onDisable
- Event Handlers
- BlockInteractEvent

### Troubleshooting
→ **[HYTALE_PLUGIN_COMPLETE_GUIDE.md](HYTALE_PLUGIN_COMPLETE_GUIDE.md)** (Kapitel 8)
- Purple-Black Checkerboard
- Block not placeable
- Missing Textures
- Log Analysis

---

## 📦 Referenz-Implementierungen

Im `libs/` Ordner:
- **HyPipes** (v1.0.5) - Network Graph, Custom Models
- **ChestTerminal** (v2.0.8) - Container GUI, Interactions

Diese wurden analysiert und alle Erkenntnisse in [HYTALE_PLUGIN_COMPLETE_GUIDE.md](HYTALE_PLUGIN_COMPLETE_GUIDE.md) dokumentiert.

---

## 📊 Dokument-Status

| Dokument | Status | Priorität | Letzte Änderung |
|----------|--------|-----------|-----------------|
| **HYTALE_PLUGIN_COMPLETE_GUIDE.md** | ✅ Complete | ⭐⭐⭐ | 2026-01-20 |
| PROJECT_STATUS.md | ✅ Complete | ⭐⭐ | 2026-01-20 |
| SETUP.md | ✅ Complete | ⭐⭐ | 2025-01-20 |
| API_REFERENCE.md | ✅ Complete | ⭐⭐ | 2025-01-20 |
| DEVELOPMENT_GUIDE.md | ✅ Complete | ⭐ | 2025-01-20 |
| PLUGIN_BEST_PRACTICES.md | ✅ Complete | ⭐ | 2025-01-20 |
| HYTALE_MANIFEST_FORMAT.md | ✅ Complete | ⭐ | 2025-01-20 |
| BUILD_COMPLETE.md | ✅ Complete | ⭐ | 2025-01-20 |
| TESTING_GUIDE.md | ✅ Complete | ⭐ | 2025-01-20 |
| IMPLEMENTATION_STATUS.md | ✅ Complete | ⭐ | 2025-01-20 |
| QUICK_START.md | ✅ Complete | ⭐ | 2025-01-20 |
| OPTIMIZATION_REPORT.md | ✅ Complete | ⭐ | 2025-01-20 |

**Empfehlung:** Starte mit [HYTALE_PLUGIN_COMPLETE_GUIDE.md](HYTALE_PLUGIN_COMPLETE_GUIDE.md) - dort ist alles Wichtige! ⭐

---

**Total:** 12 Dokumentations-Dateien  
**Haupt-Quelle:** HYTALE_PLUGIN_COMPLETE_GUIDE.md (18KB, alle Erkenntnisse)  
**Letzte Index-Aktualisierung:** 20. Januar 2026
