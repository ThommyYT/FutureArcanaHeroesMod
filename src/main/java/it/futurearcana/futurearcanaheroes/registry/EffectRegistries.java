package it.futurearcana.futurearcanaheroes.registry;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.content.effect.HeroicEffect;
import it.futurearcana.futurearcanaheroes.content.effect.LycanthropyEffect;
import it.futurearcana.futurearcanaheroes.content.effect.VampirismEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EffectRegistries {
    private EffectRegistries() {}

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Main.MODID);

    public static final RegistryObject<MobEffect> VAMPIRISM = EFFECTS.register("vampirism", VampirismEffect::new);
    public static final RegistryObject<MobEffect> LYCANTHROPY = EFFECTS.register("lycanthropy", LycanthropyEffect::new);
    public static final RegistryObject<MobEffect> HEROIC = EFFECTS.register("heroic", HeroicEffect::new);
}
