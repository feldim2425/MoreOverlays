package at.feldim2425.moreoverlays;

import at.feldim2425.moreoverlays.chunkbounds.ChunkBoundsHandler;
import at.feldim2425.moreoverlays.lightoverlay.LightOverlayHandler;

public class Proxy {

    public void preInit(){
        KeyBindings.init();

        LightOverlayHandler.init();
        ChunkBoundsHandler.init();
    }

    public void init(){

    }

    public void postInit(){

    }
}
