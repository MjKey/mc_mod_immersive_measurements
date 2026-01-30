package ru.alextrof94.immersive_measurements;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.alextrof94.immersive_measurements.items.*;

import static ru.alextrof94.immersive_measurements.ImmersiveMeasurements.MODID;

public class ModItems {

        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

        public static final DeferredItem<Item> DEPTH_METER = ITEMS.registerItem(
                        "depth_meter",
                        properties -> new DepthMeterItem(ModBlocks.DEPTH_METER.get(), properties),
                        new Item.Properties());
        public static final DeferredItem<Item> DIGITAL_CLOCK = ITEMS.registerItem(
                        "digital_clock",
                        properties -> new DigitalClockItem(ModBlocks.DIGITAL_CLOCK.get(), properties),
                        new Item.Properties());
        public static final DeferredItem<Item> TRIANGULATOR = ITEMS.registerItem(
                        "triangulator",
                        properties -> new TriangulatorItem(ModBlocks.TRIANGULATOR.get(), properties),
                        new Item.Properties());
        public static final DeferredItem<Item> GPS = ITEMS.registerItem(
                        "gps",
                        GpsItem::new,
                        new Item.Properties());
        public static final DeferredItem<Item> SPEEDOMETER = ITEMS.registerItem(
                "speedometer",
                SpeedometerItem::new,
                new Item.Properties());
        public static final DeferredItem<Item> RADAR = ITEMS.registerItem(
                "radar",
                RadarItem::new,
                new Item.Properties());


        public static final DeferredItem<Item> BASE_CASE = ITEMS.registerSimpleItem(
                        "base_case",
                        new Item.Properties());
        public static final DeferredItem<Item> BASE_CIRCUIT_BOARD = ITEMS.registerSimpleItem(
                        "base_circuit_board",
                        new Item.Properties());
        public static final DeferredItem<Item> BASE_DISPLAY = ITEMS.registerSimpleItem(
                        "base_display",
                        new Item.Properties());
        public static final DeferredItem<Item> ADVANCED_PROCESSOR = ITEMS.registerSimpleItem(
                        "advanced_processor",
                        new Item.Properties());
        public static final DeferredItem<Item> SENSOR_BAROMETER = ITEMS.registerSimpleItem(
                        "sensor_barometer",
                        new Item.Properties());
        public static final DeferredItem<Item> SENSOR_FLYWHEEL = ITEMS.registerSimpleItem(
                        "sensor_flywheel",
                        new Item.Properties());
        public static final DeferredItem<Item> SENSOR_LODESTONE_RESONATOR = ITEMS.registerSimpleItem(
                        "sensor_lodestone_resonator",
                        new Item.Properties());
        public static final DeferredItem<Item> SENSOR_QUARTZ_OSCILLATOR = ITEMS.registerSimpleItem(
                "sensor_quartz_oscillator",
                new Item.Properties());
        public static final DeferredItem<Item> SENSOR_LIFEFORMS_ANALYSER = ITEMS.registerSimpleItem(
                "sensor_lifeforms_analyzer",
                new Item.Properties());

        public static void register(IEventBus eventBus) {
                ITEMS.register(eventBus);
        }

        public static void updateItemModelFromLeds(ItemStack stack, String baseName) {
                int n = stack.getOrDefault(ModDataComponents.LEDS_COUNT.get(), 0);

                if (n < 0)
                        n = 0;
                if (n > 3)
                        n = 3;

                ResourceLocation model = ResourceLocation.fromNamespaceAndPath(
                                MODID,
                                baseName + "_" + n);

                stack.set(DataComponents.ITEM_MODEL, model);
        }

        // SCANNER сканирует блок, выдавая о нём информацию.

        // баги
        // Speedometer нен работает у игроков-клиентов.
}
