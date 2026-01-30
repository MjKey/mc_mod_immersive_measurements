package ru.alextrof94.immersive_measurements.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.alextrof94.immersive_measurements.Config;
import ru.alextrof94.immersive_measurements.CustomData;
import ru.alextrof94.immersive_measurements.ModDataComponents;

import java.util.*;
import java.util.stream.Collectors;

public class RadarRenderer extends BaseDisplayRenderer {

    private static final String MAP_MODE_MARKER = "##MAP_MODE##";

    @Override
    protected float getScreenWidth() { return 0.8f; }
    @Override
    protected float getScreenHeight() { return 0.8f; }
    @Override
    protected Vector3f getScreenOffset() { return new Vector3f(0.5f, 0.08f, 0.5f); }

    public static final Unbaked<RadarRenderer> UNBAKED = new Unbaked<>(RadarRenderer::new);

    @Nullable
    @Override
    public CustomData extractArgument(@NotNull ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return new CustomData(false, List.of("No Signal"));

        int mode = stack.getOrDefault(ModDataComponents.RADAR_MODE, RadarItem.MODE_MAP);

        // --- РЕЖИМ КАРТЫ ---
        if (mode == RadarItem.MODE_MAP) {
            return new CustomData(false, List.of(MAP_MODE_MARKER));
        }

        // --- РЕЖИМ СПИСКА ---
        double range = Config.RADAR_MAP_SIZE.get();
        AABB box = new AABB(mc.player.blockPosition()).inflate(range * 0.9);

        // Фильтруем список: живые существа, кроме самого игрока
        List<Entity> entities = mc.level.getEntities(mc.player, box,
                e -> e instanceof LivingEntity && e != mc.player);

        // Переменные для сводки
        int countVillagers = 0;
        int countHostile = 0;
        int countPeaceful = 0;

        // Карта для подсчета конкретных видов
        Map<String, Integer> speciesCount = new HashMap<>();

        for (Entity e : entities) {
            // Подсчет видов
            String name = e.getType().getDescription().getString();
            speciesCount.put(name, speciesCount.getOrDefault(name, 0) + 1);

            // Подсчет категорий
            if (e instanceof Villager) {
                countVillagers++;
            } else if (e instanceof Enemy || e.getType().getCategory() == MobCategory.MONSTER) {
                countHostile++;
            } else {
                // Все остальное считаем мирным/нейтральным (животные, големы и т.д.)
                countPeaceful++;
            }
        }

        // 2. Список существ (сортировка по убыванию количества)
        List<String> speciesLines = speciesCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> shortNaming(entry.getKey()) + ": " + entry.getValue())
                .collect(Collectors.toList());

        if (speciesLines.isEmpty()) {
            speciesLines.add(Component.translatable("item.immersive_measurements.nobody_around").getString());
        }

        List<String> finalLines = new ArrayList<>(speciesLines);

        // 3. Пагинация
        int pageSize = Config.RADAR_PAGE_SIZE.get();
        if (pageSize < 1) pageSize = 4;

        int page = stack.getOrDefault(ModDataComponents.RADAR_PAGE, 0);
        int totalPages = (int) Math.ceil((double) finalLines.size() / pageSize) + 1;

        if (page >= totalPages && totalPages > 0) {
            page %= totalPages;
        }


        List<String> pageOutput = new ArrayList<>();

        // 1. Сводка
        if (page == 0) {
            pageOutput.add("§a" + Component.translatable("item.immersive_measurements.villagers").getString() + ": " + countVillagers);
            pageOutput.add("§c" + Component.translatable("item.immersive_measurements.enemies").getString() + ": " + countHostile);
            pageOutput.add("§f" + Component.translatable("item.immersive_measurements.peaceful").getString() + ": " + countPeaceful);
        }
        else {
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, finalLines.size());
            if (start >= finalLines.size()) {
                pageOutput = List.of(Component.translatable("item.immersive_measurements.page").getString() + " " + (page + 1) + " " + Component.translatable("item.immersive_measurements.empty_page").getString());
            } else {
                pageOutput = new ArrayList<>(finalLines.subList(start, end));
            }
        }


        // Добавляем заголовок в начало списка для рендера
        pageOutput.addFirst("§7= " + Component.translatable("item.immersive_measurements.page").getString() + " " + (page + 1) + "/" + (totalPages == 1 ? 2 : totalPages) + " =");

        return new CustomData(false, pageOutput);
    }


    private static final String alphConsonant = "цкнгшщзхфвпрлджчсмтбъьqwrtpsdfghjklzxcvbnm";
    private String shortNaming(String key) {
        if (key.length() <= 10) return key;

        StringBuilder res = new StringBuilder();
        var strs = key.split("[- ]");
        int w = 0;
        for (String str : strs) {
            if (str.length() <= 6) {
                res.append(str).append(w < strs.length - 1 ? " " : "");
                continue;
            }
            int i = 3;
            //noinspection ConstantValue
            while (!(alphConsonant.indexOf(str.charAt(i)) > -1) && (i < str.length())) {
                i++;
            }
            i++;
            res.append(str, 0, i).append((i < str.length())?".":"").append(w < strs.length - 1 ? " " : "");
            w++;
        }
        return res.toString();
    }

    @Override
    public void render(@Nullable CustomData data, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay, boolean hasFoil) {
        if (data == null || data.data.isEmpty()) return;

        // Если это режим списка (нет маркера карты) -> используем стандартный рендер текста
        if (!data.data.contains(MAP_MODE_MARKER)) {
            super.render(data, displayContext, poseStack, buffer, packedLight, packedOverlay, hasFoil);
            return;
        }

        // --- РЕНДЕР КАРТЫ ---
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        poseStack.pushPose();

        Vector3f offset = getScreenOffset();
        poseStack.translate(offset.x(), offset.y(), offset.z());
        poseStack.mulPose(getDefItemRotation().rotateY((float) Math.toRadians(180)));

        float sw = getScreenWidth();
        float sh = getScreenHeight();
        double mapRadius = Config.RADAR_MAP_SIZE.get(); // Радиус сканирования в блоках


        float scale = (float) (Math.min(sw, sh) / (mapRadius * 2));

        AABB box = new AABB(mc.player.blockPosition()).inflate(mapRadius);
        List<Entity> entities = mc.level.getEntities(mc.player, box, e -> e instanceof LivingEntity && e != mc.player);
        List<Entity> enemies = entities.stream().filter(e -> e.getType().getCategory() == MobCategory.MONSTER).toList();
        List<Entity> villagers = entities.stream().filter(e -> e instanceof Villager).toList();
        List<Entity> players = entities.stream().filter(e -> e instanceof Player).toList();
        List<Entity> neutrals = entities.stream().filter(e -> !enemies.contains(e) && !villagers.contains(e) && !players.contains(e)).toList();

        float playerYaw = mc.player.getYRot();

        VertexConsumer vertexConsumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.gui());
        Matrix4f matrix = poseStack.last().pose();

        float dotSize = (float) (0.8/Config.RADAR_MAP_SIZE.get());
        if (dotSize < 0.01f) dotSize = 0.01f;

        for (Entity e : neutrals) {
            drawEntityonMap(packedLight, e, mc, mapRadius, playerYaw, scale, vertexConsumer, matrix, dotSize, 0xFFFFFFFF, 0.00f);
        }
        for (Entity e : villagers) {
            drawEntityonMap(packedLight, e, mc, mapRadius, playerYaw, scale, vertexConsumer, matrix, dotSize, 0xFF00FF00, -0.01f);
        }
        for (Entity e : enemies) {
            drawEntityonMap(packedLight, e, mc, mapRadius, playerYaw, scale, vertexConsumer, matrix, dotSize, 0xFFFF0000, -0.02f);
        }
        for (Entity e : players) {
            drawEntityonMap(packedLight, e, mc, mapRadius, playerYaw, scale, vertexConsumer, matrix, dotSize, 0xFFFF0000, -0.03f);
        }

        drawQuad(vertexConsumer, matrix, 0, 0, -0.04f, 0.02f, 0xFF00FFFF, packedLight);

        poseStack.popPose();
    }

    private void drawEntityonMap(int packedLight, Entity entity, Minecraft mc, double mapRadius, float playerYaw, float scale, VertexConsumer vertexConsumer, Matrix4f matrix, float dotSize, int color, float z) {
        double dx = 0;
        double dz = 0;
        if (mc.player != null) {
            dx = entity.getX() - mc.player.getX();
            dz = entity.getZ() - mc.player.getZ();
        }

        if (dx*dx + dz*dz > mapRadius * mapRadius) return;
        float rad = (float) Math.toRadians(playerYaw);

        float mapX = (float) (dx * Math.cos(-rad) - dz * Math.sin(-rad));
        float mapY = (float) (dx * Math.sin(-rad) + dz * Math.cos(-rad));

        float sx = mapX * scale;
        float sy = mapY * scale;

        drawQuad(vertexConsumer, matrix, sx, sy, z, dotSize, color, packedLight);
    }

    private void drawQuad(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float size, int color, int light) {
        float half = size / 2f;
        // Извлекаем компоненты цвета
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;

        // Рисуем квадрат на плоскости Z=0 (относительно экрана)
        // Порядок вершин важен для culling, но UI usually disables culling or is double sided
        consumer.addVertex(matrix, x - half, y - half, z).setColor(r, g, b, a).setLight(light);
        consumer.addVertex(matrix, x - half, y + half, z).setColor(r, g, b, a).setLight(light);
        consumer.addVertex(matrix, x + half, y + half, z).setColor(r, g, b, a).setLight(light);
        consumer.addVertex(matrix, x + half, y - half, z).setColor(r, g, b, a).setLight(light);
    }
}