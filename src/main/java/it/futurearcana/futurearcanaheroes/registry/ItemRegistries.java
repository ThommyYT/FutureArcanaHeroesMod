package it.futurearcana.futurearcanaheroes.registry;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.content.item.ArcaneStaffItem;
import it.futurearcana.futurearcanaheroes.content.item.ChronoBraceletItem;
import it.futurearcana.futurearcanaheroes.content.item.EssenceElixirItem;
import it.futurearcana.futurearcanaheroes.content.item.WoodenStake;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Consumables;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ItemRegistries {
	private ItemRegistries() {
	}

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

	// --- Block items ---
	public static final RegistryObject<Item> QUANTUM_CONSOLE = ITEMS.register("quantum_console",
			() -> new BlockItem(BlockRegistries.QUANTUM_CONSOLE.get(),
					new Item.Properties().setId(ITEMS.key("quantum_console"))));

	public static final RegistryObject<Item> TEMPORAL_ANCHOR = ITEMS.register("temporal_anchor",
			() -> new BlockItem(BlockRegistries.TEMPORAL_ANCHOR.get(),
					new Item.Properties().setId(ITEMS.key("temporal_anchor"))));

	public static final RegistryObject<Item> TEMPORAL_TUNNEL = ITEMS.register("temporal_tunnel",
			() -> new BlockItem(BlockRegistries.TEMPORAL_TUNNEL.get(),
					new Item.Properties().setId(ITEMS.key("temporal_tunnel"))));

	// --- Materials / tech ---
	public static final RegistryObject<Item> NANO_ALLOY_INGOT = ITEMS.register("nano_alloy_ingot",
			() -> new Item(new Item.Properties().setId(ITEMS.key("nano_alloy_ingot"))));

	// --- Time travel ---
	public static final RegistryObject<Item> CHRONO_BRACELET = ITEMS.register("chrono_bracelet",
			() -> new ChronoBraceletItem(
					new Item.Properties().setId(ITEMS.key("chrono_bracelet")).stacksTo(1).rarity(Rarity.RARE)));

	// --- Magic ---
	public static final RegistryObject<Item> ARCANE_STAFF = ITEMS.register("arcane_staff", () -> new ArcaneStaffItem(
			new Item.Properties().setId(ITEMS.key("arcane_staff")).durability(500).rarity(Rarity.UNCOMMON)));

	// --- Wooden Stake ---
	public static final RegistryObject<SwordItem> WOODEN_STAKE = ITEMS.register("wooden_stake",
			() -> new WoodenStake(ToolMaterial.WOOD, 2.5F, 1f,
					new Item.Properties().setId(ITEMS.key("wooden_stake")).durability(250)));

	public static final RegistryObject<SwordItem> NANO_SWORD = ITEMS.register("nano_sword",
			() -> new SwordItem(ToolMaterial.NETHERITE, 4.0F, -2.4F,
					new Item.Properties().setId(ITEMS.key("nano_sword"))));

	// --- Supernatural / Heroes (elixirs) ---
	private static Item.Properties elixirProps(String id) {
		return new Item.Properties().setId(ITEMS.key(id)).stacksTo(16).component(DataComponents.CONSUMABLE,
				Consumables.DEFAULT_DRINK); // animazione+suono da bevuta
	}

	public static final RegistryObject<Item> VAMPIRIC_ELIXIR = ITEMS.register("vampiric_elixir",
			() -> new EssenceElixirItem(elixirProps("vampiric_elixir"),
					() -> EffectRegistries.VAMPIRISM.getHolder().orElseThrow(), 20 * 60 * 10, 0));

	public static final RegistryObject<Item> LYCANTHROPE_ELIXIR = ITEMS.register("lycanthrope_elixir",
			() -> new EssenceElixirItem(elixirProps("lycanthrope_elixir"),
					() -> EffectRegistries.LYCANTHROPY.getHolder().orElseThrow(), 20 * 60 * 10, 0));

	public static final RegistryObject<Item> HERO_SERUM = ITEMS.register("hero_serum",
			() -> new EssenceElixirItem(elixirProps("hero_serum"),
					() -> EffectRegistries.HEROIC.getHolder().orElseThrow(), 20 * 60 * 10, 0));

	public static final RegistryObject<Item> VAMPIRE_SPAWN_EGG = ITEMS.register("vampire_spawn_egg",
			() -> new SpawnEggItem(EntityRegistries.VAMPIRE.get(),
					new Item.Properties().setId(ITEMS.key("vampire_spawn_egg"))));

}
