package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class GuiHandler {

	private long firstClick = 0;

	public static void init() {
		if (Proxy.isJeiInstalled())
			MinecraftForge.EVENT_BUS.register(new GuiHandler());
	}

	@Deprecated
	public static void toggleMode() {
		GuiRenderer.INSTANCE.toggleMode();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
		JeiModule.updateModule();
		GuiRenderer.INSTANCE.guiInit(event.getGui());
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		GuiRenderer.INSTANCE.guiOpen(event.getGui());
	}

	@SubscribeEvent
	public void onGuiClick(GuiScreenEvent.MouseInputEvent.Pre event) {
		GuiTextField searchField = JeiModule.getJEITextField();
		if (searchField != null && Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && GuiRenderer.INSTANCE.canShowIn(event.getGui())) {
			GuiScreen guiScreen = event.getGui();
			int x = Mouse.getEventX() * guiScreen.width / guiScreen.mc.displayWidth;
			int y = guiScreen.height - Mouse.getEventY() * guiScreen.height / guiScreen.mc.displayHeight - 1;

			if (x > searchField.x && x < searchField.x + searchField.width && y > searchField.y && y < searchField.y + searchField.height) {
				long now = System.currentTimeMillis();
				if (now - firstClick < 1000) {
					GuiRenderer.INSTANCE.toggleMode();
					firstClick = 0;
				}
				else {
					firstClick = now;
				}
			}
		}
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
}
