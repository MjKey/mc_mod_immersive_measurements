package ru.alextrof94.immersive_measurements;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import ru.alextrof94.immersive_measurements.items.*;
import static ru.alextrof94.immersive_measurements.ImmersiveMeasurements.MODID;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import ru.alextrof94.immersive_measurements.client.DepthMeterBlockRenderer;
import ru.alextrof94.immersive_measurements.client.DigitalClockBlockRenderer;
import ru.alextrof94.immersive_measurements.client.TriangulatorBlockRenderer;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods
// in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ImmersiveMeasurementsClient {

    public ImmersiveMeasurementsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DEPTH_METER.get(), DepthMeterBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DIGITAL_CLOCK.get(), DigitalClockBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TRIANGULATOR.get(), TriangulatorBlockRenderer::new);
    }

    @SubscribeEvent
    public static void onLeftClick(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.getMainHandItem().getItem() instanceof IDisplayItem item) {
                item.leftClickAction(mc.level, mc.player);
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }

    @SubscribeEvent
    public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(MODID, "depth_meter_text"),
                DepthMeterRenderer.UNBAKED.type());
        event.register(
                ResourceLocation.fromNamespaceAndPath(MODID, "digital_clock_text"),
                DigitalClockRenderer.UNBAKED.type());
        event.register(
                ResourceLocation.fromNamespaceAndPath(MODID, "triangulator_text"),
                TriangulatorRenderer.UNBAKED.type());
        event.register(
                ResourceLocation.fromNamespaceAndPath(MODID, "gps_text"),
                GpsRenderer.UNBAKED.type());
        event.register(
                ResourceLocation.fromNamespaceAndPath(MODID, "speedometer_text"),
                SpeedometerRenderer.UNBAKED.type());
        event.register(
                ResourceLocation.fromNamespaceAndPath(MODID, "radar_text"),
                RadarRenderer.UNBAKED.type());
    }
}
