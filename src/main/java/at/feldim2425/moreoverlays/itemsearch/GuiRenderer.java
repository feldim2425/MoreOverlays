package at.feldim2425.moreoverlays.itemsearch;

import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.blaze3d.platform.GlStateManager;

import org.lwjgl.opengl.GL11;

import at.feldim2425.moreoverlays.api.itemsearch.SlotHandler;
import at.feldim2425.moreoverlays.api.itemsearch.SlotViewWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec2f;

public class GuiRenderer {

	public static final GuiRenderer INSTANCE = new GuiRenderer();

	private static final float OVERLAY_ZLEVEL = 299F;
	private static final float FRAME_RADIUS = 1.0F;

	private static boolean enabled = false;

	private static String lastFilterText = "";
	private static boolean emptyFilter = true;
	private static BiMap<Slot, SlotViewWrapper> views = HashBiMap.create();

	private boolean allowRender = false;
	private int guiOffsetX = 0;
	private int guiOffsetY = 0;

	public void guiInit(Screen gui) {
		if (!canShowIn(gui)) {
			return;
		}

		guiOffsetX = GuiUtils.getGuiLeft((ContainerScreen<?>) gui);
		guiOffsetY = GuiUtils.getGuiTop((ContainerScreen<?>) gui);

	}

	public void guiOpen(Screen gui) {

	}

	public void preDraw() {
		Screen guiscr = Minecraft.getInstance().currentScreen;

		TextFieldWidget textField = JeiModule.getJEITextField();

		if (canShowIn(guiscr)) {
			allowRender = true;
			if (textField != null && enabled) {
				drawSearchFrame(textField);
			}
		}
	}

	public void postDraw() {
		Screen guiscr = Minecraft.getInstance().currentScreen;

		if (allowRender && canShowIn(guiscr)) {
			allowRender = false;
			drawSlotOverlay((ContainerScreen<?>) guiscr);
		}
	}

	private void drawSearchFrame(TextFieldWidget textField) {
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableDepthTest();
		GlStateManager.disableTexture();
		GlStateManager.color4f(1, 1, 1, 1);
		GlStateManager.pushMatrix();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();
		GlStateManager.color4f(1, 1, 0, 1);

		float x = textField.x + 2;
		float y = textField.y + 2;
		float width = textField.getWidth() - 4;
		float height = textField.getHeight() - 4;

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(x + width + FRAME_RADIUS, y - FRAME_RADIUS, 1000).endVertex();
		buffer.pos(x - FRAME_RADIUS, y - FRAME_RADIUS, 1000).endVertex();
		buffer.pos(x - FRAME_RADIUS, y, 1000).endVertex();
		buffer.pos(x + width + FRAME_RADIUS, y, 1000).endVertex();

		buffer.pos(x, y, 1000).endVertex();
		buffer.pos(x - FRAME_RADIUS, y, 1000).endVertex();
		buffer.pos(x - FRAME_RADIUS, y + height, 1000).endVertex();
		buffer.pos(x, y + height, 1000).endVertex();

		buffer.pos(x + width + FRAME_RADIUS, y + height, 1000).endVertex();
		buffer.pos(x - FRAME_RADIUS, y + height, 1000).endVertex();
		buffer.pos(x - FRAME_RADIUS, y + height + FRAME_RADIUS, 1000).endVertex();
		buffer.pos(x + width + FRAME_RADIUS, y + height + FRAME_RADIUS, 1000).endVertex();

		buffer.pos(x + width + FRAME_RADIUS, y, 1000).endVertex();
		buffer.pos(x + width, y, 1000).endVertex();
		buffer.pos(x + width, y + height, 1000).endVertex();
		buffer.pos(x + width + FRAME_RADIUS, y + height, 1000).endVertex();

		tess.draw();
		GlStateManager.color4f(1, 1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		GlStateManager.enableTexture();
	}

	public void renderTooltip(ItemStack stack) {
		Screen guiscr = Minecraft.getInstance().currentScreen;
		if (allowRender && canShowIn(guiscr)) {
			ContainerScreen<?> gui = (ContainerScreen<?>) guiscr;
			if (gui.getSlotUnderMouse() != null && gui.getSlotUnderMouse().getHasStack()
					&& gui.getSlotUnderMouse().getStack().equals(stack)) {
				allowRender = false;
				drawSlotOverlay((ContainerScreen<?>) guiscr);
			}
		}
	}

	private void drawSlotOverlay(ContainerScreen<?> gui) {
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableAlphaTest();
		GlStateManager.color4f(1, 1, 1, 1);

		if (!enabled || views == null || views.isEmpty())
			return;

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder renderer = tess.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.color4f(0, 0, 0, 0.5F);

		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		for (Map.Entry<Slot, SlotViewWrapper> slot : views.entrySet()) {
			if (slot.getValue().isEnableOverlay()) {
				Vec2f posvec = slot.getValue().getView().getRenderPos(guiOffsetX, guiOffsetY);
				float px = posvec.x;
				float py = posvec.y;
				renderer.pos(px + 16 + guiOffsetX, py + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
				renderer.pos(px + guiOffsetX, py + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
				renderer.pos(px + guiOffsetX, py + 16 + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
				renderer.pos(px + 16 + guiOffsetX, py + 16 + guiOffsetY, OVERLAY_ZLEVEL).endVertex();
			}
		}

		tess.draw();

		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.disableBlend();
	}

	public boolean canShowIn(Screen gui) {
		return (gui instanceof ContainerScreen<?>) && ((ContainerScreen<?>) gui).getContainer() != null && !((ContainerScreen<?>) gui).getContainer().inventorySlots.isEmpty();
	}

	private void checkSlots(ContainerScreen<?> container) {
		if (views == null) {
			views = HashBiMap.create();
		}
		else {
			views.clear();
		}
		for (Slot slot : container.getContainer().inventorySlots) {
			//System.out.println(slot);
			SlotViewWrapper wrapper;
			if(!views.containsKey(slot)){
				wrapper = new SlotViewWrapper(SlotHandler.INSTANCE.getViewSlot(container, slot));
				views.put(slot, wrapper);
			}
			else {
				wrapper = views.get(slot);
			}

			wrapper.setEnableOverlay(wrapper.getView().canSearch() && !isSearchedItem(slot.getStack()));
		}
	}

	private boolean isSearchedItem(ItemStack stack) {
		if (emptyFilter) return true;
		else if (stack.isEmpty()) return false;
		for (Object ingredient : JeiModule.filter.getFilteredIngredients()) {
			if(ItemUtils.ingredientMatches(ingredient, stack)){
				return true;
			}
        }
        return false;
    }

	public void tick() {
		final Screen screen = Minecraft.getInstance().currentScreen;
		if (!canShowIn(screen))
			return;
		if (enabled && !JeiModule.filter.getFilterText().equals(lastFilterText)) {
			lastFilterText = JeiModule.filter.getFilterText();
			emptyFilter = lastFilterText.replace(" ", "").isEmpty();
		}

		
		if (enabled && screen instanceof ContainerScreen<?>) {
			checkSlots((ContainerScreen<?>) screen);
			guiOffsetX = GuiUtils.getGuiLeft((ContainerScreen<?>)screen);
			guiOffsetY = GuiUtils.getGuiTop((ContainerScreen<?>) screen);
		}
		else if (views != null) {
			views.clear();
		}
	}

	public void toggleMode() {
		enabled = !enabled;
		if (enabled) {
			lastFilterText = JeiModule.filter.getFilterText();
			emptyFilter = lastFilterText.replace(" ", "").isEmpty();
		}
		else {
			lastFilterText = "";
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}
