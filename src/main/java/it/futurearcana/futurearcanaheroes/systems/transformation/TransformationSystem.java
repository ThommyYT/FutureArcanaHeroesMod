package it.futurearcana.futurearcanaheroes.systems.transformation;

import net.minecraft.world.entity.player.Player;

/**
 * Stub for transformation system. Responsible for handling entity/ player transformations (vampire/lycanthrope).
 */
public final class TransformationSystem {
    private TransformationSystem() {}

    public static void applyTransformation(Player player, String transformationId) {
        // TODO: implement transformation orchestration (capabilities, effects, entity swap if needed)
    }

    public static void revertTransformation(Player player) {
        // TODO
    }
}
