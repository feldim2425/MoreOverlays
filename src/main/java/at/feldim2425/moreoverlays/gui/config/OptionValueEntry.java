package at.feldim2425.moreoverlays.gui.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.gui.ChatFormatting;

import at.feldim2425.moreoverlays.MoreOverlays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class OptionValueEntry<V> extends ConfigOptionList.OptionEntry {

    public static final int CONTROL_WIDTH_NOVALIDATOR = 44;
    public static final int CONTROL_WIDTH_VALIDATOR = 64;
    public static final int TITLE_WIDTH = 80;

    private List<String> tooltip;
    private String txtUndo = "";
    private String txtReset = "";
    private String name = "";

    protected final ForgeConfigSpec.ConfigValue<V> value;
    protected final ForgeConfigSpec.ValueSpec spec;
    protected Button btnReset;
    protected Button btnUndo;
    protected V defaultValue;
    protected V newValue;

    protected boolean showValidity = false;
    private boolean valid = false;
    private boolean changes = false;

    @SuppressWarnings("unchecked")
    public OptionValueEntry(ConfigOptionList list, ForgeConfigSpec.ConfigValue<V> confValue, ForgeConfigSpec.ValueSpec spec) {
        super(list);
        this.value = confValue;
        this.spec = spec;
        this.btnReset = new Button(list.getRowWidth() - 20, 0, 20, 20, ConfigOptionList.RESET_CHAR,
                (btn) -> this.reset());
        this.btnUndo = new Button(list.getRowWidth() - 42, 0, 20, 20, ConfigOptionList.UNDO_CHAR,
                (btn) -> this.undo());

        this.txtReset = I18n.format("gui.config." + MoreOverlays.MOD_ID + ".reset_config");
        this.txtUndo = I18n.format("gui.config." + MoreOverlays.MOD_ID + ".undo");

        final Object defaultVal = this.spec.getDefault();
        if(defaultVal != null && spec.getClazz().isAssignableFrom(defaultVal.getClass())){
            this.defaultValue = (V) defaultVal;
        }
        else {
            btnReset.active = false;
        }

        this.name = this.value.getPath().get(this.value.getPath().size()-1);

        String[] lines = null;
        if(this.spec.getComment() != null){
            lines = this.spec.getComment().split("\\n");
        }

        tooltip = new ArrayList<>(lines.length + 1);
        tooltip.add(ChatFormatting.RED.toString() + this.name);
        for(final String line : lines){
            tooltip.add(ChatFormatting.YELLOW.toString() + line);
        }

        this.updateValue(this.value.get());
    }

    @Override
    protected void renderControls(int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX,
            int mouseY, boolean mouseOver, float partialTick){
        this.getConfigOptionList().getScreen().drawRightAlignedString(Minecraft.getInstance().fontRenderer, this.name, TITLE_WIDTH, 6, 0xFFFFFF);
        this.btnReset.render(mouseX, mouseY, partialTick);
        this.btnUndo.render(mouseX, mouseY, partialTick);

        if(this.showValidity){
            if(this.valid){
                this.getConfigOptionList().getScreen().drawCenteredString(Minecraft.getInstance().fontRenderer, ConfigOptionList.VALID, this.getConfigOptionList().getRowWidth() - 53, 6, 0x00FF00);
            }
            else {
                this.getConfigOptionList().getScreen().drawCenteredString(Minecraft.getInstance().fontRenderer, ConfigOptionList.INVALID, this.getConfigOptionList().getRowWidth() - 53, 6, 0xFF0000);
            }
        }
    }   


    @Override
    protected void renderTooltip(int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX, int mouseY) {
        super.renderTooltip(rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY);
        if(btnReset.isHovered()){
            this.getConfigOptionList().getScreen().renderTooltip(this.txtReset, mouseX, mouseY);
        }
        else if(btnUndo.isHovered()){
            this.getConfigOptionList().getScreen().renderTooltip(this.txtUndo, mouseX , mouseY);
        }
        else if(mouseX < TITLE_WIDTH + rowLeft){
            this.getConfigOptionList().getScreen().renderTooltip(this.tooltip, mouseX , mouseY);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
    }

    protected abstract void overrideUnsaved(V value);

    protected boolean isUndoable(V current){
        return  current == null || !current.equals(this.value.get()) || !this.valid;
    }

    protected void updateValue(@Nullable V value){
        this.valid = value != null && this.spec.test(value);
        btnReset.active = isResettable();
        this.changes = isUndoable(value);
        btnUndo.active = this.changes;
        this.newValue = value;
    }

    @Override
    public void undo(){
        this.overrideUnsaved(this.value.get());
        this.updateValue(this.value.get());
    }

    @Override
    public void reset() {
        if(this.defaultValue != null){
            this.value.set(this.defaultValue);
            this.overrideUnsaved(this.defaultValue);
            this.updateValue(this.defaultValue);
        }
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        return Arrays.asList(this.btnReset, this.btnUndo);
    }

    @Override
    public boolean isValid(){
        return this.valid;
    }

    @Override
    public boolean hasChanges(){
        return this.changes;
    }

    @Override
    public boolean isResettable(){
        return this.defaultValue != null && (this.value.get() == null || !this.value.get().equals(this.defaultValue));
    }

    @Override
    public void save() {
        this.value.set(this.newValue);
        this.value.save();
    }
}