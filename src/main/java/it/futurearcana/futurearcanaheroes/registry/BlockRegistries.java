package it.futurearcana.futurearcanaheroes.registry;

import it.futurearcana.futurearcanaheroes.Main;
import it.futurearcana.futurearcanaheroes.content.block.QuantumConsoleBlock;
import it.futurearcana.futurearcanaheroes.content.block.TemporalAnchorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BlockRegistries {
    private BlockRegistries() {}

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

    public static final RegistryObject<Block> QUANTUM_CONSOLE = BLOCKS.register("quantum_console",
            () -> new QuantumConsoleBlock(BlockBehaviour.Properties.of()
                    .setId(BLOCKS.key("quantum_console"))
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> TEMPORAL_ANCHOR = BLOCKS.register("temporal_anchor",
            () -> new TemporalAnchorBlock(BlockBehaviour.Properties.of()
                    .setId(BLOCKS.key("temporal_anchor"))
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(4.0F)
                    .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> TEMPORAL_TUNNEL = BLOCKS.register("temporal_tunnel",
            () -> new Block(BlockBehaviour.Properties.of()
                    .setId(BLOCKS.key("temporal_tunnel"))
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel(p_152605_ -> 2)
                    .noLootTable()
            )
    );
}
