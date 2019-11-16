package at.feldim2425.moreoverlays.gui.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class OptionGeneric<V>
        extends OptionValueEntry<V> {

    public OptionGeneric(ConfigOptionList list, ForgeConfigSpec.ConfigValue<V> valSpec) {
		super(list, valSpec);
		//TODO Auto-generated constructor stub
	}

	@Override
    protected void renderControls(int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX, int mouseY,
            boolean mouseOver, float partialTick) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void overrideUnsaved(V value) {
        // TODO Auto-generated method stub

    }

    
}