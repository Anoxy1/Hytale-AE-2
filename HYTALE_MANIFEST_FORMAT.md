# Hytale manifest.json Format - Januar 2026

## Korrektes Format für Hytale Mods

Nach Analyse von funktionierenden Mods und dem Hytale Plugin-System ist dies das korrekte manifest.json Format:

```json
{
  "Group": "Autor/Gruppe",
  "Name": "Mod Name",
  "Version": "1.0.0",
  "Description": "Mod Beschreibung",
  "Authors": [
    {
      "Name": "Author Name",
      "Email": "",
      "Url": ""
    }
  ],
  "Website": "",
  "Dependencies": {},
  "OptionalDependencies": {},
  "LoadBefore": {},
  "DisabledByDefault": false,
  "IncludesAssetPack": false,
  "SubPlugins": []
}
```

## Wichtige Unterschiede zu Standard Plugin-Manifests

### ✅ Korrekt (Hytale Format)
- `"Group"` - PascalCase, nicht "id"
- `"Name"` - PascalCase, nicht "name"
- `"Version"` - PascalCase, nicht "version"
- `"Authors"` - Array von Objekten mit Name/Email/Url
- `"Dependencies"` - Leeres Objekt `{}`
- Keine `"main"` Klasse für Mods

### ❌ Falsch (Standard Plugin Format)
- `"id": "modid"` - existiert nicht in Hytale
- `"name": "Mod Name"` - lowercase
- `"version": "1.0.0"` - lowercase
- `"authors": ["String"]` - nur Strings statt Objekte
- `"main": "com.example.Plugin"` - nur für Java Plugins, nicht Mods

## Fehlerbeispiel

Wenn manifest.json falsch ist, erhältst du:
```
Failed to validate asset!
Key: Name
Results: FAIL: Can't be null!
```

Dies bedeutet, dass das Format nicht dem erwarteten Schema entspricht.

## Beispiele aus funktionierenden Mods

### Bonnibel - One Log Four Planks
```json
{
  "Group": "Bonnibel",
  "Name": "Bonnibel - One Log Four Planks",
  "Version": "1.0.0",
  "Description": "A simple mod that changes vanilla wood crafting...",
  "Authors": [
    {
      "Name": "Bonnibel",
      "Email": "",
      "Url": ""
    },
    {
      "Name": "Megadoger",
      "Email": "",
      "Url": ""
    }
  ],
  "Website": "",
  "Dependencies": {},
  "OptionalDependencies": {},
  "LoadBefore": {},
  "DisabledByDefault": false,
  "IncludesAssetPack": false,
  "SubPlugins": []
}
```

### HytaleAE2 (ME System)
```json
{
  "Group": "Tobi",
  "Name": "ME System",
  "Version": "0.2.0",
  "Description": "Applied Energistics 2 für Hytale - ME Network Storage & Transport System",
  "Authors": [
    {
      "Name": "Tobi",
      "Email": "",
      "Url": ""
    }
  ],
  "Website": "",
  "Dependencies": {},
  "OptionalDependencies": {},
  "LoadBefore": {},
  "DisabledByDefault": false,
  "IncludesAssetPack": false,
  "SubPlugins": []
}
```

## Hinweise

1. **PascalCase ist Pflicht** - Alle Hauptfelder beginnen mit Großbuchstaben
2. **Authors ist immer ein Array** - Auch bei nur einem Autor
3. **Leere Felder sind erlaubt** - Email und Url können leer bleiben
4. **Dependencies** - Aktuell scheint es keine Dependency-Syntax zu geben
5. **SubPlugins** - Für modulare Mods (noch nicht dokumentiert)

## Quelle

Diese Information wurde durch:
- Reverse Engineering funktionierender Mods
- Analyse von Hytale Early Access (Januar 2026)
- Trial & Error beim Plugin-Loading

Stand: Januar 20, 2026
