package it.futurearcana.futurearcanaheroes.content.item;

import it.futurearcana.futurearcanaheroes.Main;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;

import java.util.Comparator;

public class ArcaneStaffItem extends Item {
    private static final double RANGE = 20.0;

    public ArcaneStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            LivingEntity target = findTarget(level, player);

            if (target != null) {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0));

                if (level instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.ENCHANT, target.getX(), target.getY() + 1.0, target.getZ(), 30, 0.3, 0.6, 0.3, 0.1);
                }

                player.displayClientMessage(Component.translatable("message." + Main.MODID + ".staff_hit"), true);
                // In 1.21.4 i cooldown accettano direttamente l'ItemStack
                player.getCooldowns().addCooldown(stack, 20);

                // In 1.21.4 ItemStack#hurtAndBreak usa EquipmentSlot (niente Consumer)
                EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND)
                        ? EquipmentSlot.MAINHAND
                        : EquipmentSlot.OFFHAND;
                stack.hurtAndBreak(1, player, slot);

                return InteractionResult.CONSUME;
            } else {
                player.displayClientMessage(Component.translatable("message." + Main.MODID + ".staff_no_target"), true);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private LivingEntity findTarget(Level level, Player player) {
        Vec3 eyes = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();

        // Cerca entitÃƒÂ  nel cono visivo
        return level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(RANGE))
                .stream()
                .filter(e -> e != player)
                .filter(player::hasLineOfSight)
                .filter(e -> {
                    Vec3 dir = e.getEyePosition(1.0F).subtract(eyes).normalize();
                    double dot = dir.dot(look);
                    return dot > 0.94; // ~20Ã‚Â°
                })
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                .orElse(null);
    }
}
