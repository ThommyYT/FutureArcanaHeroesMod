package it.futurearcana.futurearcanaheroes.content.biome;

import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;

import it.futurearcana.futurearcanaheroes.registry.BiomeRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

public class VampireBiome extends Region {
	public VampireBiome(ResourceLocation name, int weight) {
		super(name, RegionType.OVERWORLD, weight);
	}

	@Override
	public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
		addBiomeSimilar(mapper, Biomes.DARK_FOREST, BiomeRegistries.VAMPIRE_FOREST);
	}
}
