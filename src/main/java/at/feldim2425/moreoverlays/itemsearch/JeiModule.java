package at.feldim2425.moreoverlays.itemsearch;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import at.feldim2425.moreoverlays.MoreOverlays;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.runtime.IIngredientFilter;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JeiModule implements IModPlugin {

	public static IIngredientListOverlay overlay;
	public static IIngredientFilter filter;
	private static IJeiHelpers jeiHelpers;
	private static IngredientListOverlay overlayInternal;
	private static TextFieldWidget textField;

	public static void updateModule() {
		if (overlay instanceof IngredientListOverlay) {
			overlayInternal = ((IngredientListOverlay) overlay);
			try {
				Field searchField = IngredientListOverlay.class.getDeclaredField("searchField");
				searchField.setAccessible(true);
				textField = (TextFieldWidget) searchField.get(overlayInternal);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				MoreOverlays.logger.error("Something went wrong. Tried to load JEI Search Text Field object");
				e.printStackTrace();
			}
		} else {
			overlayInternal = null;
			textField = null;
		}
	}

	public static TextFieldWidget getJEITextField() {
		return textField;
	}

	@Override
	public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		overlay = jeiRuntime.getIngredientListOverlay();
		filter = jeiRuntime.getIngredientFilter();
		updateModule();
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
		jeiHelpers = registration.getJeiHelpers();
	}

	public static boolean areItemsEqualInterpreter(ItemStack stack1, ItemStack stack2) {
		if (jeiHelpers == null) {
			return ItemUtils.matchNBT(stack1, stack2);
		}
		return jeiHelpers.getStackHelper().isEquivalent(stack1, stack2);

		/*
		String info1 = subtypes.getSubtypeInfo(stack1);
		String info2 = subtypes.getSubtypeInfo(stack2);
		if (info1 == null || info2 == null) {
			return ItemUtils.matchNBT(stack1, stack2);
		} else {
			return info1.equals(info2);
		}*/
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(MoreOverlays.MOD_ID, "jei_module");
	}
}
