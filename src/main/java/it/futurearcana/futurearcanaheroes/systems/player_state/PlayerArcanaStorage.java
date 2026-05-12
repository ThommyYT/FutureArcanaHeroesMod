package it.futurearcana.futurearcanaheroes.systems.player_state;

import it.futurearcana.futurearcanaheroes.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple persistence manager that saves PlayerArcanaData into the player's persistent NBT and keeps a small in-memory cache.
 * This is intentionally simple (uses player persistent data) to avoid the complexity of Forge capabilities for now.
 */
public final class PlayerArcanaStorage {
    private static final String NBT_KEY = "futurearcana:arcana_data";
    private static final Map<UUID, PlayerArcanaData> CACHE = new ConcurrentHashMap<>();

    private PlayerArcanaStorage() {}

    public static PlayerArcanaData get(ServerPlayer player) {
        return CACHE.computeIfAbsent(player.getUUID(), uuid -> load(player));
    }

    public static PlayerArcanaData load(ServerPlayer player) {
        try {
            CompoundTag root = player.getPersistentData();
            if (root == null) return new PlayerArcanaData();
            if (!root.contains(NBT_KEY)) return new PlayerArcanaData();
            CompoundTag data = root.getCompound(NBT_KEY);
            return PlayerArcanaData.fromTag(data);
        } catch (Throwable t) {
            Main.LOGGER.warn("Failed to load PlayerArcanaData for {}: {}", player.getName().getString(), t.getMessage());
            return new PlayerArcanaData();
        }
    }

    public static void save(ServerPlayer player) {
        try {
            PlayerArcanaData d = CACHE.get(player.getUUID());
            if (d == null) d = new PlayerArcanaData();
            CompoundTag root = player.getPersistentData();
            root.put(NBT_KEY, d.toTag());
        } catch (Throwable t) {
            Main.LOGGER.warn("Failed to save PlayerArcanaData for {}: {}", player.getName().getString(), t.getMessage());
        }
    }

    public static void clear(ServerPlayer player) {
        CACHE.remove(player.getUUID());
    }
}
