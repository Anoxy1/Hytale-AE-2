# Hytale Event Wiring - API Integration Guide

## Gefundene Event-Klassen in HytaleServer.jar

### Block-Interaction Events
```
✓ com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent
✓ com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent
✓ com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent (mit $Pre und $Post)
```

### Player Events
```
✓ com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent
✓ com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent
✓ com.hypixel.hytale.server.core.event.events.player.DrainPlayerFromWorldEvent
```

### System Events
```
✓ com.hypixel.hytale.server.core.event.events.BootEvent
✓ com.hypixel.hytale.server.core.event.events.ShutdownEvent
✓ com.hypixel.hytale.server.core.event.events.PrepareUniverseEvent
```

## Mapping: HytaleAE2 Events → Hytale Events

### PlaceBlockEvent
- Hytale: `com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent`
- Usage: MECableBlock, METerminalBlock, MEControllerBlock onPlaced()
- Handler: `@EventHandler public void onPlaceBlock(PlaceBlockEvent event)`

### BreakBlockEvent
- Hytale: `com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent`
- Usage: MECableBlock, METerminalBlock, MEControllerBlock onBroken()
- Handler: `@EventHandler public void onBreakBlock(BreakBlockEvent event)`

### UseBlockEvent (Interaction)
- Hytale: `com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent`
- Usage: METerminalBlock onRightClick() - Terminal GUI öffnen
- Handler: `@EventHandler public void onUseBlock(UseBlockEvent event)`
- Note: Nutze `UseBlockEvent$Pre` zum Blocken

## Implementierungs-Schritte

1. Korrekte Imports in Event-Listener-Klassen
2. @EventHandler Annotation aus Hytale-API nutzen
3. Event-Listener bei Plugin-Startup registrieren
4. MEPlugin.setup() muss Event-Registration durchführen

## Nächste Schritte
1. ServerTickEvent finden (für Netzwerk-Maintenance)
2. World Load/Unload Events finden
3. Listener-Registrierung implementieren
