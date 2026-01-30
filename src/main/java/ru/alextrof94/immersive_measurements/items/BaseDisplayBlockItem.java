package ru.alextrof94.immersive_measurements.items;

import net.minecraft.MethodsReturnNonnullByDefault;
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

public abstract class BaseDisplayBlockItem extends BlockItem implements IDisplayItem {
    public BaseDisplayBlockItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @MethodsReturnNonnullByDefault
    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }

        rightClickAction(level, player, null);
        return InteractionResult.SUCCESS;
    }

    @MethodsReturnNonnullByDefault
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getHand() == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            return super.useOn(context);
        }
        if (context.getPlayer() != null) {
            return rightClickAction(context.getLevel(), context.getPlayer(), context.getClickedPos());
        }
        return InteractionResult.SUCCESS;
    }

    public abstract void leftClickAction(Level level, @NotNull Player player);

    public abstract InteractionResult rightClickAction(Level level, @NotNull Player player,
                                                           @Nullable BlockPos blockPos);
}
