package at.feldim2425.moreoverlays;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import at.feldim2425.moreoverlays.events.EventBusHelper;
import at.feldim2425.moreoverlays.gui.OverlayRenderEventHandler;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;
import at.feldim2425.moreoverlays.startup.ClientLifecycleHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MoreOverlays.MOD_ID)
public class MoreOverlays {

	public static final String MOD_ID = "moreoverlays";
	public static final String NAME = "MoreOverlays";
	public static final Logger LOGGER = LogManager.getLogger(NAME);

	public MoreOverlays() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		DistExecutor.runWhenOn(Dist.CLIENT, ()->()-> {
			EventBusHelper.addLifecycleListener(modEventBus, FMLClientSetupEvent.class, setupEvent -> {
				ClientLifecycleHandler clientLifecycleHandler = new ClientLifecycleHandler();

				LightOverlayHandler.init();
				ChunkBoundsHandler.init();
				OverlayRenderEventHandler.init();
			});
		});
	}
}
