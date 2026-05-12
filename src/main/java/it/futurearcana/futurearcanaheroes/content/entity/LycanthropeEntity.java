package it.futurearcana.futurearcanaheroes.content.entity;

import it.futurearcana.futurearcanaheroes.registry.EntityRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LycanthropeEntity extends Monster implements GeoEntity {

	private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.lycanthrope.idle");
	private static final RawAnimation WALK_LOOP = RawAnimation.begin().thenLoop("animation.lycanthrope.walk");
	private static final RawAnimation RUN_LOOP = RawAnimation.begin().thenLoop("animation.lycanthrope.run");

	// transizioni + loop automatico
	private static final RawAnimation TO_RUN = RawAnimation.begin().thenPlay("animation.lycanthrope.down")
			.thenLoop("animation.lycanthrope.run");

	private static final RawAnimation TO_WALK = RawAnimation.begin().thenPlay("animation.lycanthrope.up")
			.thenLoop("animation.lycanthrope.walk");

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	private static final EntityDataAccessor<Boolean> DATA_RUNNING = SynchedEntityData.defineId(LycanthropeEntity.class,
			EntityDataSerializers.BOOLEAN);

	private static final AttributeModifier RUN_SPEED_MOD = new AttributeModifier(EntityRegistries.LYCANTHROPE.getId(),
			0.45D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	private int chaseTicks = 0;
	private int calmTicks = 0;
	private boolean lastRunningForAnim = false;

	private static final int RUN_AFTER_TICKS = 40; // 2 secondi
	private static final int STOP_AFTER_TICKS = 25; // 1.25 secondi

	public LycanthropeEntity(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(Builder p_335882_) {
		super.defineSynchedData(p_335882_);
		p_335882_.define(DATA_RUNNING, false);
	}

	public boolean isRunning() {
		return this.entityData.get(DATA_RUNNING);
	}

	private void setRunning(boolean value) {
		this.entityData.set(DATA_RUNNING, value);
	}

	@Override
	protected void registerGoals() {
		// AI base come i mob vanilla (ronda / inseguimento / attacco)
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.05D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.33D)
				.add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.FOLLOW_RANGE, 28.0D);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 5, state -> {
			boolean moving = state.isMoving();
			boolean running = isRunning();

			// Gestione delle transizioni "Una Tantum"
			if (running != lastRunningForAnim) {
				lastRunningForAnim = running;
				// setAndContinue qui avvia la sequenza TO_RUN o TO_WALK definita sopra
				state.setAndContinue(running ? TO_RUN : TO_WALK);
				return PlayState.CONTINUE;
			}

			// Se il controller sta giÃƒÂ  riproducendo TO_RUN o TO_WALK, lasciamolo finire
			// controllando se l'animazione corrente ÃƒÂ¨ una di quelle di transizione
			String currentAnim = state.getController().getCurrentAnimation() != null
					? state.getController().getCurrentAnimation().animation().name()
					: "";

			if (currentAnim.contains("down") || currentAnim.contains("up")) {
				return PlayState.CONTINUE;
			}

			// Logica standard di movimento/idle
			if (!moving) {
				return state.setAndContinue(IDLE);
			} else {
				return state.setAndContinue(running ? RUN_LOOP : WALK_LOOP);
			}
		}));
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide)
			return;

		LivingEntity target = this.getTarget();

		boolean isChasing = target != null && this.distanceToSqr(target) > (4 * 4);

		if (isChasing) {
			chaseTicks++;
			calmTicks = 0;
		} else {
			calmTicks++;
			chaseTicks = 0;
		}

		if (chaseTicks >= RUN_AFTER_TICKS) {
			setRunning(true);
		}

		if (calmTicks >= STOP_AFTER_TICKS) {
			setRunning(false);
		}

		// applico/rimuovo boost speed
		var attr = this.getAttribute(Attributes.MOVEMENT_SPEED);
		if (attr != null) {
			if (isRunning()) {
				if (!attr.hasModifier(RUN_SPEED_MOD.id()))
					attr.addTransientModifier(RUN_SPEED_MOD);
			} else {
				attr.removeModifier(RUN_SPEED_MOD.id());
			}
		}
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
