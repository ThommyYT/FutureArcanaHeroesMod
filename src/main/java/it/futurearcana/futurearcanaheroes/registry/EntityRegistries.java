package it.futurearcana.futurearcanaheroes.registry;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.content.entity.LycanthropeEntity;
import it.futurearcana.futurearcanaheroes.content.entity.VampireBatEntity;
import it.futurearcana.futurearcanaheroes.content.entity.VampireEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistries {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE,
			Main.MODID);

	public static final RegistryObject<EntityType<VampireEntity>> VAMPIRE = ENTITY_TYPES.register("vampire",
			() -> EntityType.Builder.of(VampireEntity::new, MobCategory.MONSTER).sized(0.6f, 1.95f)
					.clientTrackingRange(8).build(ENTITY_TYPES.key("vampire")));

	public static final RegistryObject<EntityType<VampireBatEntity>> VAMPIRE_BAT =
		    ENTITY_TYPES.register("vampire_bat",
		        () -> EntityType.Builder.of(VampireBatEntity::new, MobCategory.AMBIENT)
		            .sized(0.5f, 0.9f)
		            .clientTrackingRange(8)
		            .build(ENTITY_TYPES.key("vampire_bat")));


	public static final RegistryObject<EntityType<LycanthropeEntity>> LYCANTHROPE = ENTITY_TYPES.register("lycanthrope",
			() -> EntityType.Builder.of(LycanthropeEntity::new, MobCategory.MONSTER).sized(0.75f, 2.1f)
					.clientTrackingRange(8).build(ENTITY_TYPES.key("lycanthrope")));
}
