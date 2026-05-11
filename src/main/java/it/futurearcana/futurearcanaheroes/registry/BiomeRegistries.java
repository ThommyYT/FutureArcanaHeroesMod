package it.futurearcana.futurearcanaheroes.registry;

import it.futurearcana.futurearcanaheroes.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class BiomeRegistries {
	public static final ResourceKey<Biome> VAMPIRE_FOREST = register("vampire_forest");

    private static ResourceKey<Biome> register(String name)
    {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Main.MODID, name));
    }

}
