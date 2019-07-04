package at.feldim2425.moreoverlays;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import at.feldim2425.moreoverlays.gui.OverlayRenderEventHandler;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;
import net.minecraftforge.fml.ModList;

public class Proxy {

	private static boolean enable_jei = false;

	public static boolean isJeiInstalled() {
		return enable_jei;
	}

	public void preInit() {
		enable_jei = ModList.get().isLoaded("jei");

		KeyBindings.init();

		LightOverlayHandler.init();
		ChunkBoundsHandler.init();
		OverlayRenderEventHandler.init();
	}

	public void init() {

	}

	public void postInit() {

	}
}
