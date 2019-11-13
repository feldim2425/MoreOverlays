package at.feldim2425.moreoverlays.chunkbounds;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import com.mojang.blaze3d.platform.GlStateManager;

import org.lwjgl.opengl.GL11;

public class ChunkBoundsRenderer {

	private final static ResourceLocation BLANK_TEX = new ResourceLocation(MoreOverlays.MOD_ID, "textures/blank.png");
	private static EntityRendererManager render = Minecraft.getInstance().getRenderManager();

	public static void renderOverlays() {
		PlayerEntity player = Minecraft.getInstance().player;

		Minecraft.getInstance().getTextureManager().bindTexture(BLANK_TEX);
		GlStateManager.pushMatrix();
		GL11.glLineWidth(Config.render_chunkLineWidth);

		final Vec3d view = render.info.getProjectedView();
		GlStateManager.translated(-view.x, -view.y, -view.z);

		final int h = player.world.getHeight();
		final int h0 = (int) player.posY;
		final int h1 = Math.min(h, h0 - 16);
		final int h2 = Math.min(h, h0 + 16);
		final int h3 = Math.min(h1, 0);

		final int x0 = player.chunkCoordX * 16;
		final int x1 = x0 + 16;
		final int x2 = x0 + 8;
		final int z0 = player.chunkCoordZ * 16;
		final int z1 = z0 + 16;
		final int z2 = z0 + 8;

		int regionX = player.chunkCoordX / ChunkBoundsHandler.REGION_SIZEX;
		int regionY = player.chunkCoordY / ChunkBoundsHandler.REGION_SIZEY_CUBIC;
		int regionZ = player.chunkCoordZ / ChunkBoundsHandler.REGION_SIZEZ;

		if(player.chunkCoordX < 0){
			regionX--;
		}
		if(player.chunkCoordY < 0){
			regionY--;
		}
		if(player.chunkCoordZ < 0){
			regionZ--;
		}
		
		final int regionBorderX0 = regionX * ChunkBoundsHandler.REGION_SIZEX * 16;
		final int regionBorderY0 = regionY * ChunkBoundsHandler.REGION_SIZEY_CUBIC * 16;
		final int regionBorderZ0 = regionZ * ChunkBoundsHandler.REGION_SIZEZ * 16;
		final int regionBorderX1 = regionBorderX0 + (ChunkBoundsHandler.REGION_SIZEX * 16);
		final int regionBorderY1 = regionBorderY0 + (ChunkBoundsHandler.REGION_SIZEY_CUBIC * 16);
		final int regionBorderZ1 = regionBorderZ0 + (ChunkBoundsHandler.REGION_SIZEZ * 16);
		
		final int radius = Config.chunk_EdgeRadius * 16;

		GlStateManager.color3f(((float) ((Config.render_chunkEdgeColor >> 16) & 0xFF)) / 255F, ((float) ((Config.render_chunkEdgeColor >> 8) & 0xFF)) / 255F, ((float) (Config.render_chunkEdgeColor & 0xFF)) / 255F);
		for (int xo = -16 - radius; xo <= radius; xo += 16) {
			for (int yo = -16 - radius; yo <= radius; yo += 16) {
				renderEdge(x0 - xo, z0 - yo, h3, h);
			}
		}

		if (Config.chunk_ShowMiddle) {
			GlStateManager.color3f(((float) ((Config.render_chunkMiddleColor >> 16) & 0xFF)) / 255F, ((float) ((Config.render_chunkMiddleColor >> 8) & 0xFF)) / 255F, ((float) (Config.render_chunkMiddleColor & 0xFF)) / 255F);
			renderEdge(x2, z2, h3, h);
		}

		if (ChunkBoundsHandler.getMode() == ChunkBoundsHandler.RenderMode.GRID) {
			GlStateManager.color3f(((float) ((Config.render_chunkGridColor >> 16) & 0xFF)) / 255F, ((float) ((Config.render_chunkGridColor >> 8) & 0xFF)) / 255F, ((float) (Config.render_chunkGridColor & 0xFF)) / 255F);
			renderGrid(x0, h1, z0 - 0.005, x0, h2, z1 + 0.005, 1.0);
			renderGrid(x1, h1, z0 - 0.005, x1, h2, z1 + 0.005, 1.0);
			renderGrid(x0 - 0.005, h1, z0, x1 + 0.005, h2, z0, 1.0);
			renderGrid(x0 - 0.005, h1, z1, x1 + 0.005, h2, z1, 1.0);
		}
		else if(ChunkBoundsHandler.getMode() == ChunkBoundsHandler.RenderMode.REGIONS) {
			GlStateManager.color3f(((float) ((Config.render_chunkGridColor >> 16) & 0xFF)) / 255F, ((float) ((Config.render_chunkGridColor >> 8) & 0xFF)) / 255F, ((float) (Config.render_chunkGridColor & 0xFF)) / 255F);
			renderGrid(regionBorderX0 - 0.005, regionBorderY0 - 0.005, regionBorderZ0 - 0.005, regionBorderX1 + 0.005,
					regionBorderY1 + 0.005, regionBorderZ1 + 0.005, 16.0);
		}
		GlStateManager.enableDepthTest();
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
