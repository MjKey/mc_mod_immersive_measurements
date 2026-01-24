package ru.alextrof94.immersive_measurements.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.alextrof94.immersive_measurements.CustomData;
import ru.alextrof94.immersive_measurements.ModDataComponents;

import java.util.List;

public class DepthMeterRenderer extends BaseDisplayRenderer {
    @Override
    protected float getScreenWidth() { return 0.875f; }

    @Override
    protected float getScreenHeight() { return 0.5f; }

    @Override
    protected Vector3f getScreenOffset() { return new Vector3f(0.4f, 0.08f, 0.5f); }

    public static final Unbaked<DepthMeterRenderer> UNBAKED = new Unbaked<>(DepthMeterRenderer::new);

    private static int tempY = 0;
    private static long displayUntilTick = 0;

    public static void setTemporaryY(int y, int ticks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            tempY = y;
            displayUntilTick = mc.level.getGameTime() + ticks;
        }
    }

    @Nullable
    @Override
    public CustomData extractArgument(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.getGameTime() < displayUntilTick) {
            if (mc.player != null && (stack == mc.player.getMainHandItem() || stack == mc.player.getOffhandItem())) {
                return new CustomData(true, List.of(tempY + ""));
            }
        }

        if (stack.has(ModDataComponents.SAVED_Y.get())) {
            return new CustomData(false, List.of(stack.get(ModDataComponents.SAVED_Y.get()).toString()));
        }
        return new CustomData(false, List.of("N/A"));
    }

}