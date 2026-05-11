package it.futurearcana.futurearcanaheroes.registry;

import it.futurearcana.futurearcanaheroes.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionRegistries {
	public static final ResourceKey<DimensionType> TEMPORAL_TUNNEL_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
			ResourceLocation.fromNamespaceAndPath(Main.MODID, "temporal_tunnel_type"));

	public static final ResourceKey<Level> TEMPORAL_TUNNEL_LEVEL = ResourceKey.create(Registries.DIMENSION,
			ResourceLocation.fromNamespaceAndPath(Main.MODID, "temporal_tunnel"));
}
