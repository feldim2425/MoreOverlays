package at.feldim2425.moreoverlays.gui.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;

public class OptionCategory extends ConfigOptionList.OptionEntry {

    private List<String> tooltip;
    private Button btnOpen;

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
    public void renderControls(int rowTop, int rowLeft, int rowWidth, int itemHeight,int mouseX, int mouseY, boolean mouseOver, float partialTick) {
        btnOpen.render(mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderTooltip(int rowTop, int rowLeft, int rowWidth, int itemHeight,int mouseX, int mouseY){
        this.getConfigOptionList().getScreen().renderTooltip(tooltip, mouseX, mouseY);
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        return Arrays.asList(this.btnOpen);
    }
}