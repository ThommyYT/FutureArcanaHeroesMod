package it.futurearcana.futurearcanaheroes.content.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

public class SpawnCommand {

	// ✅ Suggerisce TUTTI i mob registrati (minecraft + mod)
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_ALL_ENTITIES = (context, builder) -> {
		String remaining = builder.getRemainingLowerCase();

		for (ResourceLocation id : ForgeRegistries.ENTITY_TYPES.getKeys()) {
			String full = id.toString(); // es: minecraft:zombie
			// permette sia "minecraft:zombie" sia "zombie"
			if (full.startsWith(remaining) || id.getPath().startsWith(remaining)) {
				builder.suggest(full);
			}
		}
		return builder.buildFuture();
	};

	// ✅ Suggerimenti per count
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_COUNT = (context, builder) -> {
		builder.suggest(1);
		builder.suggest(5);
		builder.suggest(10);
		builder.suggest(20);
		builder.suggest(50);
		builder.suggest(100);
		return builder.buildFuture();
	};

	// ✅ Suggerimenti per spread
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_SPREAD = (context, builder) -> {
		builder.suggest(0);
		builder.suggest(3);
		builder.suggest(5);
		builder.suggest(10);
		builder.suggest(16);
		builder.suggest(32);
		return builder.buildFuture();
	};

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("spawn").requires(src -> src.hasPermission(2)).then(Commands
				.argument("mob", ResourceLocationArgument.id()).suggests(SUGGEST_ALL_ENTITIES) // <-- oppure
																								// SUGGEST_ONLY_MOD_ENTITIES
				.then(Commands.argument("count", IntegerArgumentType.integer(1, 200))
						.executes(ctx -> spawnMany(ctx.getSource(), ResourceLocationArgument.getId(ctx, "mob"),
								IntegerArgumentType.getInteger(ctx, "count"), 3))
						.suggests(SUGGEST_COUNT)
						.then(Commands.argument("spread", IntegerArgumentType.integer(0, 32)).suggests(SUGGEST_SPREAD)
								.executes(ctx -> spawnMany(ctx.getSource(), ResourceLocationArgument.getId(ctx, "mob"),
										IntegerArgumentType.getInteger(ctx, "count"),
										IntegerArgumentType.getInteger(ctx, "spread")))))));
	}

	@SuppressWarnings("deprecation")
	private static int spawnMany(CommandSourceStack source, ResourceLocation mobId, int count, int spread) {
		ServerLevel level = source.getLevel();

		EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(mobId);
		if (type == null) {
			source.sendFailure(Component.literal("❌ EntityType non trovato: " + mobId));
			return 0;
		}


		BlockPos base = BlockPos.containing(source.getPosition());
		RandomSource random = level.getRandom();

		int spawned = 0;

		for (int i = 0; i < count; i++) {
			int dx = (spread == 0) ? 0 : random.nextInt(spread * 2 + 1) - spread;
			int dz = (spread == 0) ? 0 : random.nextInt(spread * 2 + 1) - spread;

			BlockPos tryPos = base.offset(dx, 0, dz);
			BlockPos spawnPos = findSpawnPosNearY(level, tryPos, base.getY());

//			Main.LOGGER.debug("Spwawn pos: \nX: " + spawnPos.getX() + "\nY: " + spawnPos.getY() + "\nZ: " + spawnPos.getZ());
			Entity entity = type.create(level, null);
			if (entity == null)
				continue;

			entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, random.nextFloat() * 360f, 0f);

			if (entity instanceof Mob mob) {
				DifficultyInstance diff = level.getCurrentDifficultyAt(spawnPos);
				mob.finalizeSpawn(level, diff, EntitySpawnReason.COMMAND, null);
			}

			// aggiunge l'entità al mondo
			level.addFreshEntity(entity);
			spawned++;
		}

		final int spawnedCount = spawned;
		source.sendSuccess(() -> Component.literal("✅ Spawnati " + spawnedCount + " mob di tipo: " + mobId), false);
		return spawned;
	}

	@SuppressWarnings("deprecation")
	private static BlockPos findSpawnPosNearY(ServerLevel level, BlockPos xzPos, int startY) {
//		Main.LOGGER.debug("MinSectionY: " + level.getMinSectionY() + "\nMinY: " + level.getMinY());
	    int minY = level.getMinY() + 1;

	    BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(xzPos.getX(), startY, xzPos.getZ());

	    // Scende finché trova un punto spawnabile (aria + aria sopra + blocco solido sotto)
	    for (int y = startY; y > minY; y--) {
	        p.setY(y);

	        boolean feetFree = level.getBlockState(p).getCollisionShape(level, p).isEmpty();
	        boolean headFree = level.getBlockState(p.above()).getCollisionShape(level, p.above()).isEmpty();
	        boolean solidBelow = level.getBlockState(p.below()).blocksMotion();

	        if (feetFree && headFree && solidBelow) {
	            return p.immutable();
	        }
	    }

	    // Fallback (se non trova nulla): almeno non crasha
	    return new BlockPos(xzPos.getX(), startY, xzPos.getZ());
	}

}
