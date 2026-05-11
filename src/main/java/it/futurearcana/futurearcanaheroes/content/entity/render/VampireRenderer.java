package it.futurearcana.futurearcanaheroes.content.entity.render;

import it.futurearcana.futurearcanaheroes.content.entity.VampireEntity;
import it.futurearcana.futurearcanaheroes.content.entity.model.VampireModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class VampireRenderer extends GeoEntityRenderer<VampireEntity> {

	public VampireRenderer(EntityRendererProvider.Context context) {
		super(context, new VampireModel());
		this.shadowRadius = 0.5f;

		addRenderLayer(new VampireRendererGlow(this));
	}

	class VampireRendererGlow extends AutoGlowingGeoLayer<VampireEntity> {

		public VampireRendererGlow(GeoRenderer<VampireEntity> renderer) {
			super(renderer);
		}

	}
}
