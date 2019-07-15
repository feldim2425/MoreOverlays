package at.feldim2425.moreoverlays;

import at.feldim2425.moreoverlays.api.itemsearch.SlotHandler;
import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import at.feldim2425.moreoverlays.gui.OverlayRenderEventHandler;
import at.feldim2425.moreoverlays.itemsearch.GuiHandler;
import at.feldim2425.moreoverlays.itemsearch.integration.MantleGuiModuleOverride;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;
import at.feldim2425.moreoverlays.lightoverlay.integration.AlternateLightHandler;
import net.minecraftforge.fml.common.Loader;

public class Proxy {

	private static boolean enable_jei = false;

	public static boolean isJeiInstalled() {
		return enable_jei;
	}

	public void preInit() {
		enable_jei = Loader.isModLoaded("jei");

		KeyBindings.init();

		LightOverlayHandler.init();
		ChunkBoundsHandler.init();
		GuiHandler.init();
		OverlayRenderEventHandler.init();
		AlternateLightHandler.init();
	}

	public void init() {

	}

	public void postInit() {
		if (enable_jei && Loader.isModLoaded("mantle")) {
			SlotHandler.INSTANCE.addPositionOverride(new MantleGuiModuleOverride());
		}
	}
}
