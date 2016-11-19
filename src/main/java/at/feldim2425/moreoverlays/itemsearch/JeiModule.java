package at.feldim2425.moreoverlays.itemsearch;

import mezz.jei.api.*;
import mezz.jei.gui.ItemListOverlay;
import mezz.jei.gui.ItemListOverlayInternal;

import javax.annotation.Nonnull;

@JEIPlugin
public class JeiModule extends BlankModPlugin {

    public static IItemListOverlay overlay;
    private static ItemListOverlayInternal overlayInternal;

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        overlay = jeiRuntime.getItemListOverlay();
        if(overlay instanceof ItemListOverlay && ((ItemListOverlay) overlay).getInternal()!=null)
        {
            overlayInternal = ((ItemListOverlay) overlay).getInternal();
        }
    }

    public static boolean hasJEIFocus()
    {
        return overlayInternal!=null && overlayInternal.hasKeyboardFocus();
    }
}
