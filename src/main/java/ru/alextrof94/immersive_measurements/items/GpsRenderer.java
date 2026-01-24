package ru.alextrof94.immersive_measurements.items;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.alextrof94.immersive_measurements.CustomData;
import ru.alextrof94.immersive_measurements.ModDataComponents;

import java.util.List;


public class GpsRenderer extends BaseDisplayRenderer {
    @Override
    protected float getScreenWidth() { return 0.875f;}

    @Override
    protected float getScreenHeight() { return 0.75f; }

    @Override
    protected Vector3f getScreenOffset() { return new Vector3f(0.48f, 0.08f, 0.5f); }

    public static final Unbaked<GpsRenderer> UNBAKED = new Unbaked<>(GpsRenderer::new);

    private static BlockPos tempPos = new BlockPos(0, 0, 0);
    private static long displayUntilTick = 0;

    public static void setTemporaryPos(BlockPos tempPosNew, int ticks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            tempPos = tempPosNew;
            displayUntilTick = mc.level.getGameTime() + ticks;
        }
    }

    @Nullable
    @Override
    public CustomData extractArgument(@NotNull ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.getGameTime() < displayUntilTick) {
            if (mc.player != null && (stack == mc.player.getMainHandItem() || stack == mc.player.getOffhandItem())) {
                return new CustomData(true, List.of(getTime(), "x: " + tempPos.getX(), "y: " + tempPos.getY(), "z: " + tempPos.getZ()));
            }
        }

        if (stack.has(ModDataComponents.TRIANGULATED_POS.get())) {
            var pos = stack.get(ModDataComponents.TRIANGULATED_POS.get());
            if (pos != null)
                return new CustomData(false, List.of(getTime(), "x: " + pos.pos().getX(), "y: " + pos.pos().getY(), "z: " + pos.pos().getZ()));
        }
        return new CustomData(false, List.of(getTime(), "POS N/A"));
    }

    private String getTime() {
        var level = Minecraft.getInstance().level;
        if (level == null) return "00:00";
        long rawTime = level.getDayTime() % 24000;
        int hours = (int) ((rawTime / 1000 + 6) % 24);
        int minutes = (int) ((rawTime % 1000) * 60 / 1000);
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes);
    }
}