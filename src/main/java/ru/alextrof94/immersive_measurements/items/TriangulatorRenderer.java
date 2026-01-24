package ru.alextrof94.immersive_measurements.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.alextrof94.immersive_measurements.CustomData;
import ru.alextrof94.immersive_measurements.ModDataComponents;

import java.util.List;


public class TriangulatorRenderer extends BaseDisplayRenderer {
    @Override
    protected float getScreenWidth() { return 0.875f;}

    @Override
    protected float getScreenHeight() { return 0.5f; }

    @Override
    protected Vector3f getScreenOffset() { return new Vector3f(0.46f, 0.08f, 0.5f); }

    public static final BaseDisplayRenderer.Unbaked<TriangulatorRenderer> UNBAKED = new BaseDisplayRenderer.Unbaked<>(TriangulatorRenderer::new);

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
    public CustomData extractArgument(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.level.getGameTime() < displayUntilTick) {
            if (mc.player != null && (stack == mc.player.getMainHandItem() || stack == mc.player.getOffhandItem())) {
                return new CustomData(true, List.of("x: " + tempPos.getX(), "z: " + tempPos.getZ()));
            }
        }

        if (stack.has(ModDataComponents.TRIANGULATED_POS.get())) {
            var a = stack.get(ModDataComponents.TRIANGULATED_POS.get());
            if (a == null)
                return new CustomData(false, List.of("NO", "DATA"));

            return new CustomData(false, List.of("x: " + a.pos().getX(), "z: " + a.pos().getZ()));
        }
        return new CustomData(false, List.of("NO", "DATA"));
    }
}