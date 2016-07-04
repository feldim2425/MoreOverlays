package at.feldim2425.moreoverlays.chunkbounds;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ChunkBoundsRenderer {

    private final static ResourceLocation BLANK_TEX = new ResourceLocation(MoreOverlays.MOD_ID, "textures/blank.png");
    private static RenderManager render = Minecraft.getMinecraft().getRenderManager();

    public static void renderOverlays() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        GlStateManager.disableAlpha();
        Minecraft.getMinecraft().renderEngine.bindTexture(BLANK_TEX);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GL11.glLineWidth(1.5F);
        GlStateManager.translate(-render.viewerPosX, -render.viewerPosY, -render.viewerPosZ);

        int h = player.worldObj.getHeight();
        int h0 = (int) player.posY;
        int h1 = Math.min(h, Math.max(h0 - 16, 0));
        int h2 = Math.min(h, Math.max(h0 + 16, 0));

        int x0 = player.chunkCoordX * 16;
        int x1 = x0 + 16;
        int x2 = x0 + 8;
        int z0 = player.chunkCoordZ * 16;
        int z1 = z0 + 16;
        int z2 = z0 + 8;

        int radius = Config.chunk_EdgeRadius * 16;

        GlStateManager.color(1, 0, 0);
        for(int xo=-16-radius; xo<=radius; xo+=16){
            for(int yo=-16-radius; yo<=radius; yo+=16){
                renderEdge(x0-xo, z0-yo, h);
            }
        }

        if(Config.chunk_ShowMiddle) {
            GlStateManager.color(1, 1, 0);
            renderEdge(x2, z2, h);
        }

        if(ChunkBoundsHandler.mode==2) {
            GlStateManager.color(0, 1, 0);
            renderHGrid(x0, z0 + 0.005, x1, z0 + 0.005, h1, h2);
            renderVXGrid(x0, x1, z0 + 0.005, h1, h2);

            renderHGrid(x1 - 0.005, z0, x1 - 0.005, z1, h1, h2);
            renderVZGrid(x1 - 0.005, z0, z1, h1, h2);

            renderHGrid(x1, z1 - 0.005, x0, z1 - 0.005, h1, h2);
            renderVXGrid(x0, x1, z1 - 0.005, h1, h2);

            renderHGrid(x0 + 0.005, z1, x0 + 0.005, z0, h1, h2);
            renderVZGrid(x0 + 0.005, z0, z1, h1, h2);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void renderEdge(double x, double z, double h) {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer renderer = tess.getBuffer();

        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        renderer.pos(x, 0, z).endVertex();
        renderer.pos(x, h, z).endVertex();

        tess.draw();
    }

    // Horizontal
    public static void renderHGrid(double x1, double z1, double x2, double z2, double h1, double h2) {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer renderer = tess.getBuffer();

        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        for (double h = h1; h <= h2; h++) {
            renderer.pos(x1, h, z1).endVertex();
            renderer.pos(x2, h, z2).endVertex();
        }

        tess.draw();
    }

    // Vertical Z
    public static void renderVZGrid(double x, double z1, double z2, double h1, double h2) {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer renderer = tess.getBuffer();

        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        for (double z = z1 + 1; z < z2; z++) {
            renderer.pos(x, h1, z).endVertex();
            renderer.pos(x, h2, z).endVertex();
        }
        tess.draw();
    }

    // Vertical X
    public static void renderVXGrid(double x1, double x2, double z, double h1, double h2) {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer renderer = tess.getBuffer();

        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        for (double x = x1 + 1; x < x2; x++) {
            renderer.pos(x, h1, z).endVertex();
            renderer.pos(x, h2, z).endVertex();
        }
        tess.draw();
    }
}
