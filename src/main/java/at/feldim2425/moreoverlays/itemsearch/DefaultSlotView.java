package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.api.itemsearch.IViewSlot;
import net.minecraft.inventory.Slot;

public class DefaultSlotView implements IViewSlot {

    private Slot slot;

    public DefaultSlotView(Slot slot) {
        this.slot=slot;
    }

    @Override
    public Slot getSlot() {
        return slot;
    }

    @Override
    public int getRenderPosX(int guiLeft, int guiTop) {
        return slot.xDisplayPosition;
    }

    @Override
    public int getRenderPosY(int guiLeft, int guiTop) {
        return slot.yDisplayPosition;
    }

    @Override
    public boolean canSearch() {
        return true;
    }
}
