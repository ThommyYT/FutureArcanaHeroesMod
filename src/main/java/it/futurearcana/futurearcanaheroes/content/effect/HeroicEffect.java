package it.futurearcana.futurearcanaheroes.content.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class HeroicEffect extends MobEffect {
	public HeroicEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xF1C40F);
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {

		entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0 + amplifier, false, false));
		entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 0 + amplifier, false, false));
		entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));

		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}
