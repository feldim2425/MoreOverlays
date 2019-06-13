package at.feldim2425.moreoverlays.gui;

import java.util.ArrayList;
import java.util.List;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class OverlayRenderEventHandler {
	private final List<String> regionInfo = new ArrayList<String>();
	private int playerPrevRegionPosX = Integer.MIN_VALUE;
	private int playerPrevRegionPosY = Integer.MIN_VALUE;
	private int playerPrevRegionPosZ = Integer.MIN_VALUE;
	private final boolean isCubicChunksLoaded;

	public OverlayRenderEventHandler(boolean modLoaded) {
		isCubicChunksLoaded = modLoaded;
	}

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new OverlayRenderEventHandler(Loader.isModLoaded("cubicchunks")));
	}

	@SubscribeEvent
	public void onOverlayRender(RenderGameOverlayEvent.Post action) {
		if (regionInfo.isEmpty())
			return;
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.gameSettings.showDebugInfo)
			return;
		int y = 0;
		for (String text : regionInfo)
			mc.fontRenderer.drawStringWithShadow(text, 10, y += 10, 0xFFFFFF);
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || Minecraft.getMinecraft().player == null)
			return;
		if (ChunkBoundsHandler.getMode() != ChunkBoundsHandler.RenderMode.REGIONS) {
			regionInfo.clear();
			return;
		}
		boolean updateInfo = false;
		if (playerPrevRegionPosX != Minecraft.getMinecraft().player.chunkCoordX >> 4) {
			playerPrevRegionPosX = Minecraft.getMinecraft().player.chunkCoordX >> 4;
			updateInfo = true;
		}
		if (playerPrevRegionPosY != Minecraft.getMinecraft().player.chunkCoordY >> 4) {
			playerPrevRegionPosY = Minecraft.getMinecraft().player.chunkCoordY >> 4;
			updateInfo = true;
		}
		if (playerPrevRegionPosZ != Minecraft.getMinecraft().player.chunkCoordZ >> 4) {
			playerPrevRegionPosZ = Minecraft.getMinecraft().player.chunkCoordZ >> 4;
			updateInfo = true;
		}
		if (updateInfo) {
			regionInfo.clear();
			if (isCubicChunksLoaded) {
				regionInfo.add(String.format("region2d/%d.%d.2dr", playerPrevRegionPosX, playerPrevRegionPosZ));
				regionInfo.add(String.format("region3d/%d.%d.%d.3dr", playerPrevRegionPosX, playerPrevRegionPosY,
						playerPrevRegionPosZ));
			} else {
				regionInfo.add(String.format("region/r.%d.%d.mca", playerPrevRegionPosX, playerPrevRegionPosZ));
			}
		}
	}
}
