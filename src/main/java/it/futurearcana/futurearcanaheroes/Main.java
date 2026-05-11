package it.futurearcana.futurearcanaheroes;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import it.futurearcana.futurearcanaheroes.content.biome.VampireBiome;
import it.futurearcana.futurearcanaheroes.registry.BlockRegistries;
import it.futurearcana.futurearcanaheroes.registry.CreativeTabRegistries;
import it.futurearcana.futurearcanaheroes.registry.EffectRegistries;
import it.futurearcana.futurearcanaheroes.registry.EntityRegistries;
import it.futurearcana.futurearcanaheroes.registry.ItemRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import terrablender.api.Regions;

@Mod(Main.MODID)
public class Main {
	public static final String MODID = "futurearcanaheroes";
	public static final Logger LOGGER = LogUtils.getLogger();

	public Main(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();

		BlockRegistries.BLOCKS.register(modEventBus);
		ItemRegistries.ITEMS.register(modEventBus);
		EffectRegistries.EFFECTS.register(modEventBus);
		EntityRegistries.ENTITY_TYPES.register(modEventBus);
		CreativeTabRegistries.TABS.register(modEventBus);
		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::addCreative);
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.COMBAT) {
			event.accept(ItemRegistries.WOODEN_STAKE.get());
		}

		if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			event.accept(ItemRegistries.VAMPIRE_SPAWN_EGG.get());
		}

		if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
			event.accept(ItemRegistries.VAMPIRIC_ELIXIR.get());
			event.accept(ItemRegistries.LYCANTHROPE_ELIXIR.get());
			event.accept(ItemRegistries.HERO_SERUM.get());
		}
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			// QUI dentro è il posto giusto
			Regions.register(new VampireBiome(ResourceLocation.fromNamespaceAndPath(MODID, "overworld"), 1));
		});
	}

}
