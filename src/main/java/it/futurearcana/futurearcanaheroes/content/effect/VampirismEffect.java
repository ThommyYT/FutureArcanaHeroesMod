package it.futurearcana.futurearcanaheroes.content.effect;

import it.futurearcana.futurearcanaheroes.registry.BiomeRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VampirismEffect extends MobEffect {
	public VampirismEffect() {
		super(MobEffectCategory.BENEFICIAL, 0x9B111E);
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		// Vampiri: molto forti e vedono al buio, ma soffrono la luce diretta del sole.
		entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
		entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, amplifier, false, false));
		entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, amplifier, false, false));
		if (level.isDay() && !level.isRaining()) {
			BlockPos pos = entity.blockPosition();
			boolean inSun = level.canSeeSky(pos) && level.getMaxLocalRawBrightness(pos) >= 13,
					inBiome = entity.level().getBiome(entity.getBlockPosBelowThatAffectsMyMovement())
							.is(BiomeRegistries.VAMPIRE_FOREST),
					inCreative = (entity instanceof Player p && p.isCreative());
			ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.HEAD);
			if (inSun) {
				if (!inCreative && !inBiome) {
					if (itemstack.isEmpty()) {
						// 1.21.x: setSecondsOnFire e' stato sostituito (igniteForSeconds)
						entity.igniteForSeconds(1);
						entity.hurtServer(level, level.damageSources().onFire(), 1.0F + amplifier);
					} else {
						if (itemstack.isDamageableItem()) {
							Item item = itemstack.getItem();
							itemstack.setDamageValue(itemstack.getDamageValue() + entity.getRandom().nextInt(2));
							if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
								entity.onEquippedItemBroken(item, EquipmentSlot.HEAD);
								entity.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
							}
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}
