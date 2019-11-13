package at.feldim2425.moreoverlays.config;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec CLIENT_CONFIG;

	public static final String CONFIG_FILENAME = "MoreOverlays.cfg";
	public static List<String> categories = new ArrayList<>();

	public static void initClientConfig(ModLoadingContext ctx) {
		File configFile = new File(event.getModConfigurationDirectory(), CONFIG_FILENAME);
		config = new Configuration(configFile);

		MinecraftForge.EVENT_BUS.register(new ConfigHandler());
		config.load();
		Config.getCategories(categories);
		Config.loadValues();
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (!event.getModID().equals(MoreOverlays.MOD_ID))
			return;
		Config.loadValues();
		LightOverlayHandler.reloadHandler();
	}

}
