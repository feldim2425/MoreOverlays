package at.feldim2425.moreoverlays.lightoverlay.integration;

import at.feldim2425.moreoverlays.api.lightoverlay.LightOverlayReloadHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AlternateLightHandler {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new AlternateLightHandler());
	}

	@SubscribeEvent
	public void onLightOverlayEnable(LightOverlayReloadHandlerEvent event){
		if(event.isIgnoringSpawner()){
			return;
		}

		/*
		if(ModList.get().isLoaded("customspawner")){
			event.setScanner(CustomSpawnerLightScanner.class);
		}
		*/
	}

}
