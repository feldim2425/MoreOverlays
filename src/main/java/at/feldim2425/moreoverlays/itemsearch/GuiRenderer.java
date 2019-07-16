package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.api.itemsearch.IViewSlot;
import at.feldim2425.moreoverlays.api.itemsearch.SlotHandler;
import at.feldim2425.moreoverlays.api.itemsearch.SlotViewWrapper;
import at.feldim2425.moreoverlays.config.Config;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mezz.jei.plugins.vanilla.ingredients.enchant.EnchantDataHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.lang.reflect.Field;
import java.util.Map;

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

	public void guiInit(GuiScreen gui) {
		if (!canShowIn(gui)) {
			return;
		}

		guiOffsetX = GuiUtils.getGuiLeft((GuiContainer) gui);
		guiOffsetY = GuiUtils.getGuiTop((GuiContainer) gui);

	}

	public void guiOpen(GuiScreen gui) {

	}

	public void preDraw() {
		GuiScreen guiscr = Minecraft.getMinecraft().currentScreen;

		GuiTextField textField = JeiModule.getJEITextField();

		if (canShowIn(guiscr)) {
			allowRender = true;
			if (textField != null && enabled) {
				drawSearchFrame(textField);
			}
		}
	}

	public void postDraw() {
		GuiScreen guiscr = Minecraft.getMinecraft().currentScreen;

		if (allowRender && canShowIn(guiscr)) {
			allowRender = false;
			drawSlotOverlay((GuiContainer) guiscr);
		}
	}

	private void drawSearchFrame(GuiTextField textField) {
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableAlpha();
		GlStateManager.enableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.pushMatrix();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();
		GlStateManager.color(1, 1, 0, 1);

		float x = textField.x + 2;
		float y = textField.y + 2;
		float width = textField.width - 4;
		float height = textField.height - 4;

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
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		GlStateManager.enableTexture2D();
	}

	public void renderTooltip(ItemStack stack) {
		GuiScreen guiscr = Minecraft.getMinecraft().currentScreen;
		if (allowRender && canShowIn(guiscr)) {
			GuiContainer gui = (GuiContainer) guiscr;
			if (gui.getSlotUnderMouse() != null && gui.getSlotUnderMouse().getHasStack() && gui.getSlotUnderMouse().getStack().equals(stack)) {
				allowRender = false;
				drawSlotOverlay((GuiContainer) guiscr);
			}
		}
	}

	private void drawSlotOverlay(GuiContainer gui) {
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 1);

		if (!enabled || views == null || views.isEmpty())
			return;

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder renderer = tess.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.color(0, 0, 0, 0.5F);

		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		for (Map.Entry<Slot, SlotViewWrapper> slot : views.entrySet()) {
			if(slot.getValue().isEnableOverlay()) {
				Vector2f posvec = slot.getValue().getView().getRenderPos(guiOffsetX, guiOffsetY);
				float px = posvec.x;
				float py = posvec.y;
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

		GlStateManager.disableBlend();
	}

	public boolean canShowIn(GuiScreen gui) {
		return (gui instanceof GuiContainer) && ((GuiContainer) gui).inventorySlots != null && !((GuiContainer) gui).inventorySlots.inventorySlots.isEmpty();
	}

	private void checkSlots(GuiContainer container) {
		if (views == null) {
			views = HashBiMap.create();
		}
		else {
			views.clear();
		}
		for (Slot slot : container.inventorySlots.inventorySlots) {
			//System.out.println(slot);
			SlotViewWrapper wrapper;
			if(!views.containsKey(slot)){
				wrapper = new SlotViewWrapper(SlotHandler.INSTANCE.getViewSlot(container, GuiUtils.getCreativeSlot(slot)));
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
		if (!canShowIn(Minecraft.getMinecraft().currentScreen))
			return;
		if (enabled && !JeiModule.filter.getFilterText().equals(lastFilterText)) {
			lastFilterText = JeiModule.filter.getFilterText();
			emptyFilter = lastFilterText.replace(" ", "").isEmpty();
		}

		if (enabled && Minecraft.getMinecraft().currentScreen instanceof GuiContainer) {
			checkSlots((GuiContainer) Minecraft.getMinecraft().currentScreen);
			guiOffsetX = GuiUtils.getGuiLeft((GuiContainer) Minecraft.getMinecraft().currentScreen);
			guiOffsetY = GuiUtils.getGuiTop((GuiContainer) Minecraft.getMinecraft().currentScreen);
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
