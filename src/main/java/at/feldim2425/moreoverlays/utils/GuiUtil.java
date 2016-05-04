package at.feldim2425.moreoverlays.utils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiUtil {

    public static void drawHoverText(List<String> text, int x, int y, int width, int height, FontRenderer font){
        if(!text.isEmpty()){
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            int textWidth = -1;
            for(int i=0;i<text.size();i+=1){
                if(font.getStringWidth(text.get(i)) > textWidth)
                    textWidth = font.getStringWidth(text.get(i));
            }

            int posX = x + 12;
            int posY = y - 12;
            int textHeight = 8;

            if (text.size() > 1) {
                textHeight += 2 + (text.size() - 1) * 10;
            }
            if (posX + textWidth > width) {
                posX -= 28 + textWidth;
            }
            if (posY + textHeight + 6 > height) {
                posY = height - textHeight - 6;
            }

            int zLevel = 300;
            int bg = 0xF0100010;
            drawRect(posX - 3, posY - 4, posX + textWidth + 3, posY - 3, zLevel,bg);
            drawRect(posX - 3, posY + textHeight + 3, posX + textWidth + 3, posY + textHeight + 4, zLevel,bg);
            drawRect(posX - 3, posY - 3, posX + textWidth + 3, posY + textHeight + 3, zLevel,bg);
            drawRect(posX - 4, posY - 3, posX - 3, posY + textHeight + 3, zLevel,bg);
            drawRect(posX + textWidth + 3, posY - 3, posX + textWidth + 4, posY + textHeight + 3, zLevel,bg);
            int color = 0x505000FF;
            drawRect(posX - 3, posY - 3 + 1, posX - 3 + 1, posY + textHeight + 3 - 1, zLevel,color);
            drawRect(posX + textWidth + 2, posY - 3 + 1, posX + textWidth + 3, posY + textHeight + 3 - 1,zLevel, color);
            drawRect(posX - 3, posY - 3, posX + textWidth + 3, posY - 3 + 1, zLevel, color);
            drawRect(posX - 3, posY + textHeight + 2, posX + textWidth + 3, posY + textHeight + 3,zLevel, color);

            for(int i=0;i<text.size();i+=1){
                font.drawStringWithShadow(text.get(i), posX, posY, -1);
                if (i == 0) {
                    posY += 2;
                }
                posY += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static void drawRect(int x1, int y1, int x2, int y2, int z, int color){
        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        renderer.pos(x1, y2, z).endVertex();
        renderer.pos(x2, y2, z).endVertex();
        renderer.pos(x2, y1, z).endVertex();
        renderer.pos(x1, y1, z).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
