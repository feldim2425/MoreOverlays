package at.feldim2425.moreoverlays.chunkbounds;

import at.feldim2425.moreoverlays.lightoverlay.LightOverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ChunkBoundsHandler {

    public static boolean enabled = false;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ChunkBoundsHandler());
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if (enabled)
            ChunkBoundsRenderer.renderOverlays();
    }


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null)
            return;
    }

    public static void toggleMode() {
        enabled = !enabled;
        if(!enabled)
            LightOverlayRenderer.clearCache();
    }
}
