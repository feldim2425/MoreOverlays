package at.feldim2425.moreoverlays.lightoverlay;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.api.lightoverlay.ILightRenderer;
import at.feldim2425.moreoverlays.api.lightoverlay.ILightScanner;
import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.mojang.blaze3d.platform.GlStateManager;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class LightOverlayRenderer implements ILightRenderer {

	private final static ResourceLocation BLANK_TEX = new ResourceLocation(MoreOverlays.MOD_ID, "textures/blank.png");
	private static EntityRendererManager render = Minecraft.getInstance().getRenderManager();


	public void renderOverlays(ILightScanner scanner) {
		Minecraft.getInstance().getTextureManager().bindTexture(BLANK_TEX);
		GlStateManager.pushMatrix();
		GL11.glLineWidth(Config.render_spawnLineWidth);

		final Vec3d view = render.info.getProjectedView();
		GlStateManager.translated(-view.x, -view.y, -view.z);

		float ar = ((float) ((Config.render_spawnAColor >> 16) & 0xFF)) / 255F;
		float ag = ((float) ((Config.render_spawnAColor >> 8) & 0xFF)) / 255F;
		float ab = ((float) (Config.render_spawnAColor & 0xFF)) / 255F;

		float nr = ((float) ((Config.render_spawnNColor >> 16) & 0xFF)) / 255F;
		float ng = ((float) ((Config.render_spawnNColor >> 8) & 0xFF)) / 255F;
		float nb = ((float) (Config.render_spawnNColor & 0xFF)) / 255F;


		for (Pair<BlockPos, Byte> entry : scanner.getLightModes()) {
			Byte mode = entry.getValue();
			if (mode == null || mode == 0)
				continue;
			else if (mode == 1)
				renderCross(entry.getKey(), nr, ng, nb);
			else if (mode == 2)
				renderCross(entry.getKey(), ar, ag, ab);
		}


		GlStateManager.popMatrix();
	}

	private static void renderCross(BlockPos pos, float r, float g, float b) {
		double y = pos.getY() + 0.005D;

		double x0 = pos.getX();
		double x1 = x0 + 1;
		double z0 = pos.getZ();
		double z1 = z0 + 1;

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder renderer = tess.getBuffer();

		renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos(x0, y, z0).color(r, g, b, 1).endVertex();
		renderer.pos(x1, y, z1).color(r, g, b, 1).endVertex();

		renderer.pos(x1, y, z0).color(r, g, b, 1).endVertex();
		renderer.pos(x0, y, z1).color(r, g, b, 1).endVertex();
		tess.draw();
	}
}
