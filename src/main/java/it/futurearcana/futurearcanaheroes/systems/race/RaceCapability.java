package it.futurearcana.futurearcanaheroes.systems.race;

import net.minecraft.world.entity.player.Player;

/**
 * Capability-like interface for applying race-specific behavior to players or entities.
 * Keep this minimal: systems implement the actual logic.
 */
public interface RaceCapability {
    RaceType getRaceType();

    /** Called when a player becomes this race (e.g. transformation/conversion). */
    void onApplyToPlayer(Player player);

    /** Called when a player loses this race (reverts to HUMAN, etc). */
    void onRemoveFromPlayer(Player player);
}
