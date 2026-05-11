package it.futurearcana.futurearcanaheroes.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.registry.DimensionRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TickEvents {

    // Cambiamo il record per includere uno stato di "attivazione"
    private static class PendingReturn {
        UUID playerUUID;
        float targetTick;
        Runnable action;
        boolean isStarted = false; // Indica se il giocatore è arrivato e il countdown è iniziato
        float delay;

        PendingReturn(UUID uuid, float f, Runnable action) {
            this.playerUUID = uuid;
            this.delay = f;
            this.action = action;
        }
    }

    private static final List<PendingReturn> PENDING_RETURNS = new ArrayList<>();

    // Metodo aggiornato: ora il delayTicks partirà solo dal momento dell'effettivo arrivo
    public static void addDelayedAction(UUID playerUUID, float f, Runnable action) {
        PENDING_RETURNS.add(new PendingReturn(playerUUID, f, action));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long currentTick = event.getServer().getTickCount();

        // Usiamo un iteratore classico o removeIf con logica
        PENDING_RETURNS.removeIf(pending -> {
            var player = event.getServer().getPlayerList().getPlayer(pending.playerUUID);
            if (player == null) return true; // Giocatore disconnesso, cancella l'azione

            // FASE 1: Aspetta che il giocatore sia fisicamente nel tunnel
            if (!pending.isStarted) {
                if (player.serverLevel().dimension().equals(DimensionRegistries.TEMPORAL_TUNNEL_LEVEL)) {
                    pending.isStarted = true;
                    pending.targetTick = currentTick + pending.delay;
                }
                return false; // Continua a monitorare
            }

            // FASE 2: Esegui l'azione dopo il countdown
            if (currentTick >= pending.targetTick) {
                pending.action.run();
                return true;
            }
            return false;
        });
    }

    // --- Parte Client (Rimane quasi uguale, corretta solo la struttura) ---
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
