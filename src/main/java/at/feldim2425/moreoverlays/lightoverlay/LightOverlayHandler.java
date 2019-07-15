package at.feldim2425.moreoverlays.lightoverlay;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.api.lightoverlay.ILightRenderer;
import at.feldim2425.moreoverlays.api.lightoverlay.ILightScanner;
import at.feldim2425.moreoverlays.api.lightoverlay.LightOverlayReloadHandlerEvent;
import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.message.FormattedMessage;

public class LightOverlayHandler {

	private static boolean enabled = false;
	private static ILightRenderer renderer = null;
	private static ILightScanner scanner = null;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new LightOverlayHandler());
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		if(LightOverlayHandler.enabled == enabled){
			return;
		}

		if (enabled) {
			reloadHandlerInternal();
		}
		else {
			scanner.clear();
		}
		LightOverlayHandler.enabled = enabled;
	}

	public static void reloadHandler(){
		if(enabled){
			MoreOverlays.logger.info("Light overlay handlers reloaded");
			reloadHandlerInternal();
		}
	}

	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent event) {
		if (enabled) {
			renderer.renderOverlays(scanner);

		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null && enabled && event.phase == TickEvent.Phase.END &&
				(Minecraft.getMinecraft().currentScreen == null || !Minecraft.getMinecraft().currentScreen.doesGuiPauseGame())) {
			scanner.update(Minecraft.getMinecraft().player);
		}

	}




	private static void reloadHandlerInternal() {
		LightOverlayReloadHandlerEvent event = new LightOverlayReloadHandlerEvent(Config.light_IgnoreSpawn, LightOverlayRenderer.class, LightScannerVanilla.class);
		MinecraftForge.EVENT_BUS.post(event);

		if(renderer == null || renderer.getClass() != event.getRenderer()){
			try {
				renderer = event.getRenderer().newInstance();
			} catch (IllegalAccessException | InstantiationException e) {
				MoreOverlays.logger.warn(new FormattedMessage("Could not create ILightRenderer from type \"%s\"!",event.getRenderer().getName()), e);
				renderer = new LightOverlayRenderer();
			}
		}

		if(scanner == null || scanner.getClass() != event.getScanner()){
			if(scanner != null && enabled){
				scanner.clear();
			}

			try {
				scanner = event.getScanner().newInstance();
			} catch (IllegalAccessException | InstantiationException e) {
				MoreOverlays.logger.warn(new FormattedMessage("Could not create ILightScanner from type \"%s\"!",event.getScanner().getName()), e);
				scanner = new LightScannerVanilla();
			}
		}
	}
}
