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
            DepthMeterItem::new,
            new Item.Properties()
    );
    public static final DeferredItem<Item> DIGITAL_CLOCK = ITEMS.registerItem(
            "digital_clock",
            DigitalClockItem::new,
            new Item.Properties()
    );
    public static final DeferredItem<Item> TRIANGULATOR = ITEMS.registerItem(
            "triangulator",
            TriangulatorItem::new,
            new Item.Properties()
    );
    public static final DeferredItem<Item> GPS = ITEMS.registerItem(
            "gps",
            GpsItem::new,
            new Item.Properties()
    );
    public static final DeferredItem<Item> SPEEDOMETER = ITEMS.registerItem(
            "speedometer",
            SpeedometerItem::new,
            new Item.Properties()
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }


    public static void updateItemModelFromLeds(ItemStack stack, String baseName) {
        int n = stack.getOrDefault(ModDataComponents.LEDS_COUNT.get(), 0);

        if (n < 0) n = 0;
        if (n > 3) n = 3;

        ResourceLocation model = ResourceLocation.fromNamespaceAndPath(
                MODID,
                baseName + "_" + n
        );

        stack.set(DataComponents.ITEM_MODEL, model);
    }
    // ГОТОВО Depthmeter - отображает глубину (типа барометр)
    // ГОТОВО DigitalClock - цифровые часы
    // ГОТОВО Triangulator крафтится из 3-х компасов, магнитного камня и прочего, работает за счёт хранения в себе 3-х позиций магнитных камней (которые должны быть на расстоянии 100м друг от друга, если сломать - показания ломаются также), отображает x-z координаты
    // ГОТОВО GPS собирается из триангулятора, глубиномера и цифровых часов, отображает все 3 координаты и время (требования как у триангулятора)

    // SCANNER сканирует блок, выдавая о нём информацию.
}
