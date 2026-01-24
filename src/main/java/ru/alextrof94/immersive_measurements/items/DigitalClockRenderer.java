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

public class DigitalClockRenderer extends BaseDisplayRenderer {
    protected float getScreenWidth() { return 0.875f;}

    @Override
    protected float getScreenHeight() { return 0.6f; }

    @Override
    protected Vector3f getScreenOffset() { return new Vector3f(0.48f, 0.08f, 0.5f); }

    public static final BaseDisplayRenderer.Unbaked<DigitalClockRenderer> UNBAKED = new BaseDisplayRenderer.Unbaked<>(DigitalClockRenderer::new);

    @Nullable
    @Override
    public CustomData extractArgument(ItemStack stack) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return new CustomData(false, List.of("00:00"));
        }

        long rawTime = level.getDayTime() % 24000;

        int hours = (int) ((rawTime / 1000 + 6) % 24);
        int minutes = (int) ((rawTime % 1000) * 60 / 1000);

        return new CustomData(false, List.of(String.format("%02d", hours) + ":" + String.format("%02d", minutes)));
    }
}