package it.futurearcana.futurearcanaheroes.systems.player_state;

import it.futurearcana.futurearcanaheroes.systems.race.RaceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Centralized player data container for the Arcana systems.
 * Persist/serialize this per-player later (this is a plain POJO for now).
 */
public final class PlayerArcanaData {
    private RaceType race = RaceType.HUMAN;
    private int level = 1;
    private int corruption = 0;
    private final Map<String, Integer> abilities = new HashMap<>();
    private final Map<String, Long> cooldowns = new HashMap<>();

    public PlayerArcanaData() {}

    public RaceType getRace() {
        return race;
    }

    public void setRace(RaceType race) {
        this.race = Objects.requireNonNull(race);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCorruption() {
        return corruption;
    }

    public void setCorruption(int corruption) {
        this.corruption = corruption;
    }

    public Map<String, Integer> getAbilities() {
        return abilities;
    }

    public Map<String, Long> getCooldowns() {
        return cooldowns;
    }
}
