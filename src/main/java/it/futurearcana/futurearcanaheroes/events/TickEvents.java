package it.futurearcana.futurearcanaheroes.events;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.registry.DimensionRegistries;
import it.futurearcana.futurearcanaheroes.systems.player_state.PlayerStateSystem;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TickEvents {

    /**
     * Compatibility helper: preserve the previous API so other code can schedule delayed actions here.
     * Delegates to PlayerStateSystem.
     */
    public static void addDelayedAction(java.util.UUID playerUUID, float f, Runnable action) {
        PlayerStateSystem.addDelayedAction(playerUUID, f, action);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        // Bridge: delegate all server-side ticking to systems
        PlayerStateSystem.tick(event.getServer());
    }

    // --- Client part remains unchanged except structure: ---
    @Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public final class TickEventsClient {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.player != null && mc.level.dimension().equals(DimensionRegistries.TEMPORAL_TUNNEL_LEVEL)) {
                RandomSource random = mc.level.random;
                double px = mc.player.getX() + (random.nextDouble() - 0.5) * 16;
                double py = mc.player.getY() + (random.nextDouble() - 0.5) * 10;
                double pz = mc.player.getZ() + (random.nextDouble() - 0.5) * 16;

                mc.level.addParticle(ParticleTypes.ELECTRIC_SPARK, px, py, pz, 0, 0, 0);
                if (random.nextInt(5) == 0) {
                    mc.level.addParticle(ParticleTypes.PORTAL, px, py, pz, 0, (random.nextDouble() - 0.5) * 0.2, 0);
                }
            }
        }
    }
}
