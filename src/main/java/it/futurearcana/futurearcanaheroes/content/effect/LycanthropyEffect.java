package it.futurearcana.futurearcanaheroes.content.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class LycanthropyEffect extends MobEffect {
    public LycanthropyEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x6D6D6D);
    }

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		boolean night = !level.isDay();
        if (night) {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, amplifier, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, amplifier, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0, false, false));
        } else {
            // Di giorno rimane un po' di forza, ma piÃƒÂ¹ "umana".
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, true, false, true));
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
