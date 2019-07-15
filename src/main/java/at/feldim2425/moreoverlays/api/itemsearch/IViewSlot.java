package at.feldim2425.moreoverlays.api.itemsearch;

import net.minecraft.inventory.Slot;
import org.lwjgl.util.vector.Vector2f;


public interface IViewSlot {

	/*
	 * The Slot
	 */
	Slot getSlot();

	/*
	 * Position offset for the Gui
	 */
	Vector2f getRenderPos(int guiLeft, int guiTop);

	/*
	 * false if the ItemSearch should ignore this slot
	 */
	boolean canSearch();
}
