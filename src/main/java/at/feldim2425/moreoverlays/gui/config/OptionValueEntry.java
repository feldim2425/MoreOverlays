package at.feldim2425.moreoverlays.gui.config;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.blaze3d.platform.GlStateManager;

import org.apache.logging.log4j.Level;

import at.feldim2425.moreoverlays.MoreOverlays;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class OptionValueEntry<V> extends ConfigOptionList.OptionEntry {
    protected final ForgeConfigSpec.ConfigValue<V> value;
    protected Button btnReset;
    protected Button btnUndo;
    protected int renderPosX;
    protected int renderPosY;
    protected Supplier<V> default_supplier;

    @SuppressWarnings("unchecked")
    public OptionValueEntry(ConfigOptionList list, ForgeConfigSpec.ConfigValue<V> valSpec) {
        super(list);
        this.value = valSpec;
        btnReset = new Button(list.getRowWidth() - 20, 0, 20, 20, ConfigOptionList.RESET_CHAR,
                (btn) -> this.resetSetting());
        btnUndo = new Button(list.getRowWidth() - 42, 0, 20, 20, ConfigOptionList.UNDO_CHAR,
                (btn) -> this.undoEditing());

        try {
            Field confValue_defaultSupplier = ForgeConfigSpec.ConfigValue.class.getDeclaredField("defaultSupplier");
            confValue_defaultSupplier.setAccessible(true);
            Object defaultSupplier_raw = confValue_defaultSupplier.get(this.value);
            if(defaultSupplier_raw instanceof Supplier){
                default_supplier = (Supplier<V>) defaultSupplier_raw;
            }
            else {
                btnReset.active = false;
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            MoreOverlays.logger.log(Level.WARN,
                    "Could not reflect defaultSupplier field from ConfigValue class! Reset function disabled.",e);
            btnReset.active = false;
        }
    }

    @Override
    public void render(int itemindex, int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX, int mouseY,
            boolean mouseOver, float partialTick) {
        super.render(itemindex, rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY, mouseOver, partialTick);
        renderPosX = rowLeft;
        renderPosY = rowTop;
        mouseX -= rowLeft;
        mouseY -= rowTop;
        GlStateManager.translatef(rowLeft, rowTop, 0);
        renderControls(rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY, mouseOver, partialTick);
        btnReset.render(mouseX, mouseY, partialTick);
        btnUndo.render(mouseX, mouseY, partialTick);
        
        GlStateManager.translatef(-rowLeft, -rowTop, 0);
        
        mouseX += rowLeft;
        mouseY += rowTop;

        
    }

    protected abstract void renderControls(int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX,
            int mouseY, boolean mouseOver, float partialTick);


    @Override
    protected void renderTooltip(int rowTop, int rowLeft, int rowWidth, int itemHeight, int mouseX, int mouseY) {
        super.renderTooltip(rowTop, rowLeft, rowWidth, itemHeight, mouseX, mouseY);
        if(btnReset.isHovered()){
            this.getConfigOptionList().getScreen().renderTooltip("Reset Settings", mouseX, mouseY);
        }
        if(btnUndo.isHovered()){
            this.getConfigOptionList().getScreen().renderTooltip("Undo", mouseX , mouseY);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
    }

    protected abstract void overrideUnsaved(V value);

    protected void undoEditing(){
        this.overrideUnsaved(this.value.get());
    }

    protected void resetSetting() {
        if(this.default_supplier != null){
            final V value = this.default_supplier.get();
            this.value.set(value);
            this.overrideUnsaved(value);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.btnReset.mouseClicked(mouseX - renderPosX, mouseY - renderPosY, button)){
            return true;
        }

        if(this.btnUndo.mouseClicked(mouseX - renderPosX, mouseY - renderPosY, button)){
            return true;
        }

        return false;
    }

    @Override
    public List<? extends IGuiEventListener> children() {
        return Arrays.asList(this.btnReset);
    }
}