package at.feldim2425.moreoverlays.chunkbounds;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkBoundsHandler {

	private static RenderMode mode = RenderMode.NONE;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ChunkBoundsHandler());
	}

	public static RenderMode getMode() {
		return mode;
	}

	public static void setMode(RenderMode mode) {
		ChunkBoundsHandler.mode = mode;
	}

	public static void toggleMode() {
		RenderMode[] modes = RenderMode.values();
		mode = modes[(mode.ordinal() + 1) % modes.length];
	}

	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent event) {
		if (mode != RenderMode.NONE)
			ChunkBoundsRenderer.renderOverlays();
	}

	public enum RenderMode {
		NONE,
		CORNERS,
		GRID,
		REGIONS
	}
}
