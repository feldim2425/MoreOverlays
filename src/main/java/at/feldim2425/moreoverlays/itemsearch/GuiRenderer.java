package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.KeyBindings;
import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.Proxy;
import at.feldim2425.moreoverlays.api.itemsearch.IViewSlot;
import at.feldim2425.moreoverlays.api.itemsearch.SlotHandler;
import at.feldim2425.moreoverlays.config.Config;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mezz.jei.api.JEIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.Map;

public class GuiRenderer {

    public static final GuiRenderer INSTANCE = new GuiRenderer();

    private static final float OVERLAY_ZLEVEL = 299F;
    private static final int TEXT_FADEOUT = 20;
    private static final int FRAME_RADIUS = 1;

    private static boolean enabled = false;

    private static String lastFilterText = "";
    private static boolean emptyFilter = true;
    private static BiMap<Integer, IViewSlot> views = HashBiMap.create();
    //private static String text = I18n.translateToLocal("gui." + MoreOverlays.MOD_ID + ".search.disabled");
    private static int highlightTicks = 0;

    //private int txtPosY = 0;
    private boolean isCreative = false;
    private boolean allowRender = false;
    private int guiOffsetX = 0;
    private int guiOffsetY = 0;

    public void guiInit(GuiScreen gui) {
        if (!canShowIn(gui))
            return;
        highlightTicks = 0;
        //txtPosY = gui.height  - 19 + (16- Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT)/2;
        try {
            Field left = ReflectionHelper.findField(GuiContainer.class, "field_147003_i", "guiLeft"); //Obfuscated -> guiLeft
            left.setAccessible(true);
            guiOffsetX = left.getInt(gui);

            Field top = ReflectionHelper.findField(GuiContainer.class, "field_147009_r", "guiTop"); //Obfuscated -> guiTop
            top.setAccessible(true);
            guiOffsetY = top.getInt(gui);
        } catch (Exception e) {
            MoreOverlays.logger.error("Something went wrong. Tried to load gui coords with java reflection. Gui class: "+gui.getClass().getName());
            e.printStackTrace();
        }
    }

    public void guiOpen(GuiScreen gui) {
        isCreative = (gui instanceof GuiContainerCreative);
        /*text = I18n.translateToLocal("gui." + MoreOverlays.MOD_ID + ".search."+( enabled ? "enabled" : "disabled"));
        if(enabled && Config.itemsearch_ShowItemSearchKey)
            text += " - [" + KeyBindings.invSearch.getKeyModifier().getLocalizedComboName(KeyBindings.invSearch.getKeyCode()) + "]";*/
    }

    public void preDraw() {
        GuiScreen guiscr = Minecraft.getMinecraft().currentScreen;

        GuiTextField textField = JeiModule.getJEITextField();

        if(canShowIn(guiscr)) {
            allowRender = true;
            if(textField!=null && enabled) {
                drawSearchFrame(textField);
            }
        }
    }

    public void postDraw() {
        GuiScreen guiscr = Minecraft.getMinecraft().currentScreen;

        if(allowRender && canShowIn(guiscr))
        {
            allowRender = false;
            drawSlotOverlay((GuiContainer) guiscr);
        }
    }

    private void drawSearchFrame(GuiTextField textField)
    {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.pushMatrix();
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();
        GlStateManager.color(1, 1, 0, 1);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(textField.xPosition + textField.width + FRAME_RADIUS, textField.yPosition - FRAME_RADIUS, 1000).endVertex();
        buffer.pos(textField.xPosition - FRAME_RADIUS,textField.yPosition - FRAME_RADIUS, 1000).endVertex();
        buffer.pos(textField.xPosition - FRAME_RADIUS, textField.yPosition, 1000).endVertex();
        buffer.pos(textField.xPosition + textField.width + FRAME_RADIUS, textField.yPosition, 1000).endVertex();

        buffer.pos(textField.xPosition, textField.yPosition, 1000).endVertex();
        buffer.pos(textField.xPosition - FRAME_RADIUS,textField.yPosition, 1000).endVertex();
        buffer.pos(textField.xPosition - FRAME_RADIUS, textField.yPosition+textField.height, 1000).endVertex();
        buffer.pos(textField.xPosition, textField.yPosition+textField.height, 1000).endVertex();

        buffer.pos(textField.xPosition + textField.width + FRAME_RADIUS, textField.yPosition +textField.height, 1000).endVertex();
        buffer.pos(textField.xPosition - FRAME_RADIUS,textField.yPosition +textField.height, 1000).endVertex();
        buffer.pos(textField.xPosition - FRAME_RADIUS, textField.yPosition +textField.height + FRAME_RADIUS, 1000).endVertex();
        buffer.pos(textField.xPosition + textField.width + FRAME_RADIUS, textField.yPosition +textField.height + FRAME_RADIUS, 1000).endVertex();

        buffer.pos(textField.xPosition + textField.width+ FRAME_RADIUS, textField.yPosition, 1000).endVertex();
        buffer.pos(textField.xPosition  + textField.width ,textField.yPosition, 1000).endVertex();
        buffer.pos(textField.xPosition  + textField.width, textField.yPosition+textField.height, 1000).endVertex();
        buffer.pos(textField.xPosition + textField.width + FRAME_RADIUS, textField.yPosition+textField.height, 1000).endVertex();

        tess.draw();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
    }

    public void renderTooltip(ItemStack stack) {
        GuiScreen guiscr = Minecraft.getMinecraft().currentScreen;
        if(allowRender && canShowIn(guiscr)) {
            GuiContainer gui = (GuiContainer) guiscr;
            if(gui.getSlotUnderMouse()!=null && gui.getSlotUnderMouse().getHasStack() && gui.getSlotUnderMouse().getStack().equals(stack)) {
                allowRender = false;
                drawSlotOverlay((GuiContainer) guiscr);
            }
        }
    }

    private void drawSlotOverlay(GuiContainer gui)
    {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.color(1,1,1,1);

        /*if(highlightTicks>0 || !Config.itemsearch_FadeoutText || (Config.itemsearch_ShowItemSearchKey && enabled)) {
            int alpha = 255;
            if(Config.itemsearch_FadeoutText && !(Config.itemsearch_ShowItemSearchKey && enabled)) {
                alpha = (int) (((float) highlightTicks / (float) TEXT_FADEOUT) * 256);
                alpha = Math.max(0, Math.min(255, alpha));
            }
            int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
            int color = 0x00ffffff | (alpha << 24);

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            Minecraft.getMinecraft().fontRenderer.drawString(text, (gui.width - width) / 2, txtPosY, color);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

        }*/


        if (!enabled || isCreative || views == null || views.isEmpty())
            return;

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer renderer = tess.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0, 0, 0, 0.5F);

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for (Map.Entry<Integer, IViewSlot> slot : views.entrySet()) {
            int px = slot.getValue().getRenderPosX(guiOffsetX, guiOffsetY);
            int py = slot.getValue().getRenderPosY(guiOffsetX, guiOffsetY);
            renderer.pos(px + 16 + guiOffsetX, py + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
            renderer.pos(px + guiOffsetX, py + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
            renderer.pos(px + guiOffsetX, py + 16 + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
            renderer.pos(px + 16 + guiOffsetX, py + 16 + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
        }

        tess.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.disableBlend();
    }

    public boolean canShowIn(GuiScreen gui){
        return (gui instanceof GuiContainer) && !isCreative && ((GuiContainer) gui).inventorySlots!=null && !((GuiContainer) gui).inventorySlots.inventorySlots.isEmpty();
    }

    private void checkSlots(GuiContainer container) {
        if (views == null)
            views = HashBiMap.create();
        else
            views.clear();
        for (Slot slot : container.inventorySlots.inventorySlots) {
            IViewSlot slotv = SlotHandler.INSTANCE.getViewSlot(container, slot);
            if(!slotv.canSearch() || isSearchedItem(slot.getStack()))
                continue;
            views.forcePut(slot.slotNumber, slotv);
        }
    }

    private boolean isSearchedItem(ItemStack stack) {
        if(emptyFilter) return true;
        else if(stack.isEmpty()) return false;
        ItemStack stack1;
        for (Object ingredient : JeiModule.filter.getFilteredIngredients()) {
            if(ingredient instanceof ItemStack)
                stack1 = (ItemStack) ingredient;
            else
                continue;

            if (stack1.isItemEqual(stack) || (stack1.getItem() == stack.getItem() && stack1.getItem().isDamageable()))
                return true;
        }
        return false;
    }

    public void tick() {
        if (!canShowIn(Minecraft.getMinecraft().currentScreen))
            return;
        if (enabled && !JeiModule.filter.getFilterText().equals(lastFilterText)) {
            lastFilterText = JeiModule.filter.getFilterText();
            emptyFilter = lastFilterText.replace(" ","").isEmpty();
        }

        if (enabled && Minecraft.getMinecraft().currentScreen instanceof GuiContainer)
            checkSlots((GuiContainer) Minecraft.getMinecraft().currentScreen);
        else if(views!=null)
            views.clear();

        if(highlightTicks>0)
            highlightTicks--;
    }

    public void toggleMode() {
        enabled = !enabled;
        if (enabled) {
            lastFilterText = JeiModule.filter.getFilterText();
            emptyFilter = lastFilterText.replace(" ","").isEmpty();
            /*text = I18n.translateToLocal("gui." + MoreOverlays.MOD_ID + ".search.enabled");
            if(Config.itemsearch_ShowItemSearchKey)
                text += " - [" + KeyBindings.invSearch.getKeyModifier().getLocalizedComboName(KeyBindings.invSearch.getKeyCode()) + "]";*/
        } else {
            lastFilterText = "";
            //text = I18n.translateToLocal("gui." + MoreOverlays.MOD_ID + ".search.disabled");
        }
        highlightTicks=TEXT_FADEOUT;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
