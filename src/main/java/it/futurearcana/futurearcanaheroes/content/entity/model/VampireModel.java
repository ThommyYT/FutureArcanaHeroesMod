package it.futurearcana.futurearcanaheroes.content.entity.model;

import org.jetbrains.annotations.Nullable;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.content.entity.VampireEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class VampireModel extends GeoModel<VampireEntity> {
	@Override
	public ResourceLocation getModelResource(VampireEntity animatable, @Nullable GeoRenderer<VampireEntity> renderer) {
		return ResourceLocation.fromNamespaceAndPath(Main.MODID, "geo/vampire.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(VampireEntity animatable,
			@Nullable GeoRenderer<VampireEntity> renderer) {
		return ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/entity/vampire.png");
	}

	@Override
	public ResourceLocation getAnimationResource(VampireEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(Main.MODID, "animations/vampire.animation.json");
	}
}
