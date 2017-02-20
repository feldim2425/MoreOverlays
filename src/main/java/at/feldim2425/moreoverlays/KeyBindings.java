package at.feldim2425.moreoverlays;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import at.feldim2425.moreoverlays.itemsearch.GuiHandler;
import at.feldim2425.moreoverlays.itemsearch.GuiRenderer;
import at.feldim2425.moreoverlays.itemsearch.JeiModule;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class KeyBindings {

    public static KeyBinding lightOverlay = new KeyBinding("key." + MoreOverlays.MOD_ID + ".lightoverlay.desc", KeyConflictContext.IN_GAME, Keyboard.KEY_F7, "key." + MoreOverlays.MOD_ID + ".category");
    public static KeyBinding chunkBounds = new KeyBinding("key." + MoreOverlays.MOD_ID + ".chunkbounds.desc", KeyConflictContext.IN_GAME, Keyboard.KEY_F9, "key." + MoreOverlays.MOD_ID + ".category");
    public static KeyBinding invSearch = new KeyBinding("key." + MoreOverlays.MOD_ID + ".invsearch.desc", KeyConflictContext.GUI, Keyboard.KEY_Z, "key." + MoreOverlays.MOD_ID + ".category");

    public static void init() {
        ClientRegistry.registerKeyBinding(lightOverlay);
        ClientRegistry.registerKeyBinding(chunkBounds);
        ClientRegistry.registerKeyBinding(invSearch);

        MinecraftForge.EVENT_BUS.register(new KeyBindings());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(receiveCanceled = true)
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if (lightOverlay.isPressed()) {
            LightOverlayHandler.toggleMode();
        }

        if (chunkBounds.isPressed()) {
            ChunkBoundsHandler.toggleMode();
        }

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGuiKeyEvent(GuiScreenEvent.KeyboardInputEvent.Post event) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if(invSearch.isActiveAndMatches(Keyboard.getEventKey()) && Keyboard.getEventKeyState() && Proxy.isJeiInstalled() &&
                (screen instanceof GuiContainer) && !(screen instanceof GuiContainerCreative) && !checkFocus(screen)){
            GuiRenderer.INSTANCE.toggleMode();
        }
    }

    private static boolean checkFocus(GuiScreen gui){
        //Check JEI Filter Focus
        if(Proxy.isJeiInstalled() && JeiModule.hasJEIFocus())
            return true;

        /*
         * Check Gui Textfield focus
         * It checks every Field in the class if it contains a GuiTextField or a Object that is a instance of GuiTextField
         * Then it makes them Accessible and check if it is focused or not.
         * It should work with every Container. Also with GuiContainers from other mods
         */
        Field[] fields = gui.getClass().getDeclaredFields();
        for(Field field : fields){
            if(GuiTextField.class.isAssignableFrom(field.getType())){
                try {
                    field.setAccessible(true);
                    Object textField = field.get(gui);
                    if(textField !=null && ((GuiTextField)textField).isFocused()) return true;
                } catch (IllegalAccessException ignored) {}
            }
        }

        return false;
    }
}
