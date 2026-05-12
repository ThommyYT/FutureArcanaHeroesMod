package it.futurearcana.futurearcanaheroes.events;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.systems.player_state.PlayerArcanaData;
import it.futurearcana.futurearcanaheroes.systems.player_state.PlayerArcanaStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerPlayer;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerDataEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        // Load persisted data into cache
        PlayerArcanaData data = PlayerArcanaStorage.get(player);

        // Greet player with current arcana info
        player.sendSystemMessage(Component.literal("[Arcana] Loaded data: race=" + data.getRace() + ", level=" + data.getLevel() + ", corruption=" + data.getCorruption()));
        Main.LOGGER.info("Loaded PlayerArcanaData for {}", player.getName().getString());
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerLoggedOutEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        // Save current data to persistent tag
        PlayerArcanaStorage.save(player);
        PlayerArcanaStorage.clear(player);
        Main.LOGGER.info("Saved PlayerArcanaData for {}", player.getName().getString());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // This event is fired when player respawns or changes dimension (clone=copy of old player to new instance)
        if (!(event.getOriginal() instanceof ServerPlayer oldP) || !(event.getEntity() instanceof ServerPlayer newP)) return;

        try {
            // Copy persistent NBT if present so respawn keeps data
            CompoundTag root = oldP.getPersistentData();
            if (root != null && root.contains("futurearcana:arcana_data")) {
                CompoundTag data = root.getCompound("futurearcana:arcana_data");
                newP.getPersistentData().put("futurearcana:arcana_data", data.copy());
                // Also update in-memory cache
                PlayerArcanaStorage.clear(newP);
                PlayerArcanaStorage.get(newP);
            }
        } catch (Throwable t) {
            Main.LOGGER.warn("Failed to clone PlayerArcanaData during respawn for {}: {}", oldP.getName().getString(), t.getMessage());
        }
    }
}
