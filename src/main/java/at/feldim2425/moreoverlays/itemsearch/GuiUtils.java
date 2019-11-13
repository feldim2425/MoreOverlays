package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.MoreOverlays;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import java.lang.reflect.Field;

public class GuiUtils {

	private static Field fieldLeft;
	private static Field fieldTop;

	public static void initUtil(){
		try {
			fieldLeft = ObfuscationReflectionHelper.findField(ContainerScreen.class, "field_147003_i");
			fieldLeft.setAccessible(true);

			fieldTop = ObfuscationReflectionHelper.findField(ContainerScreen.class, "field_147009_r");
			fieldTop.setAccessible(true);
		} catch (ObfuscationReflectionHelper.UnableToFindFieldException e) {
			MoreOverlays.logger.error("Tried to load gui coordinate fields for reflection");
			e.printStackTrace();
			fieldTop = null;
			fieldLeft = null;
		}
	}

	public static int getGuiTop(ContainerScreen<?> container){
		if(fieldTop == null){
			return 0;
		}

		try {
			return fieldTop.getInt(container);
		}catch(IllegalAccessException ignore){
			// EMPTY
		}
		return 0;
	}

	public static int getGuiLeft(ContainerScreen<?> container){
		if(fieldLeft == null){
			return 0;
		}

		try {
			return fieldLeft.getInt(container);
		}catch(IllegalAccessException ignore){
			// EMPTY
		}
		return 0;
	}
}
