package at.feldim2425.moreoverlays.gui.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;

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

        String[] lines = null;
        if(comment != null){
            lines = comment.split("\\n");
        }

        tooltip = new ArrayList<>(lines.length + 1);
        tooltip.add(ChatFormatting.RED.toString() + name);
        for(final String line : lines){
            tooltip.add(ChatFormatting.YELLOW.toString() + line);
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
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.btnOpen.mouseClicked(mouseX - offX, mouseY - offY, button);
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        return Arrays.asList(this.btnOpen);
    }

    
}