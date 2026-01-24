package ru.alextrof94.immersive_measurements.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.alextrof94.immersive_measurements.Config;
import ru.alextrof94.immersive_measurements.ModDataComponents;

import java.util.ArrayList;
import java.util.List;

import static ru.alextrof94.immersive_measurements.ModItems.updateItemModelFromLeds;

public class TriangulatorItem extends BaseDisplayItem {
    public TriangulatorItem(Properties properties) { super(properties); }

    @Override
    public void leftClickAction(Level level, @NotNull Player player) {
        TriangulatorRenderer.setTemporaryPos(player.blockPosition(), 60);
    }

    @Override
    public InteractionResult rightClickAction(Level level, @NotNull Player player, @Nullable BlockPos blockPos) {
        ItemStack stack = player.getMainHandItem();

        if (blockPos != null) {
            BlockState state = level.getBlockState(blockPos);
            List<GlobalPos> positions = new ArrayList<>(stack.getOrDefault(ModDataComponents.LODESTONE_POSITIONS.get(), List.of()));
            validateLodestones(level, positions, player);

            if (state.is(Blocks.LODESTONE)) {
                GlobalPos newPos = GlobalPos.of(level.dimension(), blockPos);

                if (positions.contains(newPos)) {
                    player.displayClientMessage(Component.literal("Магнетит уже записан").withStyle(ChatFormatting.GREEN), true);
                    return InteractionResult.SUCCESS;
                }

                positions.add(newPos);
                if (positions.size() > 3) {
                    positions.removeFirst();
                }

                stack.set(ModDataComponents.LODESTONE_POSITIONS.get(), positions);
                stack.set(ModDataComponents.LEDS_COUNT.get(), positions.size());
                updateItemModelFromLeds(stack, "triangulator");

                return InteractionResult.SUCCESS;
            }
        }
        rightClickToNotLodestone(level, player, stack);
        return InteractionResult.SUCCESS;
    }

    private void rightClickToNotLodestone(Level level, @NotNull Player player, ItemStack stack) {
        List<GlobalPos> positions = stack.getOrDefault(ModDataComponents.LODESTONE_POSITIONS.get(), List.of());
        if (positions.size() < 3) {
            validateLodestones(level, positions, player);
            player.displayClientMessage(Component.literal("Недостаточно данных: нужно 3 магнетита.").withStyle(ChatFormatting.RED), true);
            return;
        }

        if (level.dimension() != positions.getFirst().dimension()) {
            player.displayClientMessage(Component.literal("Триангулятор откалиброван на другой мир").withStyle(ChatFormatting.RED), true);
            return;
        }

        if (!validateLodestones(level, positions, player)) {
            return;
        }

        GlobalPos playerPos = GlobalPos.of(level.dimension(), player.blockPosition());
        stack.set(ModDataComponents.TRIANGULATED_POS.get(), playerPos);
    }

    private boolean validateLodestones(Level level, List<GlobalPos> storedPositions, Player player) {
        ItemStack stack = player.getMainHandItem(); // Предполагаем, что предмет в главной руке, или передавайте stack аргументом
        if (!(stack.getItem() instanceof TriangulatorItem)) {
            stack = player.getOffhandItem();
        }

        List<GlobalPos> validPositions = new ArrayList<>(storedPositions);
        boolean changed = false;
        MinecraftServer server = level.getServer();

        if (server == null) return false;

        for (int i = validPositions.size() - 1; i >= 0; i--) {
            GlobalPos gp = validPositions.get(i);

            ServerLevel targetLevel = server.getLevel(gp.dimension());

            if (targetLevel == null || !targetLevel.getBlockState(gp.pos()).is(Blocks.LODESTONE)) {
                validPositions.remove(i);
                changed = true;
                player.displayClientMessage(Component.literal("Сигнал потерян: Один из магнетитов разрушен или недоступен!").withStyle(ChatFormatting.RED), true);
            }
        }

        if (changed) {
            stack.set(ModDataComponents.LODESTONE_POSITIONS.get(), validPositions);
            stack.set(ModDataComponents.LEDS_COUNT.get(), validPositions.size());
            updateItemModelFromLeds(stack, "triangulator");
            return false;
        }

        if (validPositions.size() < 3) {
            return false;
        }

        GlobalPos p1 = validPositions.get(0);
        GlobalPos p2 = validPositions.get(1);
        GlobalPos p3 = validPositions.get(2);

        if (p1.dimension() != p2.dimension() || p2.dimension() != p3.dimension()) {
            player.displayClientMessage(Component.literal("Ошибка: Магнетиты находятся в разных измерениях!").withStyle(ChatFormatting.RED), true);
            return false;
        }

        double d1 = p1.pos().distSqr(p2.pos());
        double d2 = p2.pos().distSqr(p3.pos());
        double d3 = p3.pos().distSqr(p1.pos());

        double minSq = Config.MIN_DISTANCE_BETWEEN_MAGNETIT.get() * Config.MIN_DISTANCE_BETWEEN_MAGNETIT.get();

        if (d1 < minSq || d2 < minSq || d3 < minSq) {
            player.displayClientMessage(Component.literal("Ошибка интерференции: Магнетиты слишком близко друг к другу (<" + Config.MIN_DISTANCE_BETWEEN_MAGNETIT.get() + " блоков)!").withStyle(ChatFormatting.GOLD), true);
            return false;
        }

        return true;
    }
}
