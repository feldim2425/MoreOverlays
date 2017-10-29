package at.feldim2425.moreoverlays.lightoverlay;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LightOverlayHandler {

    private static boolean enabled = false;

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
        if(Minecraft.getMinecraft().world!=null && enabled && event.phase == TickEvent.Phase.END &&
                (Minecraft.getMinecraft().currentScreen==null || !Minecraft.getMinecraft().currentScreen.doesGuiPauseGame())){
            LightOverlayRenderer.refreshCache();
        }

    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        LightOverlayHandler.enabled = enabled;
    }

    public static void toggleMode() {
        enabled = !enabled;
        if (!enabled)
            LightOverlayRenderer.clearCache();
    }
}
