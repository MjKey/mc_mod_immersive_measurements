package ru.alextrof94.immersive_measurements.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.alextrof94.immersive_measurements.CustomData;

import java.util.function.Supplier;

public abstract class BaseDisplayRenderer implements SpecialModelRenderer<CustomData> {
    protected abstract float getScreenWidth();
    protected abstract float getScreenHeight();
    protected abstract Vector3f getScreenOffset();

    @Override
    public void render(@Nullable CustomData data, @NotNull ItemDisplayContext displayContext, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay, boolean hasFoil) {
        if (data == null || data.data.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        poseStack.pushPose();

        // 1. Позиционирование на основе параметров дочернего класса
        Vector3f offset = getScreenOffset();
        poseStack.translate(offset.x(), offset.y(), offset.z());
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-90f)).rotateZ((float) Math.toRadians(-90f)));

        // --- РАСЧЕТ МАСШТАБА ---
        float maxWidth = 0;
        for (String line : data.data) {
            maxWidth = Math.max(maxWidth, mc.font.width(line));
        }


        // Высота одной строки обычно 9 пикселей, но mc.font.lineHeight надежнее.
        float lineHeight = mc.font.lineHeight;

        // Общая высота текста в "пикселях шрифта"
        float totalHeightPx = data.data.size() * lineHeight;

        // sw и sh — это физические размеры экрана в блоках (метрах)
        float sw = getScreenWidth();
        float sh = getScreenHeight();

        // Запас (padding) 10% для эстетики
        float availableW = sw * 0.9f;
        float availableH = sh * 0.9f;

        // Масштаб: сколько "метра" приходится на 1 "пиксель шрифта"
        float scaleW = availableW / maxWidth;
        float scaleH = availableH / totalHeightPx;

        // Выбираем минимальный, чтобы влезло и по ширине, и по высоте
        float finalScale = Math.min(scaleW, scaleH);

        // Ограничиваем сверху, чтобы одна буква не была на весь экран
        // 0.04f — это разумный максимум для предметов
        finalScale = Math.min(finalScale, 0.04f);

        // --- 2. ОТРИСОВКА ОСНОВНОГО ТЕКСТА ---
        poseStack.pushPose();
        poseStack.scale(finalScale, -finalScale, finalScale);

        float startY = -(totalHeightPx / 2f);
        for (int i = 0; i < data.data.size(); i++) {
            String line = data.data.get(i);
            float lineW = mc.font.width(line);
            mc.font.drawInBatch(line, -lineW / 2f, startY + (i * mc.font.lineHeight),
                    0xFFFFFF, false, poseStack.last().pose(), buffer,
                    net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880);
        }
        poseStack.popPose();

        // --- 3. ОТРИСОВКА ЗВЕЗДОЧКИ ---
        if (data.isTemporary) {
            poseStack.pushPose();
            poseStack.translate(-sw/2f + 0.05f, sh/2f, 0.005f);
            float starScale = 0.02f;
            poseStack.scale(starScale, -starScale, starScale);
            mc.font.drawInBatch("*", 0, 0, 0xFFFF00, false, poseStack.last().pose(),
                    buffer, net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public static class Unbaked<T extends BaseDisplayRenderer> implements SpecialModelRenderer.Unbaked {
        private final MapCodec<Unbaked<T>> codec;
        private final Supplier<T> factory;

        public Unbaked(Supplier<T> factory) {
            this.factory = factory;
            // Создаем кодек, привязанный к конкретной фабрике дочернего класса
            this.codec = MapCodec.unit(this);
        }

        @Override
        public SpecialModelRenderer<?> bake(@NotNull EntityModelSet entityModelSet) {
            return factory.get();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            // В NeoForge/Minecraft 1.21.x кодеки регистрируются в SpecialModelRenderers
            // Здесь мы возвращаем наш кодек.
            return codec;
        }
    }
}
