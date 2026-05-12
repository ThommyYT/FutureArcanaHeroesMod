package it.futurearcana.futurearcanaheroes.systems.ability;

import net.minecraft.world.entity.player.Player;

/**
 * Minimal ability system facade. Items and events should call into this instead of embedding logic.
 */
public final class AbilitySystem {
    private AbilitySystem() {}

    public static void cast(Player caster, String abilityId, Object context) {
        // Resolve ability and execute. Implementation will be expanded later.
        // For now, this is a single entry point so items can delegate to systems.
    }
}
