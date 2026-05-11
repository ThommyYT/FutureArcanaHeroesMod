package it.futurearcana.futurearcanaheroes.events;

import com.mojang.brigadier.CommandDispatcher;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.content.command.MakeOPItemComamand;
import it.futurearcana.futurearcanaheroes.content.command.SpawnCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommandEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        SpawnCommand.register(dispatcher);
        MakeOPItemComamand.register(dispatcher);
    }
}
