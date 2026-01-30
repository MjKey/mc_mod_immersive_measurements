package ru.alextrof94.immersive_measurements.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.alextrof94.immersive_measurements.ModDataComponents;

public class DepthMeterItem extends BaseDisplayBlockItem {
    public DepthMeterItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    @Override
    public void leftClickAction(Level level, @NotNull Player player) {
        int currentY = player.blockPosition().getY();
        DepthMeterRenderer.setTemporaryY(currentY, 60);
    }

    @Override
    public InteractionResult rightClickAction(Level level, @NotNull Player player, @Nullable BlockPos blockPos) {
        int currentY = player.blockPosition().getY();
        player.getMainHandItem().set(ModDataComponents.SAVED_Y.get(), currentY);
        DepthMeterRenderer.setTemporaryY(currentY, 0);
        return InteractionResult.SUCCESS;
    }
}
