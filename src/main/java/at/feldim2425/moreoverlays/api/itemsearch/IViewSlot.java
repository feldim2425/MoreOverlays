package at.feldim2425.moreoverlays.api.itemsearch;

import net.minecraft.inventory.Slot;

public interface IViewSlot {

    /*
     * The Slot
     */
    Slot getSlot();

    /*
     * X Position relative to guiLeft
     */
    int getRenderPosX(int guiLeft, int guiTop);

    /*
     * Y Position relative to guiTop
     */
    int getRenderPosY(int guiLeft, int guiTop);

    /*
     * false if the ItemSearch should ignore this slot
     */
    boolean canSearch();
}
