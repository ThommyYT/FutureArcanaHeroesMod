package it.futurearcana.futurearcanaheroes.content.entity.render;

import it.futurearcana.futurearcanaheroes.content.entity.LycanthropeEntity;
import it.futurearcana.futurearcanaheroes.content.entity.model.LycanthropeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class LycanthropeRenderer extends GeoEntityRenderer<LycanthropeEntity> {
    public LycanthropeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LycanthropeModel());
        this.shadowRadius = 0.6f;

        // Aggiunge automaticamente il glow se esiste textures/entity/lycanthrope_glow.png
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
