package it.futurearcana.futurearcanaheroes.registry;

import it.futurearcana.futurearcanaheroes.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class CreativeTabRegistries {
    private CreativeTabRegistries() {}

	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("future_arcana_heroes", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
			.title(Component.translatable("itemGroup." + Main.MODID))
            .icon(() -> ItemRegistries.CHRONO_BRACELET.get().getDefaultInstance())
            .displayItems((params, output) -> {
                output.accept(ItemRegistries.QUANTUM_CONSOLE.get());
                output.accept(ItemRegistries.TEMPORAL_ANCHOR.get());
                output.accept(ItemRegistries.TEMPORAL_TUNNEL.get());
                output.accept(ItemRegistries.NANO_ALLOY_INGOT.get());
                output.accept(ItemRegistries.CHRONO_BRACELET.get());
                output.accept(ItemRegistries.ARCANE_STAFF.get());
                output.accept(ItemRegistries.VAMPIRIC_ELIXIR.get());
                output.accept(ItemRegistries.LYCANTHROPE_ELIXIR.get());
                output.accept(ItemRegistries.HERO_SERUM.get());
                output.accept(ItemRegistries.WOODEN_STAKE.get());
                output.accept(ItemRegistries.VAMPIRE_SPAWN_EGG.get());
            })
            .build());
}
