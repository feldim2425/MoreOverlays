package at.feldim2425.moreoverlays.chunkbounds;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ChunkBoundsHandler {

    public static byte mode = 0;

    public static void init(){
        MinecraftForge.EVENT_BUS.register(new ChunkBoundsHandler());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(Minecraft.getMinecraft().thePlayer==null)
            return;
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if (mode!=0)
            ChunkBoundsRenderer.renderOverlays();
    }

    public static void toggleMode(){
        mode++;
        if(mode>2)
            mode=0;
    }
}
