package it.futurearcana.futurearcanaheroes.events;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.registry.BiomeRegistries;
import it.futurearcana.futurearcanaheroes.registry.DimensionRegistries;
import net.minecraft.client.Camera;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FogEvents {

    private static final float VAMPIRE_FOG_DISTANCE = 24.0f;

    // 1. Gestione della DISTANZA (già presente, ottimizzata)
    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Camera camera = event.getCamera();
        if (camera.getEntity() == null) return;

        Level level = camera.getEntity().level();
        if (level.dimension().equals(DimensionRegistries.TEMPORAL_TUNNEL_LEVEL)) {
            event.setNearPlaneDistance(2.0f);
            event.setFarPlaneDistance(12.0f);
            event.setCanceled(true);
        } else if (level.getBiome(camera.getBlockPosition()).is(BiomeRegistries.VAMPIRE_FOREST)) {
            event.setNearPlaneDistance(1.0f);
            event.setFarPlaneDistance(VAMPIRE_FOG_DISTANCE);
            event.setCanceled(true);
        }
    }

    // 2. Gestione del COLORE (nuovo)
    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        if (camera.getEntity() == null) return;

        Level level = camera.getEntity().level();

        // Colore per il Tunnel Temporale (es. Viola Scuro / Nero Glitch)
        if (level.dimension().equals(DimensionRegistries.TEMPORAL_TUNNEL_LEVEL)) {
            event.setRed(0.1f);   // 0.0 - 1.0
            event.setGreen(0.0f);
            event.setBlue(0.2f);
        }
        // Colore per la Vampire Forest (es. Rosso Sangue cupo)
        else if (level.getBiome(camera.getBlockPosition()).is(BiomeRegistries.VAMPIRE_FOREST)) {
            event.setRed(0.2f);
            event.setGreen(0.02f);
            event.setBlue(0.02f);
        }
    }
}
