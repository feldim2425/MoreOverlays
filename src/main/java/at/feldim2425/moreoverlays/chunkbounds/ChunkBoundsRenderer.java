package at.feldim2425.moreoverlays.chunkbounds;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ChunkBoundsRenderer {

	private final static ResourceLocation BLANK_TEX = new ResourceLocation(MoreOverlays.MOD_ID, "textures/blank.png");
	private static RenderManager render = Minecraft.getMinecraft().getRenderManager();

	public static void renderOverlays() {
		EntityPlayer player = Minecraft.getMinecraft().player;

		Minecraft.getMinecraft().renderEngine.bindTexture(BLANK_TEX);
		GlStateManager.pushMatrix();
		//GlStateManager.disableDepth();
		GL11.glLineWidth(Config.render_chunkLineWidth);
		GlStateManager.translate(-render.viewerPosX, -render.viewerPosY, -render.viewerPosZ);

		int h = player.world.getHeight();
		int h0 = (int) player.posY;
		int h1 = Math.min(h, h0 - 16);
		int h2 = Math.min(h, h0 + 16);
		int h3 = Math.min(h1, 0);

		int x0 = player.chunkCoordX * 16;
		int x1 = x0 + 16;
		int x2 = x0 + 8;
		int z0 = player.chunkCoordZ * 16;
		int z1 = z0 + 16;
		int z2 = z0 + 8;

		int regionX = player.chunkCoordX >> 4;
		int regionY = player.chunkCoordY >> 4;
		int regionZ = player.chunkCoordZ >> 4;
		
		int regionBorderX0 = regionX << 8;
		int regionBorderY0 = regionY << 8;
		int regionBorderZ0 = regionZ << 8;
		int regionBorderX1 = ++regionX << 8;
		int regionBorderY1 = ++regionY << 8;
		int regionBorderZ1 = ++regionZ << 8;
		
		int radius = Config.chunk_EdgeRadius * 16;

		GlStateManager.color(((float) ((Config.render_chunkEdgeColor >> 16) & 0xFF)) / 255F, ((float) ((Config.render_chunkEdgeColor >> 8) & 0xFF)) / 255F, ((float) (Config.render_chunkEdgeColor & 0xFF)) / 255F);
		for (int xo = -16 - radius; xo <= radius; xo += 16) {
			for (int yo = -16 - radius; yo <= radius; yo += 16) {
				renderEdge(x0 - xo, z0 - yo, h3, h);
			}
		}

		if (Config.chunk_ShowMiddle) {
			GlStateManager.color(((float) ((Config.render_chunkMiddleColor >> 16) & 0xFF)) / 255F, ((float) ((Config.render_chunkMiddleColor >> 8) & 0xFF)) / 255F, ((float) (Config.render_chunkMiddleColor & 0xFF)) / 255F);
			renderEdge(x2, z2, h3, h);
		}

		if (ChunkBoundsHandler.getMode() == ChunkBoundsHandler.RenderMode.GRID) {
			GlStateManager.color(((float) ((Config.render_chunkGridColor >> 16) & 0xFF)) / 255F, ((float) ((Config.render_chunkGridColor >> 8) & 0xFF)) / 255F, ((float) (Config.render_chunkGridColor & 0xFF)) / 255F);
			renderGrid(x0, h1, z0 - 0.005, x0, h2, z1 + 0.005, 1.0);
			renderGrid(x1, h1, z0 - 0.005, x1, h2, z1 + 0.005, 1.0);
			renderGrid(x0 - 0.005, h1, z0, x1 + 0.005, h2, z0, 1.0);
			renderGrid(x0 - 0.005, h1, z1, x1 + 0.005, h2, z1, 1.0);
		}
		else if(ChunkBoundsHandler.getMode() == ChunkBoundsHandler.RenderMode.REGIONS) {
			GlStateManager.color(((float) ((Config.render_chunkGridColor >> 16) & 0xFF)) / 255F, ((float) ((Config.render_chunkGridColor >> 8) & 0xFF)) / 255F, ((float) (Config.render_chunkGridColor & 0xFF)) / 255F);
			renderGrid(regionBorderX0 - 0.005, regionBorderY0 - 0.005, regionBorderZ0 - 0.005, regionBorderX1 + 0.005,
					regionBorderY1 + 0.005, regionBorderZ1 + 0.005, 16.0);
		}
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	public static void renderEdge(double x, double z, double h3, double h) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder renderer = tess.getBuffer();

		renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

		renderer.pos(x, h3, z).endVertex();
		renderer.pos(x, h, z).endVertex();

		tess.draw();
	}
	
	public static void renderGrid(double x0, double y0, double z0, double x1, double y1, double z1, double step) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder renderer = tess.getBuffer();
		
		renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		for (double x = x0; x <= x1; x+=step) {
			renderer.pos(x, y0, z0).endVertex();
			renderer.pos(x, y1, z0).endVertex();
			renderer.pos(x, y0, z1).endVertex();
			renderer.pos(x, y1, z1).endVertex();
			renderer.pos(x, y0, z0).endVertex();
			renderer.pos(x, y0, z1).endVertex();
			renderer.pos(x, y1, z0).endVertex();
			renderer.pos(x, y1, z1).endVertex();
		}
		for (double y = y0; y <= y1; y+=step) {
			renderer.pos(x0, y, z0).endVertex();
			renderer.pos(x1, y, z0).endVertex();
			renderer.pos(x0, y, z1).endVertex();
			renderer.pos(x1, y, z1).endVertex();
			renderer.pos(x0, y, z0).endVertex();
			renderer.pos(x0, y, z1).endVertex();
			renderer.pos(x1, y, z0).endVertex();
			renderer.pos(x1, y, z1).endVertex();
		}
		for (double z = z0; z <= z1; z+=step) {
			renderer.pos(x0, y0, z).endVertex();
			renderer.pos(x1, y0, z).endVertex();
			renderer.pos(x0, y1, z).endVertex();
			renderer.pos(x1, y1, z).endVertex();
			renderer.pos(x0, y0, z).endVertex();
			renderer.pos(x0, y1, z).endVertex();
			renderer.pos(x1, y0, z).endVertex();
			renderer.pos(x1, y1, z).endVertex();
		}
		tess.draw();
	}
}
