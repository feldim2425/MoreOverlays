package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.MoreOverlays;
import mezz.jei.api.*;
import mezz.jei.gui.overlay.ItemListOverlay;
import net.minecraft.client.gui.GuiTextField;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

@JEIPlugin
public class JeiModule extends BlankModPlugin {

    public static IItemListOverlay overlay;
    private static ItemListOverlay overlayInternal;
    private static GuiTextField textField;

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        overlay = jeiRuntime.getItemListOverlay();
        updateModule();
    }

    public static void updateModule()
    {
        if(overlay instanceof ItemListOverlay) {
            overlayInternal = ((ItemListOverlay) overlay);
            try {
                Field searchField = ItemListOverlay.class.getDeclaredField("searchField");
                searchField.setAccessible(true);
                textField = (GuiTextField) searchField.get(overlayInternal);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                MoreOverlays.logger.error("Something went wrong. Tried to load JEI Search Text Field object");
                e.printStackTrace();
            }
        }
        else {
            overlayInternal = null;
            textField = null;
        }
    }

    public static boolean hasJEIFocus()
    {
        return overlayInternal!=null && overlayInternal.hasKeyboardFocus();
    }

    public static GuiTextField getJEITextField()
    {
        return textField;
    }
}
