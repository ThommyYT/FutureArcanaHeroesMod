package it.futurearcana.futurearcanaheroes.systems.player_state;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import it.futurearcana.futurearcanaheroes.registry.DimensionRegistries;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * System responsible for ticking player arcana data and handling delayed actions previously inside TickEvents.
 * Keep this class focused on orchestration; game rules should call into other systems (Race/Corruption/Ability).
 */
public final class PlayerStateSystem {

    private PlayerStateSystem() {}

    private static final List<PendingReturn> PENDING_RETURNS = new CopyOnWriteArrayList<>();

    private static class PendingReturn {
        final UUID playerUUID;
        long targetTick;
        final Runnable action;
        boolean isStarted = false;
        final long delay;

        PendingReturn(UUID uuid, long delay, Runnable action) {
            this.playerUUID = uuid;
            this.delay = delay;
            this.action = action;
        }
    }

    public static void addDelayedAction(UUID playerUUID, float delayTicks, Runnable action) {
        long delay = (long) delayTicks;
        PENDING_RETURNS.add(new PendingReturn(playerUUID, delay, action));
    }

    public static void tick(MinecraftServer server) {
        long currentTick = server.getTickCount();

        PENDING_RETURNS.removeIf(pending -> {
            var player = server.getPlayerList().getPlayer(pending.playerUUID);
            if (player == null) return true; // player disconnected

            // Phase 1: wait for entering tunnel (example: previously used DimensionRegistries)
            if (!pending.isStarted) {
                if (player.serverLevel().dimension().equals(DimensionRegistries.TEMPORAL_TUNNEL_LEVEL)) {
                    pending.isStarted = true;
                    pending.targetTick = currentTick + pending.delay;
                }
                return false;
            }

            // Phase 2: execute after countdown
            if (currentTick >= pending.targetTick) {
                try {
                    pending.action.run();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return true;
            }
            return false;
        });

        // Future: iterate over online players and tick PlayerArcanaData, abilities, corruption, etc.
        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            ServerLevel lvl = sp.getLevel();
            // Placeholder - actual per-player tick logic will be added later
        }
    }
}
