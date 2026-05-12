package it.futurearcana.futurearcanaheroes.systems.player_state;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

/**
 * Centralized player data container for the Arcana systems.
 * Persist/serialize this per-player later (this is a plain POJO for now).
 */
public final class PlayerArcanaData {
    private it.futurearcana.futurearcanaheroes.systems.race.RaceType race = it.futurearcana.futurearcanaheroes.systems.race.RaceType.HUMAN;
    private int level = 1;
    private int corruption = 0;
    private final Map<String, Integer> abilities = new HashMap<>();
    private final Map<String, Long> cooldowns = new HashMap<>();

    public PlayerArcanaData() {}

    public it.futurearcana.futurearcanaheroes.systems.race.RaceType getRace() {
        return race;
    }

    public void setRace(it.futurearcana.futurearcanaheroes.systems.race.RaceType race) {
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

    // --- Serialization helpers ---
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("race", race.name());
        tag.putInt("level", level);
        tag.putInt("corruption", corruption);

        CompoundTag abilitiesTag = new CompoundTag();
        for (Map.Entry<String, Integer> e : abilities.entrySet()) {
            abilitiesTag.putInt(e.getKey(), e.getValue());
        }
        tag.put("abilities", abilitiesTag);

        CompoundTag cooldownsTag = new CompoundTag();
        for (Map.Entry<String, Long> e : cooldowns.entrySet()) {
            cooldownsTag.putLong(e.getKey(), e.getValue());
        }
        tag.put("cooldowns", cooldownsTag);

        return tag;
    }

    public static PlayerArcanaData fromTag(CompoundTag tag) {
        PlayerArcanaData d = new PlayerArcanaData();
        if (tag == null) return d;
        if (tag.contains("race")) {
            try {
                d.race = it.futurearcana.futurearcanaheroes.systems.race.RaceType.valueOf(tag.getString("race"));
            } catch (Exception ex) {
                d.race = it.futurearcana.futurearcanaheroes.systems.race.RaceType.HUMAN;
            }
        }
        d.level = tag.getInt("level");
        d.corruption = tag.getInt("corruption");

        CompoundTag abilitiesTag = tag.getCompound("abilities");
        for (String key : abilitiesTag.getAllKeys()) {
            d.abilities.put(key, abilitiesTag.getInt(key));
        }

        CompoundTag cooldownsTag = tag.getCompound("cooldowns");
        for (String key : cooldownsTag.getAllKeys()) {
            d.cooldowns.put(key, cooldownsTag.getLong(key));
        }

        return d;
    }
}
