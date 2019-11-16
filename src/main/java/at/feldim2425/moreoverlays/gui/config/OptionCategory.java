package at.feldim2425.moreoverlays.gui.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;

public class OptionCategory extends ConfigOptionList.OptionEntry {

    private List<String> tooltip;
    private Button btnOpen;
    private int offX;
    private int offY;

    public OptionCategory(ConfigOptionList list, List<String> path, String name, String comment){
        super(list);
        btnOpen = new Button(0, 0, this.getConfigOptionList().getRowWidth() - 4, 20, name, (btn) -> {
            list.push(path);
        });

        tooltip = new ArrayList<>(2);
        tooltip.add(ChatFormatting.RED.toString() + name);
        if(comment != null){
            tooltip.add(ChatFormatting.YELLOW + comment);
        }
    }

    @Override
    public void render(int itemindex, int rowTop, int rowLeft, int rowWidth, int itemHeight,int mouseX, int mouseY, boolean mouseOver, float partialTick) {
        super.render(itemindex, rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY, mouseOver, partialTick);
        offX = rowLeft;
        offY = rowTop;
        GlStateManager.translatef(rowLeft, rowTop, 0);
        btnOpen.render(mouseX - rowLeft, mouseY - rowTop, partialTick);
        GlStateManager.translatef(-rowLeft, -rowTop, 0);
    }

    protected void renderTooltip(int rowTop, int rowLeft, int rowWidth, int itemHeight,int mouseX, int mouseY){
        this.getConfigOptionList().getScreen().renderTooltip(tooltip, mouseX, mouseY);
            
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.btnOpen.mouseClicked(mouseX - offX, mouseY - offY, button);
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        return Arrays.asList(this.btnOpen);
    }

    
}