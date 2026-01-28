package ru.alextrof94.immersive_measurements.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDisplayItem {
    void leftClickAction(Level level, @NotNull Player player);

    InteractionResult rightClickAction(Level level, @NotNull Player player, @Nullable BlockPos blockPos);
}
