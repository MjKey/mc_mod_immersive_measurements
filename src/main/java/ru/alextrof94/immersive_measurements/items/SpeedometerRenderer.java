package ru.alextrof94.immersive_measurements.items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.alextrof94.immersive_measurements.Config;
import ru.alextrof94.immersive_measurements.CustomData;

import java.util.List;

public class SpeedometerRenderer extends BaseDisplayRenderer {
    private double lastX, lastY, lastZ;
    private double accumulatedDistance = 0; // Накопленный путь за секунду
    private long lastUpdateMs = 0;

    private double targetSpeed = 0;    // То, что насчитали за прошлую секунду
    private double displayedSpeed = 0; // Сглаженное значение для экрана
    private static final float LERP_SPEED = 0.1f;

    protected float getScreenWidth() { return 0.7f; }
    @Override protected float getScreenHeight() { return 0.5f; }
    @Override protected Vector3f getScreenOffset() { return new Vector3f(0.6f, 0.08f, 0.5f); }

    public static final Unbaked<SpeedometerRenderer> UNBAKED = new Unbaked<>(SpeedometerRenderer::new);

    @Nullable
    @Override
    public CustomData extractArgument(@NotNull ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return new CustomData(false, List.of("0.0"));
        if (stack != mc.player.getMainHandItem()) return new CustomData(false, List.of("0.0"));

        var entity = player.getVehicle() != null ? player.getVehicle() : player;
        long now = System.currentTimeMillis();

        if (lastUpdateMs != 0) {
            double dx = entity.getX() - lastX;
            double dz = entity.getZ() - lastZ;
            double step = Math.sqrt(dx * dx + dz * dz);

            if (step < 10) {
                accumulatedDistance += step;
            }
        }

        lastX = entity.getX();
        lastZ = entity.getZ();

        long timePassed = now - lastUpdateMs;
        if (timePassed >= 200) {
            targetSpeed = accumulatedDistance / (timePassed / 1000.0);

            accumulatedDistance = 0;
            lastUpdateMs = now;
        }
        displayedSpeed += (targetSpeed - displayedSpeed) * 0.05f;
        if (displayedSpeed < 0.05) displayedSpeed = 0;

        return new CustomData(false, List.of(String.format("%.1f", displayedSpeed)));
    }
}