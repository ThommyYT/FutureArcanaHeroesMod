package it.futurearcana.futurearcanaheroes.content.item;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class EssenceElixirItem extends Item {
	private final Supplier<Holder<MobEffect>> effect;
	private final int durationTicks;
	private final int amplifier;

	public EssenceElixirItem(Properties properties, Supplier<Holder<MobEffect>> effect, int durationTicks,
			int amplifier) {
		super(properties);
		this.effect = effect;
		this.durationTicks = durationTicks;
		this.amplifier = amplifier;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.DRINK;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		ItemStack out = super.finishUsingItem(stack, level, entity);

		if (!level.isClientSide) {
			Holder<MobEffect> eff = effect.get();
			if (eff != null) {
				// 1.21.x: MobEffectInstance richiede un Holder<MobEffect>
				entity.addEffect(new MobEffectInstance(eff, durationTicks, amplifier));
			}
		}

		if (entity instanceof Player p && !p.getAbilities().instabuild && !(out.getCount() >= 1)) {
			stack.shrink(1);

			ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
			if (stack.isEmpty())
				return bottle;
			if (!p.getInventory().add(bottle))
				p.drop(bottle, false);

		}

		return out;
	}
}
