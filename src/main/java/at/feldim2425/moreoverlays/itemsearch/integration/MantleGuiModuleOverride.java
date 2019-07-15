package at.feldim2425.moreoverlays.itemsearch.integration;

import at.feldim2425.moreoverlays.api.itemsearch.IOverrideSlotPos;
import at.feldim2425.moreoverlays.api.itemsearch.IViewSlot;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.lwjgl.util.vector.Vector2f;
import slimeknights.mantle.client.gui.GuiMultiModule;

public class MantleGuiModuleOverride implements IOverrideSlotPos {

	@Override
	public IViewSlot getSlot(GuiContainer gui, Slot slot) {
		if (gui instanceof GuiMultiModule) {
			return new GuiModuleSlotView(slot, (GuiMultiModule) gui);
		}
		return null;
	}

	public static class GuiModuleSlotView implements IViewSlot {

		private Slot slot;
		private GuiMultiModule gui;

		public GuiModuleSlotView(Slot slot, GuiMultiModule gui) {
			this.slot = slot;
			this.gui = gui;
		}

		@Override
		public Slot getSlot() {
			return slot;
		}

		@Override
		public Vector2f getRenderPos(int guiLeft, int guiTop) {
			return new Vector2f(-guiLeft + gui.cornerX + slot.xPos, -guiTop + gui.cornerY + slot.yPos);
		}

		@Override
		public boolean canSearch() {
			return slot.inventory.getSizeInventory() > slot.getSlotIndex();
		}
	}
}
