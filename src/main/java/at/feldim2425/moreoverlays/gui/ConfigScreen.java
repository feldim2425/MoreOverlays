package at.feldim2425.moreoverlays.gui;

import java.util.ArrayList;
import java.util.List;

import at.feldim2425.moreoverlays.gui.config.ConfigOptionList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigScreen extends Screen {

    private final String modId;
    private ForgeConfigSpec configSpec;
    private ConfigOptionList optionList;
    private String categoryTitle = null;
    private List<String> pathCache = new ArrayList<>();
    

    public ConfigScreen(ForgeConfigSpec spec, String modId) {
        super(new TranslationTextComponent("gui.config."+modId+".tile"));
        this.configSpec = spec;
        this.modId = modId;
    }

    @Override
    protected void init() {
        this.optionList = new ConfigOptionList(this.minecraft, this.modId, this);
        this.children.add(this.optionList);
        if(pathCache.isEmpty()){
            this.optionList.setConfiguration(configSpec);
        }
        else {
            this.optionList.setConfiguration(configSpec, this.pathCache);
        }
    }
    
    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.optionList.render(p_render_1_, p_render_2_, p_render_3_);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
        if(this.categoryTitle != null){
            this.drawCenteredString(this.font, this.categoryTitle, this.width / 2, 24, 16777215);
        }
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }
    
    public void updatePath(final List<String> newPath){
        final String key = this.optionList.categoryTitleKey(newPath);
        if(key == null){
            this.categoryTitle = null;
        }
        else {
            this.categoryTitle = I18n.format(key);
        }

        pathCache.clear();
        pathCache.addAll(newPath);
    }

    @Override
    public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_) {
        if(key == 256 && !this.optionList.getCurrentPath().isEmpty()){
            this.optionList.pop();
            return true;
        }
        else {
            return super.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
        }
    }
}