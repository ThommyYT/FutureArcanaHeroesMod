package it.futurearcana.futurearcanaheroes.content.entity.model;

import org.jetbrains.annotations.Nullable;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.content.entity.LycanthropeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class LycanthropeModel extends GeoModel<LycanthropeEntity> {

	@Override
	public ResourceLocation getModelResource(LycanthropeEntity animatable,
			@Nullable GeoRenderer<LycanthropeEntity> renderer) {
        return ResourceLocation.fromNamespaceAndPath(Main.MODID, "geo/lycanthrope.geo.json");
    }

	@Override
	public ResourceLocation getTextureResource(LycanthropeEntity animatable,
			@Nullable GeoRenderer<LycanthropeEntity> renderer) {
        return ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/entity/lycanthrope.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LycanthropeEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Main.MODID, "animations/lycanthrope.animation.json");
    }
}
