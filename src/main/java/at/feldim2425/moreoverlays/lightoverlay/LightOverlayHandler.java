package at.feldim2425.moreoverlays.lightoverlay;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LightOverlayHandler {

    public static boolean enabled = false;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new LightOverlayHandler());
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if (enabled)
            LightOverlayRenderer.renderOverlays();
    }


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (enabled)
            LightOverlayRenderer.refreshCache();
    }

    public static void toggleMode() {
        enabled = !enabled;
        if (!enabled)
            LightOverlayRenderer.clearCache();
    }
}
