# Hytale Plugin Development Reference

## Official Resources

### Example Plugin
- **Repository**: [noel-lang/hytale-example-plugin](https://github.com/noel-lang/hytale-example-plugin)
- **Video Tutorial**: [Create Your First Hytale Plugin in 2 Minutes](https://www.youtube.com/watch?v=NEw9QjzZ9nM)
- **Description**: Official example showing basic plugin structure with command registration

### Documentation Sites
- [Hytale Modding Documentation](https://hytalemodding.dev/)
- [Hytale Server Manual](https://support.hytale.com/hc/en-us/articles/45326769420827-Hytale-Server-Manual)

## Key Patterns from Example Plugin

### Plugin Structure
```java
public class MyPlugin extends JavaPlugin {
    public MyPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();
        // Register commands, events, etc.
    }
}
```

### Command Registration
```java
// In setup() method:
this.getCommandRegistry().registerCommand(
    new MyCommand("commandname", "description", false)
);
```

### Command Implementation
```java
public class MyCommand extends AbstractPlayerCommand {
    public MyCommand(@Nonnull String name, @Nonnull String description, 
                     boolean requiresConfirmation) {
        super(name, description, requiresConfirmation);
    }

    @Override
    protected void execute(
        @Nonnull CommandContext commandContext,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {
        // Command logic
        playerRef.sendMessage(Message.raw("Hello!"));
    }
}
```

## API Differences from our Implementation

### ❌ Old (Wrong)
```java
// CommandRegistry via reflection
CommandRegistry registry = plugin.getServiceRegistry().getService(CommandRegistry.class);

// Event registration via custom wrapper
EventRegistry eventRegistry = new EventRegistry(plugin);
```

### ✅ New (Correct)
```java
// Direct command registry access
this.getCommandRegistry().registerCommand(new MyCommand(...));

// Event registration via plugin methods
this.getEventRegistry().register(...);
```

## Common Pitfalls

1. **Commands don't use reflection** - Use `getCommandRegistry()` directly
2. **Events use plugin registry** - Not custom wrapper classes
3. **AbstractPlayerCommand** - Not BaseCommand or custom implementations
4. **PlayerRef.sendMessage()** - Not CommandContext.getSource()

## HytaleAE2 Migration Status

- [x] Plugin loads successfully
- [x] Commands fixed to use AbstractPlayerCommand
- [x] Basic command registration working
- [x] Event system migration to native API
- [x] Remove custom wrapper classes (CommandRegistry, EventRegistry, BlockRegistry)
- [x] MEPlugin vereinfacht nach HelloPlugin-Vorbild
- [x] Full API compliance erreicht

## Änderungen

### Entfernte Klassen
- `EventRegistry.java` - Nicht mehr benötigt, direkte `getEventRegistry()` Verwendung
- `CommandRegistry.java` - Nicht mehr benötigt, direkte `getCommandRegistry()` Verwendung
- `BlockRegistry.java` - Nicht mehr benötigt, Blocks via JSON geladen
- `HytaleBlockEventListenerStub.java` - Ersetzt durch Lambda-Event-Handler
- `EventHandler.java` - Annotation nicht mehr benötigt

### Vereinfachte MEPlugin.java
- Folgt jetzt dem HelloPlugin-Beispiel Pattern
- `super.setup()` und `super.start()` aufgerufen
- Direkte Event-Registrierung via `getEventRegistry().register()`
- Direkte Command-Registrierung via `getCommandRegistry().registerCommand()`
- Weniger defensive Programmierung, sauberer Code
- `@Nonnull` Annotations hinzugefügt
