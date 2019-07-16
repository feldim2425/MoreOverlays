package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.MoreOverlays;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

@JEIPlugin
public class JeiModule implements IModPlugin {

	public static IIngredientListOverlay overlay;
	public static IIngredientFilter filter;
	private static ISubtypeRegistry subtypes;
	private static IngredientListOverlay overlayInternal;
	private static GuiTextField textField;

	public static void updateModule() {
		if (overlay instanceof IngredientListOverlay) {
			overlayInternal = ((IngredientListOverlay) overlay);
			try {
				Field searchField = IngredientListOverlay.class.getDeclaredField("searchField");
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

	public static boolean hasJEIFocus() {
		return overlayInternal != null && overlayInternal.hasKeyboardFocus();
	}

	public static GuiTextField getJEITextField() {
		return textField;
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		overlay = jeiRuntime.getIngredientListOverlay();
		filter = jeiRuntime.getIngredientFilter();
		updateModule();
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry){
		subtypes = subtypeRegistry;
	}

	public static boolean areItemsEqualInterpreter(ItemStack stack1, ItemStack stack2){
		if(subtypes == null){
			return false;
		}

		String info1 = subtypes.getSubtypeInfo(stack1);
		String info2 = subtypes.getSubtypeInfo(stack2);
		if(info1 == null || info2 == null){
			return false;
		}
		else {
			return info1.equals(info2);
		}
	}
}
