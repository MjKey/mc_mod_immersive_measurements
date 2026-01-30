package ru.alextrof94.immersive_measurements.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.alextrof94.immersive_measurements.ModDataComponents;

public class RadarItem extends BaseDisplayItem {

    public static final int MODE_MAP = 0;
    public static final int MODE_LIST = 1;

    public RadarItem(Properties properties) {
        super(properties);
    }

    @Override
    public void leftClickAction(Level level, @NotNull Player player) {
        //if (level.isClientSide) return; // Логику меняем только на сервере

        var stack = player.getMainHandItem();
        if (stack.getItem() != this) return;

        int currentMode = stack.getOrDefault(ModDataComponents.RADAR_MODE, MODE_MAP);
        int currentPage = stack.getOrDefault(ModDataComponents.RADAR_PAGE, 0);

        if (currentMode == MODE_MAP) {
            stack.set(ModDataComponents.RADAR_MODE, MODE_LIST);
            stack.set(ModDataComponents.RADAR_PAGE, 0);
        } else {
            stack.set(ModDataComponents.RADAR_PAGE, currentPage + 1);
        }
    }

    @Override
    public InteractionResult rightClickAction(Level level, @NotNull Player player, @Nullable BlockPos blockPos) {
        var stack = player.getMainHandItem();
        int currentMode = stack.getOrDefault(ModDataComponents.RADAR_MODE, MODE_MAP);

        if (currentMode != MODE_MAP) {
            stack.set(ModDataComponents.RADAR_MODE, MODE_MAP);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}