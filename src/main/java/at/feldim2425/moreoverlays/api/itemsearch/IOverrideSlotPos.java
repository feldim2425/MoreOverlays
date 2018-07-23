package at.feldim2425.moreoverlays.api.itemsearch;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

/*
 * Can be implemented in the GuiContainer to Override the position of the slots
 * If a class implements it that is not instance of GuiContainer it has to register it
 * wit SlotHandler.INSTANCE.addPositionOverride(...);
 */
public interface IOverrideSlotPos {

	/*
	 * Get the override
	 * if the Handler cannot handle this Slot/Gui just return null
	 */
	IViewSlot getSlot(GuiContainer gui, Slot slot);
}
