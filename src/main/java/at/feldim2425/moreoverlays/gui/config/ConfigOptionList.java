package at.feldim2425.moreoverlays.gui.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;

import at.feldim2425.moreoverlays.MoreOverlays;
import at.feldim2425.moreoverlays.gui.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ConfigOptionList extends AbstractOptionList<ConfigOptionList.OptionEntry> {

    public static final String UNDO_CHAR = "\u21B6";
    public static final String RESET_CHAR = "\u2604";
    public static final String VALID = "\u2714";
    public static final String INVALID = "\u2715";
    private static final int ITEM_HEIGHT = 20;

    private final ConfigScreen parent;
    private final String modId;

    private ForgeConfigSpec rootConfig;
    private List<String> configPath = Collections.emptyList();
    private Map<String, Object> currentMap;
    private CommentedConfig comments;

    public static List<String> splitPath(String path) {
        return Arrays.asList(path.split("\\."));
    }

    public ConfigOptionList(Minecraft minecraft, String modId, ConfigScreen configs) {
        // Width, Height, Y-Start, Y-End, item_height
        super(minecraft, configs.width, configs.height, 43, configs.height - 32, ITEM_HEIGHT);
        this.parent = configs;
        this.modId = modId;
    }

    public ConfigScreen getScreen() {
        return this.parent;
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15 + 20;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    @Override
    protected void renderDecorations(int p_renderDecorations_1_, int p_renderDecorations_2_) {
        int i = this.getItemCount();
        for(int j = 0; j < i; ++j) {
            int k = this.getRowTop(j);
            int l = this.getRowTop(j) + ITEM_HEIGHT;
            if (l >= this.y0 && k <= this.y1) {
                ConfigOptionList.OptionEntry e = this.getEntry(j);
                e.runRenderTooltip();
            }
        }
    }

    public String categoryTitleKey(List<String> path) {
        if (path.isEmpty()) {
            return null;
        }
        return "config." + this.modId + ".category." + path.stream().collect(Collectors.joining("."));
    }

    public void setConfiguration(ForgeConfigSpec rootConfig) {
        this.setConfiguration(rootConfig, Collections.emptyList());
    }

    public void setConfiguration(ForgeConfigSpec rootConfig, List<String> path) {
        this.rootConfig = rootConfig;
        try {
            final Field forgeconfigspec_childconfig = ForgeConfigSpec.class.getDeclaredField("childConfig");
            forgeconfigspec_childconfig.setAccessible(true);
            final Object childConfig_raw = forgeconfigspec_childconfig.get(rootConfig);
            if(childConfig_raw instanceof CommentedConfig){
                this.comments = (CommentedConfig) childConfig_raw;
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            MoreOverlays.logger.warn("Couldn't reflect childConfig from ForgeConfigSpec! Comments will be missing.", e);
        }
        this.updatePath(path);
    }

    private void setPath(List<String> path) {
        Object val;
        if (path.isEmpty()) {
            val = this.rootConfig.getValues();
        } else {
            val = this.rootConfig.getValues().getRaw(path);
        }

        if (val instanceof UnmodifiableConfig) {
            this.configPath = path;
            this.currentMap = ((UnmodifiableConfig) val).valueMap();
            this.refreshEntries();
            this.parent.updatePath(this.getCurrentPath());
        } else {
            throw new IllegalArgumentException("Path in config list has to point to another config object");
        }
    }

    public void updatePath(List<String> path) {
        this.setPath(new ArrayList<>(path));
    }

    public void push(String path) {
        this.push(splitPath(path));
    }

    public void push(List<String> path) {
        final List<String> tmp = new ArrayList<>(this.configPath.size() + path.size());
        tmp.addAll(this.configPath);
        tmp.addAll(path);
        this.setPath(tmp);
    }

    public void pop() {
        pop(1);
    }

    public void pop(int amount) {
        final List<String> tmp = new ArrayList<>(this.configPath);
        for (int i = 0; i < amount && !tmp.isEmpty(); i++) {
            tmp.remove(tmp.size() - 1);
        }
        setPath(tmp);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void refreshEntries() {
        this.clearEntries();
        for (final Map.Entry<String, Object> cEntry : this.currentMap.entrySet()) {
            final Object value = cEntry.getValue();
            final List<String> fullPath = new ArrayList<>(this.configPath.size() + 1);
            fullPath.addAll(this.configPath);
            fullPath.add(cEntry.getKey());

            String comment = null;
            if(this.comments != null){
                comment = this.comments.getComment(fullPath);
            }

            if (value instanceof UnmodifiableConfig) {
                final String name = I18n.format(categoryTitleKey(fullPath));
                this.addEntry(new OptionCategory(this, Arrays.asList(cEntry.getKey()), name, comment));
            }
            else {
                this.addEntry(new OptionGeneric(this, (ConfigValue<?>)value));
            }
        }
    }

    public List<String> getCurrentPath() {
        return Collections.unmodifiableList(this.configPath);
    }

    public ForgeConfigSpec getConfig(){
        return this.rootConfig;
    }

    public String getModId(){
        return this.modId;
    }

    public abstract static class OptionEntry extends AbstractOptionList.Entry<ConfigOptionList.OptionEntry> {
        private ConfigOptionList optionList;

        private int rowTop, rowLeft, rowWidth, itemHeight, mouseX,  mouseY;
        private boolean mouseOver;

        public OptionEntry(ConfigOptionList list) {
            this.optionList = list;
        }

        @Override
        public void render(int itemindex, int rowTop, int rowLeft, int rowWidth, int itemHeight,int mouseX, int mouseY, boolean mouseOver, float partialTick){
            this.rowTop = rowTop;
            this.rowLeft = rowLeft;
            this.rowWidth = rowWidth;
            this.itemHeight = itemHeight;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.mouseOver = mouseOver;
        }

        public void runRenderTooltip(){
            if(this.mouseOver){
                this.renderTooltip(this.rowTop, this.rowLeft, this.rowWidth, this.itemHeight, this.mouseX, this.mouseY);
            }
        }

        protected void renderTooltip(int rowTop, int rowLeft, int rowWidth, int itemHeight,int mouseX, int mouseY){
        }

        @Override
        public List<? extends IGuiEventListener> children() {
            return Collections.emptyList();
        }

        public ConfigOptionList getConfigOptionList() {
            return this.optionList;
        }
    }

}