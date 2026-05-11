package it.futurearcana.futurearcanaheroes.registry;

import it.futurearcana.futurearcanaheroes.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;

public class TagRegistries {
	private TagRegistries() {
	}

	public static final ResourceKey<DamageType> OP_HIT = ResourceKey.create(Registries.DAMAGE_TYPE,
			ResourceLocation.fromNamespaceAndPath(Main.MODID, "op_hit"));
	public static final TagKey<Item> WOOD_WEAPONS = TagKey.create(Registries.ITEM,
			ResourceLocation.fromNamespaceAndPath(Main.MODID, "wood_weapons"));
}
