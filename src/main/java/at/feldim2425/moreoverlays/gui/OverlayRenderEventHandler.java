package at.feldim2425.moreoverlays.gui;

import java.util.ArrayList;
import java.util.List;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
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
		MinecraftForge.EVENT_BUS.register(new OverlayRenderEventHandler(ModList.get().isLoaded("cubicchunks")));
	}

	@SubscribeEvent
	public void onOverlayRender(RenderGameOverlayEvent.Post action) {
		if (regionInfo.isEmpty())
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.gameSettings.showDebugInfo)
			return;
		int y = 0;
		for (String text : regionInfo)
			mc.fontRenderer.drawStringWithShadow(text, 10, y += 10, 0xFFFFFF);
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null)
			return;
		if (ChunkBoundsHandler.getMode() != ChunkBoundsHandler.RenderMode.REGIONS) {
			regionInfo.clear();
			return;
		}
		boolean updateInfo = false;
		if (playerPrevRegionPosX != Minecraft.getInstance().player.chunkCoordX >> 4) {
			playerPrevRegionPosX = Minecraft.getInstance().player.chunkCoordX >> 4;
			updateInfo = true;
		}
		if (playerPrevRegionPosY != Minecraft.getInstance().player.chunkCoordY >> 4) {
			playerPrevRegionPosY = Minecraft.getInstance().player.chunkCoordY >> 4;
			updateInfo = true;
		}
		if (playerPrevRegionPosZ != Minecraft.getInstance().player.chunkCoordZ >> 4) {
			playerPrevRegionPosZ = Minecraft.getInstance().player.chunkCoordZ >> 4;
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
