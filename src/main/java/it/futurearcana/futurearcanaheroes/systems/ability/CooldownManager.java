package it.futurearcana.futurearcanaheroes.systems.ability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Very small cooldown manager used by AbilitySystem.
 */
public final class CooldownManager {
    private static final Map<String, Long> COOLDOWNS = new ConcurrentHashMap<>();

    private CooldownManager() {}

    public static boolean isOnCooldown(String key, long nowMillis) {
        Long until = COOLDOWNS.get(key);
        return until != null && until > nowMillis;
    }

    public static void setCooldown(String key, long untilMillis) {
        COOLDOWNS.put(key, untilMillis);
    }

    public static void clear(String key) {
        COOLDOWNS.remove(key);
    }
}
