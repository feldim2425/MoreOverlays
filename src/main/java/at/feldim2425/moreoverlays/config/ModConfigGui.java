package at.feldim2425.moreoverlays.config;

import at.feldim2425.moreoverlays.MoreOverlays;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ModConfigGui extends GuiConfig {
    public ModConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), MoreOverlays.MOD_ID, false, false, I18n.translateToLocal("gui.config."+ MoreOverlays.MOD_ID+".tile"));
    }

    private static List<IConfigElement> getConfigElements(){
        List<IConfigElement> elements = new ArrayList<>();
        for(String category : ConfigHandler.categories){
            elements.add(new ConfigElement(ConfigHandler.config.getCategory(category).setLanguageKey("config."+MoreOverlays.MOD_ID+".category."+category)));
        }
        return elements;
    }
}
