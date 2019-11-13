package at.feldim2425.moreoverlays;

import org.lwjgl.glfw.GLFW;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {

	public static KeyBinding lightOverlay = new KeyBinding("key." + MoreOverlays.MOD_ID + ".lightoverlay.desc", KeyConflictContext.IN_GAME, mappedKey(GLFW.GLFW_KEY_F7), "key." + MoreOverlays.MOD_ID + ".category");
	public static KeyBinding chunkBounds = new KeyBinding("key." + MoreOverlays.MOD_ID + ".chunkbounds.desc", KeyConflictContext.IN_GAME, mappedKey(GLFW.GLFW_KEY_F9), "key." + MoreOverlays.MOD_ID + ".category");

	private static InputMappings.Input mappedKey(int key){
		return InputMappings.Type.KEYSYM.getOrMakeInput(key);
	}

	public static void init() {
		ClientRegistry.registerKeyBinding(lightOverlay);
		ClientRegistry.registerKeyBinding(chunkBounds);

		MinecraftForge.EVENT_BUS.register(new KeyBindings());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(receiveCanceled = true)
	public void onKeyEvent(InputEvent.KeyInputEvent event) {
		if (lightOverlay.isPressed()) {
			LightOverlayHandler.setEnabled(!LightOverlayHandler.isEnabled());
		}

		if (chunkBounds.isPressed()) {
			ChunkBoundsHandler.toggleMode();
		}
	}
}
