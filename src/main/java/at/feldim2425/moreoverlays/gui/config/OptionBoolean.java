package at.feldim2425.moreoverlays.gui.config;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.common.ForgeConfigSpec;

public class OptionBoolean
        extends OptionValueEntry<Boolean> {

    private Button btnChange;
    private boolean state;
    
    public OptionBoolean(ConfigOptionList list, ForgeConfigSpec.BooleanValue valSpec, ForgeConfigSpec.ValueSpec spec) {
		super(list, valSpec, spec);
        this.showValidity = false;
        
        btnChange = new Button(OptionValueEntry.TITLE_WIDTH + 5, 0,this.getConfigOptionList().getRowWidth() - OptionValueEntry.TITLE_WIDTH - 5 - OptionValueEntry.CONTROL_WIDTH_VALIDATOR,20, "", this::buttonPressed);
        this.overrideUnsaved(this.value.get());
	}

	@Override
    protected void renderControls(int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX, int mouseY,
            boolean mouseOver, float partialTick) {
        super.renderControls(rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY, mouseOver, partialTick);
        this.btnChange.render(mouseX, mouseY, 0);
    }

    @Override
    protected void overrideUnsaved(Boolean value) {
        this.state = value;
        if(this.state){
            this.btnChange.setMessage(ChatFormatting.GREEN + "TRUE");
        }
        else {
            this.btnChange.setMessage(ChatFormatting.RED + "FALSE");
        }
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        List<IGuiEventListener> childs = new ArrayList<>(super.children());
        childs.add(this.btnChange);
        return childs;
    }

    private void buttonPressed(Button btn){
        this.overrideUnsaved(!this.state);
        this.updateValue(this.state);
    }
    
}