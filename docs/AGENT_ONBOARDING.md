# Agent Onboarding & Project Overview

**Purpose**: Quick reference for new agents/developers joining the project  
**Last Updated**: 2026-01-21  
**Status**: Foundation Complete (v0.1.0)

---

## 🎯 Project At-A-Glance

| Aspect | Details |
|--------|---------|
| **Project** | HytaleAE2 – Early-game ME (Matter/Energy) System for Hytale |
| **Tech Stack** | Java 25 LTS, Gradle 9.3.0, Hytale Server 2026.01.17 |
| **Repository** | GitHub: Anoxy1/Hytale-AE-2 (main branch) |
| **Build** | ✅ BUILD SUCCESSFUL (~988ms) – Shadow JAR in build/libs/ |
| **Status** | Phase 1 Complete: Core infrastructure (no gameplay yet) |

---

## 📚 Documentation Structure (Current State)

### File Size Analysis (as of Jan 21, 2026)

```
Size Ranking (Lines)
===============
1. TECHNICAL_SOURCES.md      938 lines   [26.8 KB] – Deep-dive JSON configs, server setup
2. PLUGIN_BEST_PRACTICES.md  847 lines   [27.0 KB] – 8 concrete Java examples + anti-patterns
3. JSON_DATA_ASSETS.md       796 lines   [17.9 KB] – Complete Item/Block/NPC/Loot schemas
4. API_REFERENCE.md          585 lines   [17.0 KB] – Codec System, BlockState, manifest.json
5. QUICK_START.md            359 lines   [12.9 KB] – 5-min setup, paths, troubleshooting
6. PROJECT_RULES.md          300 lines   [15.5 KB] – Governance, build rules, git workflow
7. DEVELOPMENT_GUIDE.md      238 lines   [7.4  KB] – Architecture overview, roadmap
8. INDEX.md                  212 lines   [11.2 KB] – Main documentation hub (navigation)
9. CHESTTERMINAL_API_REF.md  195 lines   [6.0  KB] – ChestTerminal block documentation
10. TESTING_GUIDE.md         186 lines   [6.7  KB] – Testing strategies (future)
11. CHANGELOG.md             138 lines   [4.9  KB] – v0.1.0 release notes
12. RESOURCES.md             117 lines   [8.6  KB] – 38+ external links + best practices
```

### ⚠️ Issues Identified

**1. Size & Potential Consolidation**

| Files | Issue | Recommendation |
|-------|-------|-----------------|
| TECHNICAL_SOURCES + JSON_DATA_ASSETS | Content overlaps (JSON specs in both) | ✅ ALREADY SEPARATED: TECHNICAL = configs, JSON_DATA = asset schemas |
| PLUGIN_BEST_PRACTICES + DEVELOPMENT_GUIDE | Pattern examples scattered | ✅ CONSOLIDATED: Examples now in Section 8 of PLUGIN_BEST_PRACTICES |
| QUICK_START + PROJECT_RULES | Some governance appears in both | ⚠️ MINOR: Some git workflow duplication – acceptable for quick reference |

**Status**: Documentation is **well-organized**, not bloated. No actionable consolidation needed.

**2. What Each File Actually Does**

```
TIER 1: Entry Points
├─ README.md (root)                  – Project summary, quick links
└─ QUICK_START.md                    – 5-min setup: clone, build, deploy

TIER 2: Governance & Contributing
├─ PROJECT_RULES.md                  – Dev standards, build, git, release
└─ CONTRIBUTING.md                   – PR process, commit conventions

TIER 3: Code Quality & Best Practices
└─ PLUGIN_BEST_PRACTICES.md          – 8 concrete Java examples + 9 anti-patterns

TIER 4: Deep Technical Reference
├─ DEVELOPMENT_GUIDE.md              – Architecture, phases, roadmap
├─ API_REFERENCE.md                  – Codec System, BlockState API, manifest
├─ JSON_DATA_ASSETS.md               – Item/Block/NPC/Loot/Prefab schemas (850+ lines)
└─ TECHNICAL_SOURCES.md              – Server config, world config, spawn rules, plugins (938 lines)

TIER 5: Special Files
├─ INDEX.md                          – Navigation hub (212 lines – GOOD size)
├─ RESOURCES.md                      – 38 external links + onboarding path
├─ CHANGELOG.md                      – Release history
├─ TESTING_GUIDE.md                  – Test strategies (lightweight)
└─ CHESTTERMINAL_API_REF.md          – Block-specific docs
```

**No Duplicates**: Each file has distinct purpose. Some cross-references are intentional (e.g., JSON schema appears in both JSON_DATA_ASSETS.md "what to configure" AND TECHNICAL_SOURCES.md "how to configure server").

---

## 🚀 Startpunkt für neuen Agent

### **Entry Point: START HERE**

1. **Read in order** (5 minutes):
   - [README.md](../README.md) – What is HytaleAE2?
   - [QUICK_START.md](QUICK_START.md) – Clone, build, deploy
   - [docs/INDEX.md](INDEX.md) – Full navigation

2. **Find what you need**:
   - Need to write Java code? → [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) Section 8 (copy-paste examples)
   - Need JSON specs? → [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md) (Items, Blocks, Loot)
   - Need server config? → [TECHNICAL_SOURCES.md](TECHNICAL_SOURCES.md) Section 9 (world setup, plugins)
   - Need API details? → [API_REFERENCE.md](API_REFERENCE.md) (Codec, BlockState)
   - Need to understand architecture? → [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md)

3. **Do your work**:
   - Follow rules: [PROJECT_RULES.md](PROJECT_RULES.md)
   - Make PRs: [CONTRIBUTING.md](CONTRIBUTING.md)
   - Check anti-patterns: [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) Section 9

---

## 📖 What Each Document Is For

**QUICK_START.md** (359 lines)
- **For**: New developers in first hour
- **Contains**: Clone, build, deploy, troubleshooting
- **Skip if**: You already have the project running

**DEVELOPMENT_GUIDE.md** (238 lines)
- **For**: Understanding architecture & roadmap
- **Contains**: System design, 3-phase roadmap, key components
- **Read when**: Planning new features

**API_REFERENCE.md** (585 lines)
- **For**: Understanding Hytale plugin APIs
- **Contains**: BlockState, Codec System, manifest.json format, multiserver architecture
- **Read when**: Writing plugin code or configuring server

**PLUGIN_BEST_PRACTICES.md** (847 lines) ⭐ MOST PRACTICAL
- **For**: Writing production-quality Java code
- **Contains**: 8 concrete examples (PingCommand, EchoCommand, PlayerOnlyCommand, etc.)
- **Anti-patterns**: 9 common mistakes with fixes
- **Read when**: Writing any Java code – copy from Section 8!

**JSON_DATA_ASSETS.md** (796 lines)
- **For**: Content creators & modders
- **Contains**: Complete JSON schemas for Items, Blocks, NPCs, Loot, Prefabs
- **Examples**: stone_pickaxe tool, iron_sword weapon, diamond_chestplate armor
- **Read when**: Creating custom items or blocks

**TECHNICAL_SOURCES.md** (938 lines) ⭐ MOST COMPREHENSIVE
- **For**: Advanced setup, performance tuning, deployment
- **Contains**: 
  - Server config (MaxPlayers, MaxViewRadius, compression)
  - World config (GameRules, ClientEffects, NPCs, Chunk management)
  - 3 scenario configs (Kreativ, PvP-Arena, Performance-optimized)
  - Spawn rules, Loot tables, Crafting recipes
  - Best practices for JSON, debugging, testing
- **Read when**: Setting up server or tuning performance

**PROJECT_RULES.md** (300 lines)
- **For**: Understanding project governance
- **Contains**: Code standards, build process, git workflow, release checklist
- **Read when**: Before first commit

**INDEX.md** (212 lines) ⭐ NAVIGATION HUB
- **For**: Finding anything in the docs
- **Contains**: Topic lookup, keyword search, file descriptions
- **Read when**: "Where is X documented?"

**RESOURCES.md** (117 lines)
- **For**: External links & learning path
- **Contains**: 38 official/community resources, recommended reading order
- **Read when**: Need community support or external examples

---

## 🔍 Quick Reference by Task

| Task | Read This | Section |
|------|-----------|---------|
| "I want to write a command" | PLUGIN_BEST_PRACTICES.md | 8.2 (PingCommand) |
| "I want to write a command with parameters" | PLUGIN_BEST_PRACTICES.md | 8.3 (EchoCommand) |
| "I want to create a custom item" | JSON_DATA_ASSETS.md | 3.1 (Item-Konfiguration) |
| "I want to create a tool" | JSON_DATA_ASSETS.md | 3.1.5 (Tool-Konfiguration) |
| "I want to set up a server" | TECHNICAL_SOURCES.md | 2.1 (Haupt-Server-Konfiguration) |
| "I want to set up a creative world" | TECHNICAL_SOURCES.md | 9.2 (Szenarien) |
| "I want to understand the API" | API_REFERENCE.md | All sections |
| "I want to know what not to do" | PLUGIN_BEST_PRACTICES.md | 9 (Common Pitfalls) |
| "I need to understand the architecture" | DEVELOPMENT_GUIDE.md | All sections |
| "I want to find X topic" | INDEX.md | 🔍 Search by Keyword |

---

## ⚠️ Critical Project Information

**Repository**: https://github.com/Anoxy1/Hytale-AE-2
- Branch: `main` (default)
- Build: `./gradlew build` → Shadow JAR in `build/libs/`
- Deploy: Copy JAR to `%APPDATA%\Roaming\Hytale\UserData\earlyplugins/`

**Version**: 0.1.0 (Foundation Complete)
- ✅ Core infrastructure (MENode, ContainerUtils, MEBlockBase)
- ✅ Build system (Gradle + Shadow plugin)
- ✅ Documentation (7-core structure + technical deep-dives)
- ⏳ Gameplay features (Terminal GUI, Storage Cells, Container Search)

**Hytale Server**: 2026.01.17-4b0f30090
- Java 25 LTS (Temurin)
- Protocol: QUIC over UDP (not TCP!)
- Port: 25565

**Key Standards**:
- ✅ NO EMOJI (PROJECT_RULES enforcement)
- ✅ ASCII-only console output
- ✅ Conventional commits (`feat:`, `fix:`, `docs:`)
- ✅ Code review for PRs
- ✅ Build must pass before merge

---

## 🗂️ Codebase Structure

```
HytaleAE2/
├── src/main/java/com/tobi/mesystem/
│   ├── core/
│   │   ├── MENode.java                  – Volatile world + getWorld()/setWorld()
│   │   ├── MEBlockBase.java             – Block integration
│   │   └── MEControllerBlock.java       – Main controller
│   ├── utils/
│   │   ├── ContainerUtils.java          – 300+ lines, 6-direction search
│   │   └── MEDebugCommand.java          – Testing command
│   └── blocks/
│       ├── MECableBlock.java
│       ├── METerminalBlock.java
│       └── MEStorageBlock.java          – (Placeholder)
├── build.gradle                         – Gradle config + Shadow plugin
├── gradle.properties                    – JVM args
├── settings.gradle                      – Project structure
├── docs/                                – 12 markdown files (indexed below)
├── .github/
│   └── workflows/
│       └── ci.yml                       – GitHub Actions CI/CD
└── README.md                            – Project summary
```

---

## 📝 Documentation Maintenance

### When Adding New Features
1. Update [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) roadmap
2. Add code examples to [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md)
3. Update [CHANGELOG.md](CHANGELOG.md) with version entry
4. Reference in [INDEX.md](INDEX.md) if new topic
5. Update [docs/README.md](README.md) if major feature

### When Updating API
1. Update [API_REFERENCE.md](API_REFERENCE.md)
2. Update [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) examples
3. Add breaking changes to [CHANGELOG.md](CHANGELOG.md)

### When Adding External Resources
1. Add link to [RESOURCES.md](RESOURCES.md)
2. Categorize by topic (Official, Community, Tools, etc.)
3. Keep #1-20 = stable links, #21+ = new discoveries

---

## 🚦 Next Steps for New Agent

**Priority 1 (First Hour)**:
- [ ] Clone repo: `git clone https://github.com/Anoxy1/Hytale-AE-2.git`
- [ ] Build: `cd HytaleAE2 && .\gradlew build`
- [ ] Read [QUICK_START.md](QUICK_START.md) completely
- [ ] Read [PROJECT_RULES.md](PROJECT_RULES.md) completely

**Priority 2 (First Day)**:
- [ ] Read [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) for architecture
- [ ] Understand roadmap: Phase 1 ✅ → Phase 2 (Terminal GUI) → Phase 3 (Storage)
- [ ] Check [PLUGIN_BEST_PRACTICES.md](PLUGIN_BEST_PRACTICES.md) Section 8 for code patterns

**Priority 3 (As Needed)**:
- [ ] Deep-dive: [TECHNICAL_SOURCES.md](TECHNICAL_SOURCES.md) for server config
- [ ] Reference: [JSON_DATA_ASSETS.md](JSON_DATA_ASSETS.md) for asset creation
- [ ] API: [API_REFERENCE.md](API_REFERENCE.md) for plugin development

---

## ❓ Common Questions

**Q: Where do I add new code?**  
A: `src/main/java/com/tobi/mesystem/` – follow package structure in [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md#project-structure)

**Q: How do I build?**  
A: `.\gradlew build` → JAR in `build/libs/hytale-ae2-*.jar`

**Q: How do I test?**  
A: See [TESTING_GUIDE.md](TESTING_GUIDE.md) + deploy JAR to Hytale and test in-game

**Q: What's the git workflow?**  
A: See [PROJECT_RULES.md](PROJECT_RULES.md#-git-workflow) + [CONTRIBUTING.md](CONTRIBUTING.md)

**Q: Can I use emoji in code?**  
A: **NO!** See [PROJECT_RULES.md](PROJECT_RULES.md#critical-no-emoji-or-unicode-in-codestrings)

**Q: Where are the API docs?**  
A: [API_REFERENCE.md](API_REFERENCE.md) + [TECHNICAL_SOURCES.md](TECHNICAL_SOURCES.md)

**Q: What's the roadmap?**  
A: Phase 1 (Core) ✅ → Phase 2 (GUI) → Phase 3 (Storage). See [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md#roadmap)

---

**Need Help?** Start with [INDEX.md](INDEX.md) for full navigation, or ask in the repository issues.

**Last Review**: 2026-01-21 (v0.1.0)
