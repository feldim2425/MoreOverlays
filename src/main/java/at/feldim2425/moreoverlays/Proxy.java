package at.feldim2425.moreoverlays;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import at.feldim2425.moreoverlays.itemsearch.GuiHandler;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;
import net.minecraftforge.fml.common.Loader;

public class Proxy {

    private static boolean enable_jei=false;

    public void preInit(){
        enable_jei = Loader.isModLoaded("JEI");

        KeyBindings.init();

        LightOverlayHandler.init();
        ChunkBoundsHandler.init();
        GuiHandler.init();
    }

    public void init(){

    }

    public void postInit(){

    }

    public static boolean isJeiInstalled(){
        return enable_jei;
    }
}
