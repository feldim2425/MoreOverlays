package at.feldim2425.moreoverlays;

import at.feldim2425.moreoverlays.api.itemsearch.SlotHandler;
import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import at.feldim2425.moreoverlays.itemsearch.GuiHandler;
import at.feldim2425.moreoverlays.itemsearch.GuiUtils;
import at.feldim2425.moreoverlays.itemsearch.integration.MantleModuleScreenOverride;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;
import at.feldim2425.moreoverlays.lightoverlay.integration.AlternateLightHandler;
import net.minecraftforge.fml.ModList;

public final class ClientRegistrationHandler {

	private static boolean enable_jei = false;

	public static boolean isJeiInstalled() {
		return enable_jei;
	}

	public static void setupClient() {
		enable_jei = ModList.get().isLoaded("jei");
		KeyBindings.init();

		LightOverlayHandler.init();
		ChunkBoundsHandler.init();
		GuiUtils.initUtil();
		AlternateLightHandler.init();

		GuiHandler.init();

		if (enable_jei && ModList.get().isLoaded("mantle")) {
			SlotHandler.INSTANCE.addPositionOverride(new MantleModuleScreenOverride());
		}
	}

	private ClientRegistrationHandler() {
		// EMPTY
	}
}
