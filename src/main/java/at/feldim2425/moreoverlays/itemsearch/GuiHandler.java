package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.Proxy;
import at.feldim2425.moreoverlays.config.Config;
import at.feldim2425.moreoverlays.utils.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GuiHandler {

    private static final float OVERLAY_ZLEVEL = 299F;
    private static final int TEXT_FADEOUT = 20;

    public static List<ItemStack> itemCache = null;
    private static String lastFilterText = "";
    private static boolean emptyFilter = true;
    private static boolean enabled = false;

    private static List<String> tooltip = new ArrayList<>();
    private static List<Integer> slotindexCache = null;
    private static int txtPosY = 0;
    private static boolean isCreative = false;
    private static String text = StatCollector.translateToLocal("gui."+ MoreOverlays.MOD_ID+".search.disabled");
    private static int guiOffsetX = 0;
    private static int guiOffsetY = 0;
    private static long highlightTicks = 0;


    public static void init(){
        if(Proxy.isJeiInstalled())
            MinecraftForge.EVENT_BUS.register(new GuiHandler());
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!canShowIn(event.gui))
            return;
        txtPosY = event.gui.height  - 19 + (16-Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT)/2;
        GuiContainer gui = (GuiContainer) event.gui;
        try {
            Field left = gui.getClass().getField("field_147003_i"); //Obfuscated -> guiLeft
            left.setAccessible(true);
            guiOffsetX = left.getInt(gui);

            Field top = gui.getClass().getField("field_147009_r"); //Obfuscated -> guiTop
            top.setAccessible(true);
            guiOffsetY = top.getInt(gui);
        } catch (IllegalAccessException e) {
            MoreOverlays.logger.error("Something went wrong. Tried to load gui coords with java reflection. Gui class: "+gui.getClass().getName());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {

            try{
                Field left = gui.getClass().getField("guiLeft");
                left.setAccessible(true);
                guiOffsetX = left.getInt(gui);

                Field top = gui.getClass().getField("guiTop");
                top.setAccessible(true);
                guiOffsetY = top.getInt(gui);
            } catch (IllegalAccessException | NoSuchFieldException e1) {
                MoreOverlays.logger.error("Something went wrong. Tried to load gui coords with java reflection. Gui class: "+gui.getClass().getName());
                e1.printStackTrace();
            }

        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        isCreative = (event.gui instanceof GuiContainerCreative);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip(ItemTooltipEvent event) {
        if(enabled && !slotindexCache.isEmpty()){ //Not the best way but it works
            tooltip.clear();
            tooltip.addAll(event.toolTip);
            event.toolTip.clear();
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!canShowIn(event.gui))
            return;

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.color(1,1,1,1);

        if((enabled || Config.itemsearch_DisableText) && (highlightTicks>0 || !Config.itemsearch_FadeoutText)) {
            int alpha = 255;
            if(Config.itemsearch_FadeoutText) {
                alpha = (int) (((float) highlightTicks / (float) TEXT_FADEOUT) * 256);
                alpha = Math.max(0, Math.min(255, alpha));
            }
            int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
            int color = 0x00ffffff | (alpha << 24);

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            Minecraft.getMinecraft().fontRendererObj.drawString(text, (event.gui.width - width) / 2, txtPosY, color);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        if(!enabled || isCreative || slotindexCache==null || slotindexCache.isEmpty())
            return;
        GuiContainer gui = (GuiContainer) event.gui;

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer renderer = tess.getWorldRenderer();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0, 0, 0, 0.5F);

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for(Slot slot : gui.inventorySlots.inventorySlots){
            if(slotindexCache.contains(slot.slotNumber)) {
                int px = slot.xDisplayPosition;
                int py = slot.yDisplayPosition;
                renderer.pos(px + 16 + guiOffsetX, py + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
                renderer.pos(px + guiOffsetX, py + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
                renderer.pos(px + guiOffsetX, py + 16 + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
                renderer.pos(px + 16 + guiOffsetX, py + 16 + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
            }
        }

        tess.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if(!tooltip.isEmpty()) {
            GuiUtil.drawHoverText(tooltip, event.mouseX, event.mouseY, event.gui.width, event.gui.height, Minecraft.getMinecraft().fontRendererObj);
            tooltip.clear();
        }

        GlStateManager.disableBlend();
    }

    private static boolean canShowIn(GuiScreen gui){
        return (gui instanceof GuiContainer) && !isCreative && ((GuiContainer) gui).inventorySlots!=null && !((GuiContainer) gui).inventorySlots.inventorySlots.isEmpty();
    }
    private static void checkSlots(Container container){
        if(slotindexCache==null)
            slotindexCache = new ArrayList<>();
        else
            slotindexCache.clear();
        for(Slot slot : container.inventorySlots){
            if(slot.xDisplayPosition<0 || slot.yDisplayPosition<0) //Don't care about slots that are not shown
                continue;
            if(!isSearchedItem(slot.getStack()))
                slotindexCache.add(slot.slotNumber);
        }
    }

    private static boolean isSearchedItem(ItemStack stack){
        if (stack == null) return emptyFilter;
        for(ItemStack stack1 : itemCache){
            if (stack1.isItemEqual(stack) || (stack1.getItem() == stack.getItem() && stack1.getItem().isDamageable()))
                return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getMinecraft().thePlayer == null || !canShowIn(Minecraft.getMinecraft().currentScreen))
            return;
        if (enabled && !JeiModule.filter.getFilterText().equals(lastFilterText)) {
            lastFilterText = JeiModule.filter.getFilterText();
            if (itemCache != null)
                itemCache.clear();
            else
                itemCache = new ArrayList<>();
            JeiModule.filter.getItemList().forEach((itemElement) -> itemCache.add(itemElement.getItemStack()));
            emptyFilter = lastFilterText.replace(" ","").isEmpty();
        }

        if (enabled && Minecraft.getMinecraft().thePlayer.openContainer != null)
            checkSlots(Minecraft.getMinecraft().thePlayer.openContainer);

        if(highlightTicks>0)
            highlightTicks--;
    }

    public static void toggleMode(){
        enabled=!enabled;
        if(enabled){
            lastFilterText = JeiModule.filter.getFilterText();
            if(itemCache!=null)
                itemCache.clear();
            else
                itemCache = new ArrayList<>();
            JeiModule.filter.getItemList().forEach((itemElement) -> itemCache.add(itemElement.getItemStack()));
            emptyFilter = lastFilterText.replace(" ","").isEmpty();
            text = StatCollector.translateToLocal("gui."+ MoreOverlays.MOD_ID+".search.enabled");
        }
        else {
            lastFilterText = "";
            if(itemCache!=null)
                itemCache.clear();
            text = StatCollector.translateToLocal("gui."+ MoreOverlays.MOD_ID+".search.disabled");
        }

        highlightTicks = TEXT_FADEOUT;
    }
}
