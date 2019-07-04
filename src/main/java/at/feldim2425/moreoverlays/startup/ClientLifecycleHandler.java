package at.feldim2425.moreoverlays.startup;

import at.feldim2425.moreoverlays.KeyBindings;
import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.config.Config;
import at.feldim2425.moreoverlays.config.Configuration;
import at.feldim2425.moreoverlays.events.EventBusHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

public class ClientLifecycleHandler {

	private final Config config;

	public ClientLifecycleHandler() {
		File moreOverlaysConfigurationDir = new File(FMLPaths.CONFIGDIR.get().toFile(), MoreOverlays.MOD_ID);
		if (!moreOverlaysConfigurationDir.exists()) {
			try {
				if (!moreOverlaysConfigurationDir.mkdir()) {
					throw new RuntimeException("Could not create config directory " + moreOverlaysConfigurationDir);
				}
			} catch (SecurityException e) {
				throw new RuntimeException("Could not create config directory " + moreOverlaysConfigurationDir, e);
			}
		}
		config = new Config(moreOverlaysConfigurationDir);

		KeyBindings.init();

		EventBusHelper.addListener(ConfigChangedEvent.OnConfigChangedEvent.class, event -> {
			if (!MoreOverlays.MOD_ID.equals(event.getModID()))
				return;
			Config.loadValues();
		});
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (!event.getModID().equals(MoreOverlays.MOD_ID))
			return;
		Config.loadValues();
	}

}
