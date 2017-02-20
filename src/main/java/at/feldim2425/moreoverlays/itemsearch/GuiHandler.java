package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.KeyBindings;
import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.Proxy;
import at.feldim2425.moreoverlays.api.itemsearch.IViewSlot;
import at.feldim2425.moreoverlays.api.itemsearch.SlotHandler;
import at.feldim2425.moreoverlays.config.Config;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GuiHandler {

    public static void init() {
        if (Proxy.isJeiInstalled())
            MinecraftForge.EVENT_BUS.register(new GuiHandler());
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiRenderer.INSTANCE.guiInit(event.getGui());
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
       GuiRenderer.INSTANCE.guiOpen(event.getGui());
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
        GuiRenderer.INSTANCE.preDraw();
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        GuiRenderer.INSTANCE.postDraw();
    }

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent.Pre event) {
        GuiRenderer.INSTANCE.renderTooltip(event.getStack());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getMinecraft().player == null)
            return;
        GuiRenderer.INSTANCE.tick();
    }

    @Deprecated
    public static void toggleMode() {
       GuiRenderer.INSTANCE.toggleMode();
    }
}
