package at.feldim2425.moreoverlays.chunkbounds;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChunkBoundsHandler {

    private static RenderMode mode = RenderMode.NONE;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ChunkBoundsHandler());
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if (mode != RenderMode.NONE)
            ChunkBoundsRenderer.renderOverlays();
    }

    public static RenderMode getMode() {
        return mode;
    }

    public static void setMode(RenderMode mode) {
        ChunkBoundsHandler.mode = mode;
    }

    public static void toggleMode(){
        switch (mode){
            case NONE:
                mode = RenderMode.CORNERS;
                break;
            case CORNERS:
                mode = RenderMode.GRID;
                break;
            case GRID:
            default:
                mode = RenderMode.NONE;
                break;
        }
    }

    public enum RenderMode{
        NONE,
        CORNERS,
        GRID
    }
}
