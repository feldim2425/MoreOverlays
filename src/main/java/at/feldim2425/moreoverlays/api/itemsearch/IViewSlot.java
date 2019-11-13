package at.feldim2425.moreoverlays.api.itemsearch;

import net.minecraft.inventory.container.Slot;
import net.minecraft.util.math.Vec2f;

public interface IViewSlot {

	/*
	 * The Slot
	 */
	Slot getSlot();

	/*
	 * Position offset for the Gui
	 */
	Vec2f getRenderPos(int guiLeft, int guiTop);

	/*
	 * false if the ItemSearch should ignore this slot
	 */
	boolean canSearch();
}
