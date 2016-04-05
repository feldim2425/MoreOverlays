package at.feldim2425.moreoverlays.chunkbounds;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    public static void toggleMode() {
        enabled = !enabled;
    }
}
