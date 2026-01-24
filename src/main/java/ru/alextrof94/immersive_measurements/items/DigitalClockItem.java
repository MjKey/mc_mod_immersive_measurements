package ru.alextrof94.immersive_measurements.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import ru.alextrof94.immersive_measurements.ModDataComponents;

public class DigitalClockItem extends Item {
    public DigitalClockItem(Properties properties) {
        super(properties.stacksTo(1));
    }
    @Override
    public InteractionResult use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return InteractionResult.SUCCESS;
    }
}
