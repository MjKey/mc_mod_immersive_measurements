package ru.alextrof94.immersive_measurements.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DigitalClockItem extends BaseDisplayBlockItem {
    public DigitalClockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @Override
    public void leftClickAction(Level level, @NotNull Player player) {
    }

    @Override
    public InteractionResult rightClickAction(Level level, @NotNull Player player, @Nullable BlockPos blockPos) {
        return InteractionResult.SUCCESS;
    }
}
