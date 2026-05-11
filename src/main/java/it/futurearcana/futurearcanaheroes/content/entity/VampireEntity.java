package it.futurearcana.futurearcanaheroes.content.entity;

import it.futurearcana.futurearcanaheroes.registry.BiomeRegistries;
import it.futurearcana.futurearcanaheroes.registry.EffectRegistries;
import it.futurearcana.futurearcanaheroes.registry.EntityRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VampireEntity extends Monster implements GeoEntity {

// private static final Logger LOGGER = Main.LOGGER;

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.vampire.idle");
	private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.vampire.walk");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private int humanoidFormTicks = 0;

	private static final int STOP_HUMANOID_FORM_TICKS = 1200; // 1.25 secondi

	public VampireEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	protected boolean isSunSensitive() {
		return true;
	}

	@Override
	public void aiStep() {
		if (this.isAlive()) {
			boolean flag = isSunSensitive() && isSunBurnTick();
			if (flag) {
				ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
				if (!itemstack.isEmpty()) {
					if (itemstack.isDamageableItem()) {
						Item item = itemstack.getItem();
						itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
						if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
							this.onEquippedItemBroken(item, EquipmentSlot.HEAD);
							this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
						}
					}

					flag = false;
				}

				if (this.level().getBiome(this.getBlockPosBelowThatAffectsMyMovement())
						.is(BiomeRegistries.VAMPIRE_FOREST)) {
					flag = false;
				}

				if (flag) {
					this.igniteForSeconds(8.0F);
				}
			}
		}
		super.aiStep();
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide)
			return;

		LivingEntity target = this.getTarget();

		boolean isChasing = target != null && this.canAttack(target);

		if (!isChasing) {
			humanoidFormTicks++;

			if (humanoidFormTicks >= STOP_HUMANOID_FORM_TICKS) {
				// Transformazione in forma bat
				convertTo(EntityRegistries.VAMPIRE_BAT.get(), ConversionParams.single(this, false, false), bat -> {
					if (bat instanceof VampireBatEntity vbat) {
						vbat.getPersistentData().putFloat("Health", getHealth());
					}
				});

			}
		}

	}

	@Override
	protected void registerGoals() {
		// AI base come i mob vanilla (ronda / inseguimento / attacco)
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new PlayerNearestAttackableTargetGoal(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.50D)
				.add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.FOLLOW_RANGE, 24.0D);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 5, state -> {
			if (state.isMoving()) {
				state.setAndContinue(WALK);
			} else {
				state.setAndContinue(IDLE);
			}
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

//	@Override
//	public boolean killedEntity(ServerLevel level, LivingEntity target) {
//		boolean flag = super.killedEntity(level, target);
//		if ((level.getDifficulty() == Difficulty.NORMAL || level.getDifficulty() == Difficulty.HARD)
//				&& target instanceof Villager villager) {
//			if (level.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
//				return flag;
//			}
//
//			if (convertVillagerToVampire(level, villager, this)) {
//				flag = false;
//			}
//		}
//		return flag;
//	}

	public static final boolean convertVillagerToVampire(ServerLevel level, Villager villager, LivingEntity entity) {
		if (!ForgeEventFactory.canLivingConvert(villager, EntityRegistries.VAMPIRE.get(), (timer) -> {
		}))
			return false;
		VampireEntity vampire = villager.convertTo(EntityRegistries.VAMPIRE.get(),
				ConversionParams.single(villager, true, true), conversion -> {
					ForgeEventFactory.onFinalizeSpawn(villager, level,
							level.getCurrentDifficultyAt(conversion.blockPosition()), EntitySpawnReason.CONVERSION,
							null);
					ForgeEventFactory.onLivingConvert(villager, conversion);
					if (!entity.isSilent()) {
						level.levelEvent(null, 1026, entity.blockPosition(), 0);
					}
				});
		return vampire != null;
	}

	class PlayerNearestAttackableTargetGoal extends NearestAttackableTargetGoal<Player> {

		public PlayerNearestAttackableTargetGoal(Mob p_26060_, Class<Player> p_26061_, boolean p_26062_) {
			super(p_26060_, p_26061_, p_26062_);
		}

		@Override
		public void start() {
			if (!target.hasEffect(EffectRegistries.VAMPIRISM.getKey().getOrThrow(target))) {
				super.start();
			}
		}

	}
}
