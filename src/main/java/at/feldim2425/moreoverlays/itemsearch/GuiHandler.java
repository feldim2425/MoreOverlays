package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GuiHandler {

    private static final float OVERLAY_ZLEVEL = 299F;

    public static List<ItemStack> itemCache = null;
    private static String lastFilterText = "";
    private static boolean enabled = false;

    private static List<Integer> slotindexCache = null;
    private static int txtPosY = 0;
    private static boolean isCreative = false;
    private static String text = I18n.translateToLocal("gui." + MoreOverlays.MOD_ID + ".search.disabled");
    private static int guiOffsetX = 0;
    private static int guiOffsetY = 0;


    public static void init() {
        if (Proxy.isJeiInstalled())
            MinecraftForge.EVENT_BUS.register(new GuiHandler());
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiContainer) || isCreative)
            return;
        txtPosY = event.getGui().height - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT - 3;
        GuiContainer gui = (GuiContainer) event.getGui();

        try {
            Field left = gui.getClass().getField("guiLeft");
            left.setAccessible(true);
            guiOffsetX = left.getInt(gui);

            Field top = gui.getClass().getField("guiTop");
            top.setAccessible(true);
            guiOffsetY = top.getInt(gui);
        } catch (Exception e) {
            MoreOverlays.logger.error("Something went wrong. Tried to load gui coords with java reflection");
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        isCreative = (event.getGui() instanceof GuiContainerCreative);
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.getGui() instanceof GuiContainer) || isCreative)
            return;

        int width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, (event.getGui().width - width) / 2, txtPosY, 0xffffff);

        if (!enabled || isCreative || slotindexCache == null || slotindexCache.isEmpty())
            return;
        GuiContainer gui = (GuiContainer) event.getGui();

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer renderer = tess.getBuffer();

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.color(0, 0, 0, 0.5F);

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (slotindexCache.contains(slot.slotNumber)) {
                int px = slot.xDisplayPosition;
                int py = slot.yDisplayPosition;
                renderer.pos(px + 16 + guiOffsetX, py + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
                renderer.pos(px + guiOffsetX, py + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
                renderer.pos(px + guiOffsetX, py + 16 + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
                renderer.pos(px + 16 + guiOffsetX, py + 16 + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
            }
        }

        tess.draw();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void checkSlots(Container container) {
        if (slotindexCache == null)
            slotindexCache = new ArrayList<>();
        else
            slotindexCache.clear();
        for (Slot slot : container.inventorySlots) {
            if (slot.xDisplayPosition < 0 || slot.yDisplayPosition < 0) //Don't care about slots that are not shown
                continue;
            if (!isSearchedItem(slot.getStack()))
                slotindexCache.add(slot.slotNumber);
        }
    }

    private static boolean isSearchedItem(ItemStack stack) {
        if (stack == null) return false;
        for (ItemStack stack1 : itemCache) {
            if (stack1.isItemEqual(stack))
                return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null)
            return;
        if (enabled && !isCreative && JeiModule.filter.getFilterText() != lastFilterText) {
            lastFilterText = JeiModule.filter.getFilterText();
            if (itemCache != null)
                itemCache.clear();
            else
                itemCache = new ArrayList<>();
            JeiModule.filter.getItemList().forEach((itemElement) -> itemCache.add(itemElement.getItemStack()));
        }

        if (enabled && !isCreative && Minecraft.getMinecraft().thePlayer.openContainer != null)
            checkSlots(Minecraft.getMinecraft().thePlayer.openContainer);
    }

    public static void toggleMode() {
        enabled = !enabled;
        if (enabled) {
            lastFilterText = JeiModule.filter.getFilterText();
            if (itemCache != null)
                itemCache.clear();
            else
                itemCache = new ArrayList<>();
            JeiModule.filter.getItemList().forEach((itemElement) -> itemCache.add(itemElement.getItemStack()));
            text = I18n.translateToLocal("gui." + MoreOverlays.MOD_ID + ".search.enabled");
        } else {
            lastFilterText = "";
            if (itemCache != null)
                itemCache.clear();
            text = I18n.translateToLocal("gui." + MoreOverlays.MOD_ID + ".search.disabled");
        }
    }
}
