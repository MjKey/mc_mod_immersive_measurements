package ru.alextrof94.immersive_measurements.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DigitalClockItem extends BlockItem implements IDisplayItem {
    public DigitalClockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            rightClickAction(level, player, null);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return super.useOn(context);
        }
        if (!context.getLevel().isClientSide && context.getPlayer() != null) {
            return rightClickAction(context.getLevel(), context.getPlayer(), context.getClickedPos());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void leftClickAction(Level level, @NotNull Player player) {
    }

    @Override
    public InteractionResult rightClickAction(Level level, @NotNull Player player, @Nullable BlockPos blockPos) {
        return InteractionResult.SUCCESS;
    }
}
