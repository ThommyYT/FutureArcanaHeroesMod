package it.futurearcana.futurearcanaheroes.content.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.events.TickEvents;
import it.futurearcana.futurearcanaheroes.registry.DimensionRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChronoBraceletItem extends Item {
	private static final String TAG_DIM = "SavedDim", TAG_X = "SavedX", TAG_Y = "SavedY", TAG_Z = "SavedZ",
			TAG_TIME = "SavedTime";
	// Raggio d'azione (es. 32 blocchi attorno al punto di salvataggio)
	private static final int RADIUS = 32;
	// Struttura dati per memorizzare i blocchi (in una classe di utility o nella
	// tua classe Item)
	private static final Map<UUID, Map<BlockPos, BlockData>> playerSnapshots = new HashMap<>();

	// Record per tenere traccia di stato e NBT del blocco
	record BlockData(BlockState state, @Nullable CompoundTag nbt) {
	}

	public ChronoBraceletItem(Properties properties) {
		super(properties);
	}

	public void captureArea(Level level, Player player, BlockPos center, int radius) {
		UUID playerUUID = player.getUUID();

		// Creiamo o puliamo la mappa specifica per questo giocatore
		Map<BlockPos, BlockData> currentSnapshot = playerSnapshots.computeIfAbsent(playerUUID, k -> new HashMap<>());
		currentSnapshot.clear();

		// Limitiamo l'altezza per evitare crash (es. 20 blocchi sopra e sotto il
		// giocatore)
		BlockPos min = center.offset(-radius, -20, -radius);
		BlockPos max = center.offset(radius, 20, radius);

		for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
			BlockState state = level.getBlockState(pos);
			CompoundTag nbt = null;

			BlockEntity be = level.getBlockEntity(pos);
			if (be != null) {
				nbt = be.saveWithFullMetadata(level.registryAccess());
			}

			// Usiamo immutable() perché l'iteratore di betweenClosed riusa lo stesso
			// oggetto BlockPos
			currentSnapshot.put(pos.immutable(), new BlockData(state, nbt));
		}
	}

	public void restoreArea(ServerLevel level, ServerPlayer player) {
	    UUID playerUUID = player.getUUID();
	    Map<BlockPos, BlockData> snapshot = playerSnapshots.get(playerUUID);

	    if (snapshot == null || snapshot.isEmpty()) {
	        return;
	    }

	    snapshot.forEach((pos, data) -> {
	        // Flag 3: Aggiorna il blocco e notifica i client
	        level.setBlock(pos, data.state(), 3);

	        if (data.nbt() != null) {
	            BlockEntity be = level.getBlockEntity(pos);
	            if (be != null) {
	                be.loadWithComponents(data.nbt(), level.registryAccess());
	            }
	        }
	    });
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (level.isClientSide || !(player instanceof ServerPlayer sp))
			return InteractionResult.SUCCESS;

		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag tag = data.copyTag();

		// Shift + tasto destro: imposta un "Time Marker" (alla Quantum Leap)
		if (player.isShiftKeyDown()) {
			BlockPos p = player.blockPosition();
			captureArea(level, sp, p, RADIUS);
			tag.putString(TAG_DIM, level.dimension().location().toString());
			tag.putInt(TAG_X, p.getX());
			tag.putInt(TAG_Y, p.getY());
			tag.putInt(TAG_Z, p.getZ());
			tag.putLong(TAG_TIME, level.getDayTime());
			// Scrive il CompoundTag aggiornato nello stack
			CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);

			player.displayClientMessage(Component.translatable("message." + Main.MODID + ".chrono_saved"), true);
			return InteractionResult.SUCCESS;
		}

		if (!tag.contains(TAG_DIM)) {
			player.displayClientMessage(Component.translatable("message." + Main.MODID + ".chrono_missing"), true);
			return InteractionResult.SUCCESS;
		}

		ResourceLocation dimLoc = ResourceLocation.tryParse(tag.getString(TAG_DIM));
		if (dimLoc == null) {
			player.displayClientMessage(Component.translatable("message." + Main.MODID + ".chrono_invalid"), true);
			return InteractionResult.SUCCESS;
		}

		ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, dimLoc);
		ServerLevel target = sp.server.getLevel(key);
		if (target == null) {
			player.displayClientMessage(Component.translatable("message." + Main.MODID + ".chrono_dim_unloaded"), true);
			return InteractionResult.SUCCESS;
		}

		// CLICK NORMALE: Inizia il viaggio temporale
		if (tag.contains(TAG_X)) {
			ServerLevel tunnel = sp.server.getLevel(DimensionRegistries.TEMPORAL_TUNNEL_LEVEL);
			if (tunnel != null) {
				// 1. Spedisci nel Tunnel (Limbo)
				sp.teleportTo(tunnel, sp.getX(), 1.0, sp.getZ(), Set.of(), sp.getYRot(), sp.getXRot(), true);

				// 2. Avvia il ripristino con un ritardo (es. 3 secondi / 60 tick)
				// Usiamo il server per pianificare il ritorno
				float tick = sp.server.tickRateManager().tickrate()*15;
				int cooldown = (int)tick;
				sp.getCooldowns().addCooldown(stack, cooldown);
				sp.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, cooldown-2, 2)); // Effetto disorientamento
				sp.addEffect(new MobEffectInstance(MobEffects.CONFUSION, cooldown-2, 2)); // Effetto disorientamento


				TickEvents.addDelayedAction(sp.getUUID(), tick, () -> {
				    restoreArea(target, sp);
				    double x = tag.getInt(TAG_X) + 0.5;
				    double y = tag.getInt(TAG_Y) + 1.0;
				    double z = tag.getInt(TAG_Z) + 0.5;
				    long time = tag.getLong(TAG_TIME);

				    target.setDayTime(time);
				    sp.teleportTo(target, x, y, z, Set.of(), sp.getYRot(), sp.getXRot(), true);
				    sp.removeEffect(MobEffects.BLINDNESS);
				    sp.removeEffect(MobEffects.CONFUSION);
				    target.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0, 0);
				    sp.displayClientMessage(Component.translatable("message." + Main.MODID + ".chrono_leap"), true);
				});
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.FAIL;
	}
}
