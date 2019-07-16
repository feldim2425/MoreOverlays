package at.feldim2425.moreoverlays.itemsearch;

import at.feldim2425.moreoverlays.MoreOverlays;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.*;

public class GuiUtils {

	private static Field fieldLeft;
	private static Field fieldTop;

	public static void initUtil(){
		try {
			fieldLeft = ReflectionHelper.findField(GuiContainer.class, "field_147003_i", "guiLeft");
			fieldLeft.setAccessible(true);

			fieldTop = ReflectionHelper.findField(GuiContainer.class, "field_147009_r", "guiTop");
			fieldTop.setAccessible(true);
		} catch (ReflectionHelper.UnableToFindFieldException e) {
			MoreOverlays.logger.error("Tried to load gui coordinate fields for reflection");
			e.printStackTrace();
			fieldTop = null;
			fieldLeft = null;
		}
	}

	public static int getGuiTop(GuiContainer container){
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

	public static int getGuiLeft(GuiContainer container){
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
