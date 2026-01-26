package ru.alextrof94.immersive_measurements.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
        GlobalPos savedPos = null;
        if (stack.has(ModDataComponents.TRIANGULATED_POS.get())) {
            savedPos = stack.get(ModDataComponents.TRIANGULATED_POS.get());
        }


        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.getGameTime() < displayUntilTick) {
            if (mc.player != null && (stack == mc.player.getMainHandItem() || stack == mc.player.getOffhandItem())) {
                BlockPos diff = null;
                if (savedPos != null && mc.level.dimension() == savedPos.dimension())
                    diff = new BlockPos(savedPos.pos().getX() - tempPos.getX(), savedPos.pos().getY() - tempPos.getY(), savedPos.pos().getZ() - tempPos.getZ());
                return new CustomData(true, List.of(getTime(),
                        "x: " + tempPos.getX() + (diff != null ? " (" + diff.getX() + ")" : ""),
                        "y: " + tempPos.getY() + (diff != null ? " (" + diff.getY() + ")" : ""),
                        "z: " + tempPos.getZ() + (diff != null ? " (" + diff.getZ() + ")" : "")));
            }
        }

        if (savedPos != null)
            return new CustomData(false, List.of(getTime(), "x: " + savedPos.pos().getX(), "y: " + savedPos.pos().getY(), "z: " + savedPos.pos().getZ()));
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