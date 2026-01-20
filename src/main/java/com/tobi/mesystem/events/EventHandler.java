package com.tobi.mesystem.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Placeholder @EventHandler annotation for compile-time support.
 *
 * At runtime, Hytale's own @EventHandler annotation (from
 * com.hypixel.hytale.server.core.event) will override this one. This allows us
 * to: 1. Compile without HytaleServer.jar in the classpath 2. Have
 * @EventHandler present for IDE support and documentation 3. Let Hytale's
 * reflection-based event system discover these methods
 *
 * NOTE: This is a compile-time polyfill. Hytale's runtime will use its own
 * annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
}
