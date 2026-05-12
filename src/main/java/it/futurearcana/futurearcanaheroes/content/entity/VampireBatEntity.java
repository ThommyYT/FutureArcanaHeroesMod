package it.futurearcana.futurearcanaheroes.content.entity;

import java.util.Comparator;

import it.futurearcana.futurearcanaheroes.registry.EffectRegistries;
import it.futurearcana.futurearcanaheroes.registry.EntityRegistries;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class VampireBatEntity extends Bat {
//	private static final Logger LOGGER = Main.LOGGER;
	private int batTicks;
	private int returnAfter = 600;

	private static final int DETECT_EVERY_TICKS = 10; // controlla 2 volte al secondo
	private static final double DETECT_RANGE = 16.0; // raggio â€œvistaâ€

	public VampireBatEntity(EntityType<? extends Bat> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide)
			return;

		batTicks++;

		// 1) se vede un player/villager -> torna vampiro subito
		if (batTicks % DETECT_EVERY_TICKS == 0) {
			LivingEntity seen = findSeenTarget();
			if (seen != null) {
				LivingEntity target = seen; // final/effective final per la lambda

				this.convertTo(EntityRegistries.VAMPIRE.get(), ConversionParams.single(this, false, false), converted -> {
					if (converted instanceof VampireEntity v) {
						v.setTarget(target); // cosÃ¬ inizia a inseguire subito
						v.setHealth(getPersistentData().getFloat("Health"));
					}
				});
				return; // IMPORTANT: evita di continuare il tick dopo la conversione
			}
		}

		// 2) fallback: dopo tot tick torna comunque vampiro
		if (batTicks >= returnAfter) {
			this.convertTo(EntityRegistries.VAMPIRE.get(), ConversionParams.single(this, false, false), converted -> {
				if (converted instanceof VampireEntity v) {
					v.setHealth(getPersistentData().getFloat("Health"));
				}
			});
		}
	}

	private LivingEntity findSeenTarget() {
		// Player piÃ¹ vicino
		Player p = this.level().getNearestPlayer(this, DETECT_RANGE);
		if (p != null && !p.isSpectator() && !p.isCreative() && this.hasLineOfSight(p)
				&& !p.hasEffect(EffectRegistries.VAMPIRISM.getKey().getOrThrow(p))) {
			return p;
		}

		// Villager piÃ¹ vicino nel raggio
		AABB box = this.getBoundingBox().inflate(DETECT_RANGE);
		var villagers = this.level().getEntitiesOfClass(AbstractVillager.class, box, v -> true);

		return villagers.stream().min(Comparator.comparingDouble(this::distanceToSqr))
				.filter(v -> this.hasLineOfSight(v)).orElse(null);
	}
}
