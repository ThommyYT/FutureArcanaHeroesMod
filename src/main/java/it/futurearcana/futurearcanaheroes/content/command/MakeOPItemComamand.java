package it.futurearcana.futurearcanaheroes.content.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.CustomData;

public class MakeOPItemComamand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
	    dispatcher.register(
	        Commands.literal("opItem")
	            .requires(src -> src.hasPermission(2))
	            .executes(ctx -> makeItemOp(ctx.getSource(), true))
	            .then(
	                Commands.argument("defaultDamage", BoolArgumentType.bool())
	                    .executes(ctx -> makeItemOp(
	                        ctx.getSource(),
	                        BoolArgumentType.getBool(ctx, "defaultDamage")
	                    ))
	            )
	    );
	}

	private static int makeItemOp(CommandSourceStack source, boolean op_damage_default) throws CommandSyntaxException {
	    var player = source.getPlayerOrException();
	    var stack = player.getMainHandItem();

	    if (stack.isEmpty()) {
	        source.sendFailure(Component.literal("❌ Tieni un item in mano."));
	        return 0;
	    }

	    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
	        tag.putBoolean("op", true);

	        if (op_damage_default && !tag.contains("op_damage")) {
	            tag.putFloat("op_damage", 10F);
	        }
	    });

	    source.sendSuccess(() -> Component.literal("✅ Item OP attivato!"), false);
	    return 1;
	}


}
