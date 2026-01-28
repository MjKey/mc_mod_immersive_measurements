package ru.alextrof94.immersive_measurements.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import java.util.Map;
import java.util.WeakHashMap;

public class SpeedometerClientTracker {
    private static final Map<Entity, SpeedData> cache = new WeakHashMap<>();

    private static class SpeedData {
        Vec3 lastPos;
        long lastTime;
        double currentDisplaySpeed;

        SpeedData(Vec3 pos, long time) {
            this.lastPos = pos;
            this.lastTime = time;
            this.currentDisplaySpeed = 0;
        }
    }

    public static void update(Entity entity) {
        long now = System.currentTimeMillis();
        Vec3 currentPos = entity.position();

        SpeedData data = cache.computeIfAbsent(entity, e -> new SpeedData(currentPos, now));

        long timeDelta = now - data.lastTime;

        if (timeDelta >= 50) {
            double dist = currentPos.distanceTo(data.lastPos);

            double instantSpeed = dist / (timeDelta / 1000.0);
            data.currentDisplaySpeed += (instantSpeed - data.currentDisplaySpeed) * 0.15f;

            data.lastPos = currentPos;
            data.lastTime = now;
        }
    }

    public static double getSpeed(Entity entity) {
        SpeedData data = cache.get(entity);
        return data != null ? data.currentDisplaySpeed : 0.0;
    }
}