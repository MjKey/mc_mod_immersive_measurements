package ru.alextrof94.immersive_measurements.items;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import ru.alextrof94.immersive_measurements.ModDataComponents;

public class DepthMeterItem extends Item {
    public DepthMeterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            int currentY = player.blockPosition().getY();
            player.getItemInHand(hand).set(ModDataComponents.SAVED_Y.get(), currentY);
        }
        return InteractionResult.SUCCESS;
    }
}
