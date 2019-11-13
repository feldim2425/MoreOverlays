package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.ClientRegistrationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiHandler {

	private long firstClick = 0;

	public static void init() {
		if (ClientRegistrationHandler.isJeiInstalled())
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
	public void onGuiClick(GuiScreenEvent.MouseClickedEvent.Pre event) {
		TextFieldWidget searchField = JeiModule.getJEITextField();
		//Minecraft mc = Minecraft.getInstance();
		if (searchField != null && event.getButton() == 0 && GuiRenderer.INSTANCE.canShowIn(event.getGui())) {
			//Screen guiScreen = event.getGui();
			//int x = event.getMouseX() * guiScreen.width / mc.displayWidth;
			//int y = guiScreen.height - event.getMouseY() * guiScreen.height / mc.displayHeight - 1;
			int x = (int) event.getMouseX();
			int y = (int) event.getMouseY();

			if (x > searchField.x && x < searchField.x + searchField.getWidth() && y > searchField.y && y < searchField.y + searchField.getHeight()) {
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
		if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null)
			return;
		GuiRenderer.INSTANCE.tick();
	}
}
