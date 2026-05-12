package it.futurearcana.futurearcanaheroes.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.systems.player_state.PlayerArcanaData;
import it.futurearcana.futurearcanaheroes.systems.player_state.PlayerArcanaStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ArcanaCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("arcana")
                        .then(Commands.literal("info").executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            ServerPlayer player = source.getPlayerOrException();
                            PlayerArcanaData d = PlayerArcanaStorage.get(player);
                            player.sendSystemMessage(Component.literal("[Arcana] race=" + d.getRace() + ", level=" + d.getLevel() + ", corruption=" + d.getCorruption()));
                            return 1;
                        }))
        );
        Main.LOGGER.info("Registered /arcana command");
    }
}
