package it.futurearcana.futurearcanaheroes.events;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.content.entity.LycanthropeEntity;
import it.futurearcana.futurearcanaheroes.content.entity.VampireBatEntity;
import it.futurearcana.futurearcanaheroes.content.entity.VampireEntity;
import it.futurearcana.futurearcanaheroes.content.entity.render.LycanthropeRenderer;
import it.futurearcana.futurearcanaheroes.content.entity.render.VampireRenderer;
import it.futurearcana.futurearcanaheroes.registry.EffectRegistries;
import it.futurearcana.futurearcanaheroes.registry.EntityRegistries;
import it.futurearcana.futurearcanaheroes.registry.TagRegistries;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EntityEvents {

	private static final ThreadLocal<Boolean> IS_PROCESSING = ThreadLocal.withInitial(() -> false);

	@SubscribeEvent
	public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
		LivingEntity target = event.getNewTarget();
		if (target != null && target.hasEffect(EffectRegistries.VAMPIRISM.getHolder().orElseThrow()) && !(event.getEntity() instanceof VampireEntity)){
			event.setNewTarget(null);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {

		LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();

		if (!(entity.level() instanceof ServerLevel level) || !(source.getEntity() instanceof LivingEntity attacker))
			return;

		if (level.getDifficulty() == Difficulty.NORMAL && entity.getRandom().nextBoolean())
			return;

		if (level.getDifficulty() == Difficulty.EASY)
			return;

		if (entity instanceof Villager villager) {
			if (!ForgeEventFactory.canLivingConvert(villager, EntityRegistries.VAMPIRE.get(), (timer) -> {
			}))
				return;
			VampireEntity vampire = villager.convertTo(EntityRegistries.VAMPIRE.get(),
					ConversionParams.single(villager, true, true), conversion -> {
						ForgeEventFactory.onFinalizeSpawn(villager, level,
								level.getCurrentDifficultyAt(conversion.blockPosition()), EntitySpawnReason.CONVERSION,
								null);
						ForgeEventFactory.onLivingConvert(villager, conversion);
						if (!attacker.isSilent()) {
							level.levelEvent(null, 1026, attacker.blockPosition(), 0);
						}
					});

			if (vampire == null)
				return;

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity target = event.getEntity();
		DamageSource source = event.getSource();

		// Applica solo se la vittima:
		// - ha l'effetto Vampirismo
		// - ÃƒÂ¨ in realtÃƒÂ  un VampireEntity
		// - oppure ÃƒÂ¨ un Player (vogliamo proteggere il giocatore)
		if (!(target.hasEffect(EffectRegistries.VAMPIRISM.getHolder().orElseThrow()) || target instanceof VampireEntity
				|| target instanceof Player)) {
			return;
		}

//		if (target.hasEffect(EffectRegistries.VAMPIRISM.getHolder().orElseThrow()) && !(source.getEntity() instanceof VampireEntity) && source.getEntity() instanceof PathfinderMob attackerMob) {
//			attackerMob.setTarget(null);
//			attackerMob.getNavigation().stop();
//		}

		// 1) Lascia passare FUOCO
		if (source.is(DamageTypeTags.IS_FIRE)) {
			return;
		}

		// 2) Lascia passare danni che bypassano invulnerabilitÃƒÂ  vanilla
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return;
		}

		// 3) Armi di legno Ã¢â€ â€™ lascia passare
		ItemStack weapon = getAttackingItem(source);
		if (!weapon.isEmpty() && weapon.is(TagRegistries.WOOD_WEAPONS)) {
		    event.setAmount(event.getAmount() * 1.5f); // 50% in piÃƒÂ¹ di danno
			return;
		}

		// 4) Se l'attaccante stesso:
		// - ha Vampirismo oppure ÃƒÂ¨ un VampireEntity
		// allora il danno viene *gestito con una regola speciale*
		if (source.getEntity() instanceof LivingEntity attacker) {
			if (attacker.hasEffect(EffectRegistries.VAMPIRISM.getHolder().orElseThrow())
					|| attacker instanceof VampireEntity) {

				// Regole di difficoltÃƒÂ  (optionali):
				if (target.level().getDifficulty() == Difficulty.EASY) {
					return; // lascia passare in EASY
				}

				if (target.level().getDifficulty() == Difficulty.NORMAL && target.getRandom().nextBoolean()) {
					return; // 50% di probabilitÃƒÂ  in NORMAL
				}

				// Se la vittima ÃƒÂ¨ un giocatore
				if (target instanceof Player player) {
					if (target.hasEffect(EffectRegistries.VAMPIRISM.getHolder().orElseThrow()))
						return;

					// Se questo danno lo ucciderebbe:
					if (event.getAmount() >= player.getHealth()) {

						// ANNULLA il danno
						event.setAmount(0f);
						event.setCanceled(true);

						// Riporta vita al massimo
						player.setHealth(player.getMaxHealth());

						// Applica effetto Vampirismo
						player.addEffect(new MobEffectInstance(EffectRegistries.VAMPIRISM.getHolder().orElseThrow(),
								20 * 60 * 10, // 10 minuti
								0, true, false, true));

						// Rimuove eventuali flag di morte visuali
						player.hurtMarked = false;

						// Effetto visivo di guarigione
						player.level().levelEvent(null, 1027, player.blockPosition(), 0);
						if (attacker instanceof PathfinderMob attackerMob) {
							attackerMob.setTarget(null);
							attackerMob.getNavigation().stop();
						}
					}
				}
				return;
			}
		}

		// Altrimenti: annulla il danno
		Main.LOGGER.debug("\nAnnulla il danno per vampiro o chi ha vampirismo");
		event.setCanceled(true);
		event.setAmount(0f);
		if (source.getEntity() != null) {
			source.getEntity().hurtMarked = false;
			source.getEntity().level().levelEvent(null, 1027, source.getEntity().blockPosition(), 0);
		}
	}

	private static ItemStack getAttackingItem(DamageSource source) {
		// "entity" = chi ha causato il danno (owner della freccia, mob, player, ecc.)
		Entity attacker = source.getEntity();
		if (attacker instanceof LivingEntity living) {
			return living.getMainHandItem();
		}
		return ItemStack.EMPTY;
	}

	@SubscribeEvent
	public static void onAttackEntity(AttackEntityEvent e) {
		if (IS_PROCESSING.get())
			return;

		if (!(e.getEntity().level() instanceof ServerLevel level) || !(e.getTarget() instanceof LivingEntity target))
			return;

		Player player = e.getEntity();
		ItemStack stack = player.getMainHandItem();
		CustomData cd = stack.get(DataComponents.CUSTOM_DATA);

		if (cd == null || !cd.copyTag().getBoolean("op"))
			return;

		// Esecuzione logica OP
		e.setCanceled(true);
		IS_PROCESSING.set(true);

		try {
			handleOpAttack(level, player, target, cd.copyTag());
		} finally {
			IS_PROCESSING.set(false);
		}
	}

	private static void handleOpAttack(ServerLevel level, Player player, LivingEntity target, CompoundTag tag) {
		float healthBefore = target.getHealth();
		float attackStrength = player.getAttackStrengthScale(0.5F);
		boolean isFullCharge = attackStrength > 0.9F;

		// 1. Calcolo Danno Finale
		float damage = calculateFinalDamage(player, target, tag, attackStrength);
		boolean isCrit = isFullCharge && canPlayerCrit(player);
		if (isCrit)
			damage *= 1.5f;

		// 2. Applicazione Danno
		if (target.hurtServer(level, createDamageSource(level, player, target), damage)) {

			// 3. Effetti Post-Danno (Knockback, Statistiche, Particelle)
			applyKnockback(player, target, isFullCharge);
			updatePlayerStatsAndParticles(level, player, target, healthBefore);
			playAttackEffects(level, player, target, isCrit, isFullCharge);
		}

		player.resetAttackStrengthTicker();
	}

	// --- METODI DI SUPPORTO (LOGICA SEPARATA) ---

	private static float calculateFinalDamage(Player player, LivingEntity target, CompoundTag tag, float strength) {
		float baseDmg = tag.contains("op_damage") ? tag.getFloat("op_damage")
				: ((float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) > 1.0f
						? (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE)
						: 32767.0f);

		return baseDmg * (0.2F + strength * strength * 0.8F);
	}

	private static boolean canPlayerCrit(Player player) {
		return player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() && !player.isInWater()
				&& !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger();
	}

	private static void applyKnockback(Player player, LivingEntity target, boolean isFullCharge) {
		float kbLevel = (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
		if (player.isSprinting() && isFullCharge)
			kbLevel++;

		if (kbLevel > 0.0F) {
			target.knockback(kbLevel * 0.5F, (double) Mth.sin(player.getYRot() * (float) (Math.PI / 180.0)),
					(double) -Mth.cos(player.getYRot() * (float) (Math.PI / 180.0)));

			player.setDeltaMovement(player.getDeltaMovement().multiply(0.6, 1.0, 0.6));
			player.setSprinting(false);
		}
	}

	private static void updatePlayerStatsAndParticles(ServerLevel level, Player player, LivingEntity target,
			float healthBefore) {
		float actualDamage = healthBefore - target.getHealth();
		if (actualDamage <= 0)
			return;

		player.awardStat(Stats.DAMAGE_DEALT, Math.round(actualDamage * 10.0F));

		if (actualDamage > 2.0F) {
			int hearts = (int) Math.min(actualDamage * 0.5, 30); // Cap per evitare lag
			level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.5), target.getZ(), hearts,
					0.1, 0.0, 0.1, 0.2);
		}
	}

	private static void playAttackEffects(ServerLevel level, Player player, LivingEntity target, boolean isCrit,
			boolean isFullCharge) {
		if (isCrit) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT,
					player.getSoundSource(), 1.0F, 1.0F);
			level.getChunkSource().broadcastAndSend(player, new ClientboundAnimatePacket(target, 4));
		} else if (isFullCharge) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG,
					player.getSoundSource(), 1.0F, 1.0F);
		} else {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK,
					player.getSoundSource(), 1.0F, 1.0F);
		}
	}

	private static DamageSource createDamageSource(ServerLevel level, Player player, LivingEntity target) {
		var holder = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(TagRegistries.OP_HIT);
		return new DamageSource(holder, player, target);
	}

	@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public final class EntityEventsVanilla {

		@SubscribeEvent
		public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
			event.put(EntityRegistries.VAMPIRE.get(), VampireEntity.createAttributes().build());
			event.put(EntityRegistries.LYCANTHROPE.get(), LycanthropeEntity.createAttributes().build());
			event.put(EntityRegistries.VAMPIRE_BAT.get(), VampireBatEntity.createAttributes().build());
		}

	}

	@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
	public final class EntityEventsClient {
		@SubscribeEvent
		public static void registerRenderers(RegisterRenderers event) {
			event.registerEntityRenderer(EntityRegistries.VAMPIRE.get(), VampireRenderer::new);
			event.registerEntityRenderer(EntityRegistries.LYCANTHROPE.get(), LycanthropeRenderer::new);
			event.registerEntityRenderer(EntityRegistries.VAMPIRE_BAT.get(), BatRenderer::new);
		}
	}


}
